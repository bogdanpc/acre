package dev.applecontainer;

import dev.testing.MockContainerCli;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppleContainerCliTest {

    @RegisterExtension
    final MockContainerCli cli = new MockContainerCli();

    @Test
    void passesTheArgumentsAndReturnsWhatTheCliPrinted() {
        var container = cli.running("""
                #!/bin/sh
                echo "called with $*"
                """);

        var result = container.run("list", "--all");

        assertTrue(result.isSuccess());
        assertEquals("called with list --all\n", result.stdOut());
        assertEquals("", result.stdErr());
    }
}
