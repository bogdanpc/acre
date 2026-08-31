package dev.volumes;

import dev.testing.MockContainerCli;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VolumeCommandsTest {

    @RegisterExtension
    final MockContainerCli cli = new MockContainerCli();

    @Test
    void readsTheVolumeListFromTheJsonOutput() {
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
    void answersZeroWhenTheSizeIsMissing() {
        var commands = commandsPrinting("""
                [{"configuration": {"name": "logs", "driver": "local"}}]
                """);

        assertEquals(List.of(new Volume("logs", "local", "", 0, "")), commands.list());
    }

    private VolumeCommands commandsPrinting(String json) {
        return new VolumeCommands(cli.running("""
                #!/bin/sh
                cat <<'JSON'
                %s
                JSON
                """.formatted(json.strip())));
    }
}
