package dev.applecontainer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SystemCommandsTest {

    @TempDir
    Path tempDir;

    @Test
    void isRunningWhenTheStatusCommandSucceeds() throws IOException {
        assertTrue(commandsExiting(0).isRunning());
    }

    @Test
    void isNotRunningWhenTheStatusCommandFails() throws IOException {
        assertFalse(commandsExiting(1).isRunning());
    }

    @Test
    void isNotRunningWhenTheBinaryIsMissing() {
        var commands = new SystemCommands(AppleContainerCli.builder()
                .executable(tempDir.resolve("no-such-container").toString())
                .timeout(Duration.ofSeconds(5))
                .build());

        assertFalse(commands.isRunning());
    }

    private SystemCommands commandsExiting(int exitCode) throws IOException {
        var executable = Files.createFile(tempDir.resolve("container"),
                PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-xr-x")));
        Files.writeString(executable, "#!/bin/sh\nexit %d\n".formatted(exitCode));
        return new SystemCommands(AppleContainerCli.builder()
                .executable(executable.toString())
                .timeout(Duration.ofSeconds(5))
                .build());
    }
}
