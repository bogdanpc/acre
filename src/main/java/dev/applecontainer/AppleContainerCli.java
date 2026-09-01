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
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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

    public CliResult<String> run(String... args) {
        var commands = new ArrayList<String>(args.length + 1);
        commands.add(executable);
        commands.addAll(List.of(args));

        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            var p = new ProcessBuilder(commands).start();
            var stdOut = pool.submit(() -> readStream(p.getInputStream()));
            var stdErr = pool.submit(() -> readStream(p.getErrorStream()));

            if (!p.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
                p.destroyForcibly();
                return failed(args, "timed out after " + timeout);
            }
            return p.exitValue() == 0
                    ? CliResult.success(stdOut.get())
                    : failed(args, exitReason(p.exitValue(), stdOut.get(), stdErr.get()));
        } catch (IOException e) {
            return failed(args, "cannot start");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return failed(args, "interrupted");
        } catch (ExecutionException e) {
            return failed(args, "cannot read the output");
        }
    }

    /**
     * Runs the CLI
     * @param reader transform stdout text into the answer
     * @param args command and its arguments
     */
    public <T> CliResult<T> run(Function<String, T> reader, String... args) {
        return switch (run(args)) {
            case CliResult.Success<String>(var stdOut) -> CliResult.success(reader.apply(stdOut));
            case CliResult.Failure<String>(var message) -> CliResult.failure(message);
        };
    }

    /**
     * Runs the CLI with {@code --format json}
     *
     * @param reader turns the parsed tree into the answer
     * @param args the command and its arguments
     */
    public <T> CliResult<T> runJson(Function<JrsValue, T> reader, String... args) {
        var argsWithFormat = new ArrayList<String>(args.length + 2);
        argsWithFormat.addAll(List.of(args));
        argsWithFormat.add("--format");
        argsWithFormat.add("json");

        return switch (run(argsWithFormat.toArray(String[]::new))) {
            case CliResult.Success<String>(var json) -> parse(json, argsWithFormat, reader);
            case CliResult.Failure<String>(var message) -> CliResult.failure(message);
        };
    }

    private static <T> CliResult<T> parse(String json, List<String> args, Function<JrsValue, T> reader) {
        JrsValue tree;
        try {
            tree = JSON_READER.treeFrom(json);
        } catch (JacksonException e) {
            return CliResult.failure("Cannot read JSON from " + String.join(" ", args));
        }
        return CliResult.success(reader.apply(tree));
    }

    private <T> CliResult<T> failed(String[] args, String reason) {
        return CliResult.failure("%s %s: %s".formatted(executable, String.join(" ", args), reason));
    }

    private static String exitReason(int exitCode, String stdOut, String stdErr) {
        var printed = stdErr.strip();
        if (printed.isEmpty()) {
            printed = stdOut.strip();
        }
        return printed.isEmpty()
                ? "exit code %d".formatted(exitCode)
                : "exit code %d, %s".formatted(exitCode, printed);
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
