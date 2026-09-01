package dev.images;

import dev.testing.MockContainerCli;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static dev.testing.CliResults.message;
import static dev.testing.CliResults.value;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageCommandsTest {

    @RegisterExtension
    final MockContainerCli cli = new MockContainerCli();

    @Test
    void readsTheImageListFromTheJsonOutput() {
        var commands = commandsPrinting("""
                [
                  {"configuration": {"name": "docker.io/library/postgres:18-alpine"}},
                  {"configuration": {"name": "localhost:5000/team/tool:dev"}}
                ]
                """);

        var images = value(commands.list());

        assertEquals(List.of(
                new ContainerImage("docker.io/library/postgres:18-alpine"),
                new ContainerImage("localhost:5000/team/tool:dev")), images);
    }

    @Test
    void failsWhenTheCliFails() {
        var commands = new ImageCommands(cli.running("""
                #!/bin/sh
                echo "no such command" >&2
                exit 1
                """));

        var failure = message(commands.list());

        assertTrue(failure.contains("no such command"), failure);
    }

    private ImageCommands commandsPrinting(String json) {
        return new ImageCommands(cli.running("""
                #!/bin/sh
                cat <<'JSON'
                %s
                JSON
                """.formatted(json.strip())));
    }
}
