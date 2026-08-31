package dev.applecontainer;

import dev.testing.MockContainerCli;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

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

    private SystemCommands commandsExiting(int exitCode) {
        return new SystemCommands(cli.running("#!/bin/sh\nexit %d\n".formatted(exitCode)));
    }
}
