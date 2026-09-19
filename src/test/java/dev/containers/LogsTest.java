package dev.containers;

import dev.applecontainer.AppleContainerCli;
import dev.tamboui.tui.event.KeyCode;
import dev.applecontainer.CliResult;
import dev.testing.MockContainerCli;
import dev.testing.TestScreen;
import dev.ui.CliRunner;
import dev.ui.TableController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.testing.CliResults.value;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LogsTest {

    @RegisterExtension
    final MockContainerCli cli = new MockContainerCli();

    final TestScreen terminal = new TestScreen();

    private static final Container WEB = new Container(
            "web-01", "docker.io/library/nginx:1.27", "running", "192.168.64.3", 2, 1073741824L);

    private static final String LOGGING_CLI = """
            #!/bin/sh
            printf '%s\\n' \\
              'time="2026/09/01 14:56:05" level=info msg="[http] starting on [::]:8025"' \\
              '[db] got terminated signal, shutting down'
            """;

    @Test
    void readsTheLastLinesOfTheLog() {
        var commands = new ContainerCommands(cli.running("""
                #!/bin/sh
                echo "$@"
                """));

        assertEquals(List.of("logs -n 5 web-01"), value(commands.logs("web-01", 5)));
    }

    @Test
    void readsNoLineWhenTheLogIsEmpty() {
        var commands = new ContainerCommands(cli.running("#!/bin/sh\n"));

        assertEquals(List.of(), value(commands.logs("web-01", 5)));
    }

    @Test
    void lOpensTheLogsOfTheSelectedContainer() {
        terminal.show(tab(LOGGING_CLI)::element);

        terminal.press('l')
                .assertShows("web-01 logs")
                .assertShows("Filter logs...")
                .assertShows("[db] got terminated signal, shutting down");
    }

    @Test
    void tellsTheUserAboutTheLogsOnTheDetailPage() {
        terminal.show(tab(LOGGING_CLI)::element);

        terminal.press(KeyCode.ENTER).assertShows("l logs");
    }

    @Test
    void keepsOnlyTheLinesThatHoldWhatTheUserTyped() {
        terminal.show(tab(LOGGING_CLI)::element);
        terminal.press('l');

        terminal.type("db")
                .assertShows("[db] got terminated signal")
                .assertDoesNotShow("[http] starting on");
    }

    @Test
    void tellsTheUserWhenNoLineHoldsTheText() {
        terminal.show(tab(LOGGING_CLI)::element);
        terminal.press('l');

        terminal.type("zzz").assertShows("no log line holds that text");
    }

    @Test
    void escapeGoesBackToTheContainerList() {
        terminal.show(tab(LOGGING_CLI)::element);
        terminal.press('l');

        terminal.press(KeyCode.ESCAPE)
                .assertDoesNotShow("Filter logs...")
                .assertShows("web-01");
    }

    @Test
    void sendsTheKeysToTheFilterInsteadOfTheTable() {
        var loads = new AtomicInteger();
        var commands = new ContainerCommands(cli.running(LOGGING_CLI));
        var table = new TableController<>("Containers", () -> {
            loads.incrementAndGet();
            return CliResult.success(List.of(WEB));
        }, new CliRunner(Runnable::run));
        terminal.show(new ContainersTab(commands, table, new LogsController(commands, new CliRunner(Runnable::run)))::element);
        terminal.press('l');
        int before = loads.get();

        terminal.press('r');

        assertEquals(before, loads.get(), "the r went to the table instead of the filter");
    }

    @Test
    void pinsTheViewToTheNewestLineAndScrollsUpWithTheArrow() {
        terminal.show(tab("""
                #!/bin/sh
                i=1
                while [ $i -le 100 ]; do printf 'log %03d\\n' $i; i=$((i + 1)); done
                """)::element);

        terminal.press('l')
                .assertShows("log 100")
                .assertDoesNotShow("log 001");

        terminal.press(KeyCode.UP).assertDoesNotShow("log 100");
    }

    @Test
    void showsWhyTheLogCannotBeRead() {
        terminal.show(tab("#!/bin/sh\nexit 1\n")::element);

        terminal.press('l').assertShows("Cannot read the logs.");
    }

    private ContainersTab tab(String script) {
        return tab(cli.running(script));
    }

    private ContainersTab tab(AppleContainerCli container) {
        var commands = new ContainerCommands(container);
        var table = new TableController<>("Containers", () -> CliResult.success(List.of(WEB)), new CliRunner(Runnable::run));
        return new ContainersTab(commands, table, new LogsController(commands, new CliRunner(Runnable::run)));
    }
}
