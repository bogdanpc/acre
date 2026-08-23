package dev.volumes;

import dev.applecontainer.AppleContainerCli;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VolumeCommandsTest {

    @TempDir
    Path tempDir;

    @Test
    void readsTheVolumeListFromTheJsonOutput() throws IOException {
        var commands = commandsPrinting("""
                [
                  {"id": "pgdata", "configuration": {
                     "name": "pgdata", "driver": "local", "format": "ext4",
                     "sizeInBytes": 549755813888,
                     "source": "/Users/me/volumes/pgdata/volume.img"}}
                ]
                """);

        var volumes = commands.list();

        assertEquals(List.of(new Volume(
                "pgdata", "local", "ext4", 549755813888L, "/Users/me/volumes/pgdata/volume.img")), volumes);
    }

    @Test
    void answersZeroWhenTheSizeIsMissing() throws IOException {
        var commands = commandsPrinting("""
                [{"configuration": {"name": "logs", "driver": "local"}}]
                """);

        assertEquals(List.of(new Volume("logs", "local", "", 0, "")), commands.list());
    }

    private VolumeCommands commandsPrinting(String json) throws IOException {
        var script = """
                #!/bin/sh
                cat <<'JSON'
                %s
                JSON
                """.formatted(json.strip());
        var executable = Files.createFile(tempDir.resolve("container"),
                PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-xr-x")));
        Files.writeString(executable, script);
        return new VolumeCommands(AppleContainerCli.builder()
                .executable(executable.toString())
                .timeout(Duration.ofSeconds(5))
                .build());
    }
}
