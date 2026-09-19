package dev.ui;

import dev.applecontainer.SystemCommands;
import dev.tamboui.tui.event.KeyCode;
import dev.testing.MockContainerCli;
import dev.testing.TestScreen;
import dev.testing.Views;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

class SystemTest {

    @RegisterExtension
    final MockContainerCli cli = new MockContainerCli();

    @TempDir
    Path folder;

    private final TestScreen terminal = new TestScreen();

    @Test
    void showsTheSystemUpOnceItStarts() {
        var started = folder.resolve("started");
        var system = new SystemCommands(cli.running("""
                #!/bin/sh
                case "$1 $2" in
                  "system start") touch '%1$s' ;;
                  "system status") test -e '%1$s' ;;
                esac
                """.formatted(started)));
        terminal.show(Views.app(system, () -> {})).assertShows("down");

        terminal.press(':');
        terminal.type("start the container system");

        terminal.press(KeyCode.ENTER).assertShows("up");
    }

    @Test
    void showsTheSystemDownOnceItStops() {
        var stopped = folder.resolve("stopped");
        var system = new SystemCommands(cli.running("""
                #!/bin/sh
                case "$1 $2" in
                  "system stop") touch '%1$s' ;;
                  "system status") test ! -e '%1$s' ;;
                esac
                """.formatted(stopped)));
        terminal.show(Views.app(system, () -> {})).assertShows("up");

        terminal.press(':');
        terminal.type("stop the container system");

        terminal.press(KeyCode.ENTER).assertShows("down");
    }
}
