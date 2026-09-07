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

        var images = value(commands.list()).stream().map(ContainerImage::reference).toList();

        assertEquals(List.of("docker.io/library/postgres:18-alpine", "localhost:5000/team/tool:dev"), images);
    }

    @Test
    void readsTheIndexOfAnImage() {
        var commands = commandsPrinting("""
                [
                  {
                    "configuration": {
                      "name": "docker.io/library/postgres:18-alpine",
                      "descriptor": {
                        "digest": "sha256:d3e1620b530c944afa6e887d22eb899824da68e19c52024bf98f5220c88a65b2",
                        "mediaType": "application/vnd.oci.image.index.v1+json",
                        "size": 10293
                      }
                    }
                  }
                ]
                """);

        var image = value(commands.list()).getFirst();

        assertEquals("application/vnd.oci.image.index.v1+json", image.mediaType());
        assertEquals("sha256:d3e1620b530c944afa6e887d22eb899824da68e19c52024bf98f5220c88a65b2", image.digest());
        assertEquals(10293, image.indexSize());
    }

    @Test
    void readsTheConfigurationOfEveryPlatform() {
        var commands = commandsPrinting("""
                [
                  {
                    "configuration": {"name": "docker.io/library/postgres:18-alpine"},
                    "variants": [
                      {
                        "config": {
                          "config": {
                            "Cmd": ["postgres"],
                            "Entrypoint": ["docker-entrypoint.sh"],
                            "Env": ["PATH=/usr/local/bin", "PG_MAJOR=18"],
                            "WorkingDir": "/"
                          }
                        },
                        "platform": {"architecture": "arm64", "os": "linux", "variant": "v8"},
                        "size": 117881399
                      },
                      {
                        "platform": {"architecture": "unknown", "os": "unknown"},
                        "size": 659395
                      }
                    ]
                  }
                ]
                """);

        var configurations = value(commands.list()).getFirst().configurations();

        assertEquals(List.of(
                new ContainerImage.Configuration("linux/arm64/v8", 117881399L,
                        "docker-entrypoint.sh", "postgres", "/",
                        List.of(new ContainerImage.Variable("PATH", "/usr/local/bin"),
                                new ContainerImage.Variable("PG_MAJOR", "18"))),
                new ContainerImage.Configuration("unknown/unknown", 659395L, "", "", "", List.of())),
                configurations);
    }

    @Test
    void joinsTheCommandArgumentsIntoOneLine() {
        var commands = commandsPrinting("""
                [
                  {
                    "configuration": {"name": "mcr.microsoft.com/azure-storage/azurite:latest"},
                    "variants": [
                      {
                        "config": {"config": {"Cmd": ["azurite", "-l", "/data", "--blobHost", "0.0.0.0"]}},
                        "platform": {"architecture": "arm64", "os": "linux"},
                        "size": 114219902
                      }
                    ]
                  }
                ]
                """);

        var configuration = value(commands.list()).getFirst().configurations().getFirst();

        assertEquals("azurite -l /data --blobHost 0.0.0.0", configuration.cmd());
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
