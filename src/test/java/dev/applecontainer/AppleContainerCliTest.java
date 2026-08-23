package dev.applecontainer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class AppleContainerCliTest {

    @TempDir
    Path tempDir;

    @Test
    void passesTheArgumentsAndReturnsWhatTheCliPrinted() throws IOException {
        var fakeCli = fakeCli("""
                #!/bin/sh
                echo "called with $*"
                """);

        var result = new AppleContainerCli(fakeCli, Duration.ofSeconds(5)).run("list", "--all");

        assertTrue(result.isSuccess());
        assertEquals("called with list --all\n", result.stdOut());
        assertEquals("", result.stdErr());
    }

    private String fakeCli(String script) throws IOException {
        var executable = Files.createFile(tempDir.resolve("container"),
                PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-xr-x")));
        Files.writeString(executable, script);
        return executable.toString();
    }

}