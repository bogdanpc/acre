package dev.palette;

import dev.applecontainer.CliResult;
import dev.images.ContainerImage;
import dev.images.ImagesView;
import dev.tamboui.tui.event.KeyCode;
import dev.testing.TestScreen;
import dev.testing.Views;
import dev.ui.MainView;
import dev.ui.Tab;
import dev.ui.TableController;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaletteTest {

    final TestScreen terminal = new TestScreen();

    @Test
    void opensWithColonAndWithCtrlP() {
        terminal.show(Views.app(() -> {}));

        terminal.press(':').assertShows("Run a command");
        terminal.press(KeyCode.ESCAPE);
        terminal.pressCtrl('p').assertShows("Run a command");
    }

    @Test
    void closesWithEscape() {
        terminal.show(Views.app(() -> {}));
        terminal.press(':');

        terminal.press(KeyCode.ESCAPE).assertDoesNotShow("Run a command");
    }

    @Test
    void listsTheCommandsOfTheSelectedTabOnly() {
        terminal.show(Views.app(() -> {}));

        terminal.press(':')
                .assertShows("reload the images list")
                .assertDoesNotShow("reload the volumes list");
    }

    @Test
    void marksTheSelectedCommand() {
        terminal.show(Views.app(() -> {}));

        var screen = terminal.press(':');

        assertTrue(screen.line("reload the images list").contains("❯reload the images list"), screen.toString());
    }

    @Test
    void startsEveryLabelAtTheSameColumn() {
        terminal.show(Views.app(() -> {}));

        var screen = terminal.press(':');

        assertEquals(screen.columnOf("reload the images list"), screen.columnOf("quit"), screen.toString());
    }

    @Test
    void showsTheKeyOfACommandThatHasOne() {
        terminal.show(Views.app(() -> {}));

        var screen = terminal.press(':');

        assertTrue(screen.line("quit").contains("[q]"), screen.toString());
        assertTrue(screen.line("reload the images list").contains("[r]"), screen.toString());
    }

    @Test
    void putsEveryKeyAtTheSameColumn() {
        terminal.show(Views.app(() -> {}));

        var screen = terminal.press(':');

        assertEquals(screen.columnOf("[q]"), screen.columnOf("[r]"), screen.toString());
    }

    @Test
    void leavesTheRowPlainWhenTheCommandHasNoKey() {
        var images = ImagesView.of(new TableController<>("Images", () -> CliResult.success(List.of()), Runnable::run));
        var tab = new Tab("Images", images::element, () -> List.of(new Command("no key here", () -> {})));
        terminal.show(new MainView(() -> {}, List.of(tab)));

        var screen = terminal.press(':');

        assertFalse(screen.line("no key here").contains("]"), screen.toString());
    }

    @Test
    void cutsALongLabelSoTheKeyStaysOnTheRow() {
        var images = ImagesView.of(new TableController<>("Images", () -> CliResult.success(List.of()), Runnable::run));
        var tab = new Tab("Images", images::element, () -> List.of(
                new Command("a very long command label that will not fit inside the palette row",
                        "enter", () -> {})));
        terminal.show(new MainView(() -> {}, List.of(tab)));

        var screen = terminal.press(':');

        assertTrue(screen.line("a very long command").contains("[enter]"), screen.toString());
    }

    @Test
    void keepsOnlyTheCommandsThatMatchWhatTheUserTyped() {
        terminal.show(Views.app(() -> {}));
        terminal.press(':');

        terminal.type("quit")
                .assertShows("quit")
                .assertDoesNotShow("open images");
    }

    @Test
    void tellsTheUserWhenNothingMatches() {
        terminal.show(Views.app(() -> {}));
        terminal.press(':');

        terminal.type("zzz").assertShows("no command matches");
    }

    @Test
    void enterRunsTheSelectedCommandAndClosesThePalette() {
        var quit = new AtomicBoolean();
        terminal.show(Views.app(() -> quit.set(true)));
        terminal.press(':');
        terminal.type("quit");

        terminal.press(KeyCode.ENTER).assertDoesNotShow("Run a command");

        assertTrue(quit.get(), "the palette did not run the command");
    }

    @Test
    void sendsTheKeysToTheQueryInsteadOfTheTable() {
        var loads = new AtomicInteger();
        var images = ImagesView.of(new TableController<>("Images", () -> {
            loads.incrementAndGet();
            return CliResult.success(List.of(new ContainerImage("docker.io/library/redis:8")));
        }, Runnable::run));
        terminal.show(new MainView(() -> {}, List.of(Tab.of(images))));

        terminal.press(':');
        terminal.press('r');

        // the "r" went into the query, so the list did not reload
        assertEquals(1, loads.get());
    }

    @Test
    void keepsTheOriginalOrderForABlankQuery() {
        assertEquals(List.of("b", "a", "c"), labels(Fuzzy.rank("", commands("b", "a", "c"))));
    }

    @Test
    void aBlankQueryMatchesEverything() {
        var hit = Fuzzy.match("  ", "anything");

        assertNotNull(hit);
        assertEquals(0, hit.orElseThrow().score());
        assertEquals(0, hit.orElseThrow().positions().length);
    }

    private static List<Command> commands(String... labels) {
        return Arrays.stream(labels).map(label -> new Command(label, () -> {})).toList();
    }

    private static List<String> labels(List<Command> commands) {
        return commands.stream().map(Command::label).toList();
    }
}
