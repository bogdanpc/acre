package dev.images;

import dev.applecontainer.AppleContainerCli;
import dev.applecontainer.AppleContainerCliException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageCommandsTest {

    @TempDir
    Path tempDir;

    @Test
    void readsTheImageListFromTheJsonOutput() throws IOException {
        var commands = commandsPrinting("""
                [
                  {"configuration": {"name": "docker.io/library/postgres:18-alpine"}},
                  {"configuration": {"name": "localhost:5000/team/tool:dev"}}
                ]
                """);

        var images = commands.list();

        assertEquals(List.of(
                new ContainerImage("docker.io/library/postgres:18-alpine"),
                new ContainerImage("localhost:5000/team/tool:dev")), images);
    }

    @Test
    void failsWhenTheCliFails() throws IOException {
        var commands = new ImageCommands(cliRunning("""
                #!/bin/sh
                echo "no such command" >&2
                exit 1
                """));

        var failure = assertThrows(AppleContainerCliException.class, commands::list);

        assertTrue(failure.getMessage().contains("no such command"), failure.getMessage());
    }

    private ImageCommands commandsPrinting(String json) throws IOException {
        return new ImageCommands(cliRunning("""
                #!/bin/sh
                cat <<'JSON'
                %s
                JSON
                """.formatted(json.strip())));
    }

    private AppleContainerCli cliRunning(String script) throws IOException {
        var executable = Files.createFile(tempDir.resolve("container"),
                PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-xr-x")));
        Files.writeString(executable, script);
        return AppleContainerCli.builder()
                .executable(executable.toString())
                .timeout(Duration.ofSeconds(5))
                .build();
    }
}
