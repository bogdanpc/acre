package dev.ui;

import dev.tamboui.tui.event.KeyCode;
import dev.testing.TestScreen;
import dev.testing.Views;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainKeysTest {

    private final TestScreen terminal = new TestScreen();

    @Test
    void selectsATabByItsNumber() {
        terminal.show(Views.app(() -> {}));

        terminal.press('2');

        terminal.press(':')
                .assertShows("reload the volumes list")
                .assertDoesNotShow("reload the images list");
    }

    @Test
    void movesToTheNextTabWithTheRightArrow() {
        terminal.show(Views.app(() -> {}));

        terminal.press(KeyCode.RIGHT);

        terminal.press(':').assertShows("reload the volumes list");
    }

    @Test
    void movesToThePreviousTabWithH() {
        terminal.show(Views.app(() -> {}));
        terminal.press('2');

        terminal.press('h');

        terminal.press(':').assertShows("reload the images list");
    }

    @Test
    void togglesTheHelpWithQuestionMark() {
        terminal.show(Views.app(() -> {}));

        terminal.press('?').assertShows("press ? or esc to close");
        terminal.press('?').assertDoesNotShow("press ? or esc to close");
    }

    @Test
    void theHelpShowsTheKeysThatAreBound() {
        terminal.show(Views.app(() -> {}));

        terminal.press('?')
                .assertShows("Ctrl+p, :")
                .assertShows("Left, h, H")
                .assertDoesNotShow("^K");
    }

    @Test
    void closesTheHelpWithEscape() {
        terminal.show(Views.app(() -> {}));
        terminal.press('?');

        terminal.press(KeyCode.ESCAPE).assertDoesNotShow("press ? or esc to close");
    }

    @Test
    void quitsWithQ() {
        var quit = new AtomicBoolean();
        terminal.show(Views.app(() -> quit.set(true)));

        terminal.press('q');

        assertTrue(quit.get(), "q should quit");
    }
}
