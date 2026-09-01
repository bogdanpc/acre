package dev.ui;

import dev.testing.TestScreen;
import dev.testing.Views;
import org.junit.jupiter.api.Test;

/**
 * The app draws. Everything else has its own test.
 */
class AppTest {

    @Test
    void drawsTheTabBarAndTheSelectedTab() {
        new TestScreen().show(Views.app(() -> {}))
                .assertShows("Images")
                .assertShows("Volumes");
    }
}
