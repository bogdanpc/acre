package dev.testing;

import dev.applecontainer.AppleContainerCli;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Duration;
import java.util.Comparator;

/**
 * Mock Apple Container CLI.
 */
public final class MockContainerCli implements BeforeEachCallback, AfterEachCallback {

    private Path folder;

    @Override
    public void beforeEach(ExtensionContext test) throws IOException {
        folder = Files.createTempDirectory("acre-fake-cli");
    }

    @Override
    public void afterEach(ExtensionContext test) throws IOException {
        try (var paths = Files.walk(folder)) {
            paths.sorted(Comparator.reverseOrder()).forEach(MockContainerCli::delete);
        }
    }

    public AppleContainerCli running(String script) {
        try {
            var executable = Files.createFile(folder.resolve("container"),
                    PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-xr-x")));
            Files.writeString(executable, script);
            return cli(executable);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public AppleContainerCli missing() {
        return cli(folder.resolve("no-such-container"));
    }

    private static AppleContainerCli cli(Path executable) {
        return AppleContainerCli.builder()
                .executable(executable.toString())
                .timeout(Duration.ofSeconds(5))
                .build();
    }

    private static void delete(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
