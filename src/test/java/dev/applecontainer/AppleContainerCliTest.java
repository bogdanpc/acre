package dev.applecontainer;

import dev.testing.MockContainerCli;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static dev.testing.CliResults.message;
import static dev.testing.CliResults.value;
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

        assertEquals("called with list --all\n", value(result));
    }

    @Test
    void answersWhyTheCommandFailedInsteadOfThrowing() {
        var container = cli.running("""
                #!/bin/sh
                echo "no such command" >&2
                exit 1
                """);

        var failure = message(container.run("bogus"));

        assertTrue(failure.contains("no such command"), failure);
        assertTrue(failure.contains("container bogus: exit code 1"), failure);
    }

    @Test
    void answersWhyTheBinaryCannotRun() {
        var failure = message(cli.missing().run("list"));

        assertTrue(failure.contains("cannot start"), failure);
    }
}
