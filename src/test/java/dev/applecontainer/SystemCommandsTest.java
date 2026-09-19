package dev.applecontainer;

import dev.testing.MockContainerCli;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static dev.testing.CliResults.value;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SystemCommandsTest {

    @RegisterExtension
    final MockContainerCli cli = new MockContainerCli();

    @Test
    void isRunningWhenTheStatusCommandSucceeds() {
        assertTrue(commandsExiting(0).isRunning());
    }

    @Test
    void isNotRunningWhenTheStatusCommandFails() {
        assertFalse(commandsExiting(1).isRunning());
    }

    @Test
    void isNotRunningWhenTheBinaryIsMissing() {
        assertFalse(new SystemCommands(cli.missing()).isRunning());
    }

    @Test
    void startsWithoutWaitingForAKernelPrompt() {
        assertEquals("system start --disable-kernel-install", value(echoingCommands().start()).strip());
    }

    @Test
    void stops() {
        assertEquals("system stop", value(echoingCommands().stop()).strip());
    }

    private SystemCommands echoingCommands() {
        return new SystemCommands(cli.running("#!/bin/sh\necho \"$@\"\n"));
    }

    private SystemCommands commandsExiting(int exitCode) {
        return new SystemCommands(cli.running("#!/bin/sh\nexit %d\n".formatted(exitCode)));
    }
}
