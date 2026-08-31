package dev.applecontainer;

import tools.jackson.core.JacksonException;
import tools.jackson.jr.ob.JSON;
import tools.jackson.jr.stree.JrSimpleTreeExtension;
import tools.jackson.jr.stree.JrsValue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AppleContainerCli {

    static final String cli = "container";
    private static final JSON JSON_READER = JSON.builder().register(new JrSimpleTreeExtension()).build();
    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(20);

    private final String executable;
    private final Duration timeout;

    private AppleContainerCli(String executable, Duration timeout) {
        this.executable = executable;
        this.timeout = timeout;
    }

    public AppleContainerResult run(String... args) {
        var commands = new ArrayList<String>(args.length + 1);
        commands.add(executable);
        commands.addAll(List.of(args));

        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            var p = new ProcessBuilder(commands).start();
            var stdOut = pool.submit(() -> readStream(p.getInputStream()));
            var stdErr = pool.submit(() -> readStream(p.getErrorStream()));

            if (!p.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
                p.destroyForcibly();
                throw new AppleContainerCliException("Timeout %s for Apple container".formatted(timeout.toString()));
            }
            return new AppleContainerResult(p.exitValue(), stdOut.get(), stdErr.get());
        } catch (IOException e) {
            throw new AppleContainerCliException("Cannot run " + Arrays.toString(args));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AppleContainerCliException("Interrupted while trying to run " + Arrays.toString(args));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Runs the CLI and fails when it exits with an error, so the message can be shown.
     *
     * @param args the command and its arguments
     * @throws AppleContainerCliException if the CLI exited with a code other than 0
     */
    public AppleContainerResult runChecked(String... args) {
        var result = run(args);
        if (!result.isSuccess()) {
            throw new AppleContainerCliException(failureMessage(args, result));
        }
        return result;
    }

    /**
     * Runs the CLI with {@code --format json} and parses the result.
     *
     * @param args the command and its arguments
     * @throws AppleContainerCliException if the CLI failed or output is not JSON
     */
    public JrsValue runJson(String... args) {
        var argsWithFormat = new ArrayList<String>(args.length + 2);
        argsWithFormat.addAll(List.of(args));
        argsWithFormat.add("--format");
        argsWithFormat.add("json");

        var result = runChecked(argsWithFormat.toArray(String[]::new));

        try {
            return JSON_READER.treeFrom(result.stdOut());
        } catch (JacksonException e) {
            throw new AppleContainerCliException("Cannot read JSON from " + String.join(" ", argsWithFormat), e);
        }
    }

    /** Prefers what the CLI printed on stderr, then stdout, then a plain exit code. */
    private String failureMessage(String[] args, AppleContainerResult result) {
        var command = executable + " " + String.join(" ", args);
        var reason = result.stdErr().strip();
        if (reason.isEmpty()) {
            reason = result.stdOut().strip();
        }
        return reason.isEmpty()
                ? "%s failed with exit code %d".formatted(command, result.exitCode())
                : "%s failed with exit code %d: %s".formatted(command, result.exitCode(), reason);
    }

    private static String readStream(InputStream stream) throws IOException {
        try (stream) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String executable = cli;
        private Duration timeout = DEFAULT_TIMEOUT;

        private Builder() {
        }

        public Builder executable(String executable) {
            this.executable = executable;
            return this;
        }

        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public AppleContainerCli build() {
            return new AppleContainerCli(executable, timeout);
        }
    }
}
