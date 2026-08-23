package dev.applecontainer;

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
    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(20);

    private final String executable;
    private final Duration timeout;

    public AppleContainerCli() {
        this(cli, DEFAULT_TIMEOUT);
    }

    AppleContainerCli(String executable, Duration timeout) {
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

    private static String readStream(InputStream stream) throws IOException {
        try (stream) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
