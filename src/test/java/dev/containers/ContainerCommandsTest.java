package dev.containers;

import dev.applecontainer.AppleContainerCliException;
import dev.testing.MockContainerCli;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContainerCommandsTest {

    @RegisterExtension
    final MockContainerCli cli = new MockContainerCli();

    @Test
    void readsTheContainerListFromTheJsonOutput() {
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
    void hasNoAddressWhileTheContainerIsStopped() {
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
    void readsTheDetailsOfTheContainer() {
        var commands = commandsPrinting("""
                [
                  {
                    "id": "web-01",
                    "configuration": {
                      "creationDate": "2026-08-18T15:54:46Z",
                      "image": {"reference": "docker.io/library/nginx:1.27"},
                      "resources": {"cpus": 2, "memoryInBytes": 1073741824},
                      "platform": {"architecture": "arm64", "os": "linux"},
                      "networks": [{"options": {"hostname": "web-01"}}],
                      "initProcess": {
                        "executable": "docker-entrypoint.sh",
                        "arguments": ["nginx", "-g", "daemon off;"],
                        "user": {"id": {"gid": 20, "uid": 501}}
                      },
                      "publishedPorts": [
                        {"containerPort": 80, "hostAddress": "0.0.0.0", "hostPort": 8080, "proto": "tcp"}
                      ],
                      "runtimeHandler": "container-runtime-linux",
                      "rosetta": true,
                      "virtualization": false
                    },
                    "status": {"state": "running", "networks": [], "startedDate": "2026-08-31T18:56:47Z"}
                  }
                ]
                """);

        var details = commands.list().getFirst().details();

        assertEquals(new Container.Details(
                "linux/arm64", "web-01", "uid 501, gid 20", "docker-entrypoint.sh nginx -g daemon off;",
                List.of("0.0.0.0:8080 → 80/tcp"), "container-runtime-linux", false, true,
                "2026-08-18T15:54:46Z", "2026-08-31T18:56:47Z"), details);
    }

    @Test
    void showsTheMemoryInMegabytes() {
        assertEquals("512 MB", new Container("id", "image", "stopped", "", 1, 536870912L).memory());
    }

    @Test
    void failsWhenTheCliFails() {
        var commands = new ContainerCommands(cli.running("""
                #!/bin/sh
                echo "no such command" >&2
                exit 1
                """));

        var failure = assertThrows(AppleContainerCliException.class, commands::list);

        assertTrue(failure.getMessage().contains("no such command"), failure.getMessage());
    }

    @Test
    void failsWhenStartFails() {
        var commands = new ContainerCommands(cli.running("""
                #!/bin/sh
                echo "container not found" >&2
                exit 1
                """));

        var failure = assertThrows(AppleContainerCliException.class, () -> commands.start("web-01"));

        assertTrue(failure.getMessage().contains("container not found"), failure.getMessage());
    }

    private ContainerCommands commandsPrinting(String json) {
        return new ContainerCommands(cli.running("""
                #!/bin/sh
                cat <<'JSON'
                %s
                JSON
                """.formatted(json.strip())));
    }
}
