package dev.containers;

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

class ContainerCommandsTest {

    @TempDir
    Path tempDir;

    @Test
    void readsTheContainerListFromTheJsonOutput() throws IOException {
        var commands = commandsPrinting("""
                [
                  {
                    "id": "web-01",
                    "configuration": {
                      "image": {"reference": "docker.io/library/nginx:1.27"},
                      "resources": {"cpus": 2, "memoryInBytes": 1073741824}
                    },
                    "status": {
                      "state": "running",
                      "networks": [{"address": "192.168.64.3/24", "network": "default"}]
                    }
                  }
                ]
                """);

        var containers = commands.list();

        assertEquals(List.of(new Container(
                "web-01", "docker.io/library/nginx:1.27", "running", "192.168.64.3", 2, 1073741824L)), containers);
    }

    @Test
    void hasNoAddressWhileTheContainerIsStopped() throws IOException {
        var commands = commandsPrinting("""
                [
                  {
                    "id": "cache-01",
                    "configuration": {
                      "image": {"reference": "docker.io/library/redis:7"},
                      "resources": {"cpus": 1, "memoryInBytes": 536870912}
                    },
                    "status": {"state": "stopped", "networks": []}
                  }
                ]
                """);

        var containers = commands.list();

        assertEquals(List.of(new Container(
                "cache-01", "docker.io/library/redis:7", "stopped", "", 1, 536870912L)), containers);
    }

    @Test
    void showsTheMemoryInMegabytes() {
        assertEquals("512 MB", new Container("id", "image", "stopped", "", 1, 536870912L).memory());
    }

    @Test
    void failsWhenTheCliFails() throws IOException {
        var commands = new ContainerCommands(cliRunning("""
                #!/bin/sh
                echo "no such command" >&2
                exit 1
                """));

        var failure = assertThrows(AppleContainerCliException.class, commands::list);

        assertTrue(failure.getMessage().contains("no such command"), failure.getMessage());
    }

    private ContainerCommands commandsPrinting(String json) throws IOException {
        return new ContainerCommands(cliRunning("""
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
