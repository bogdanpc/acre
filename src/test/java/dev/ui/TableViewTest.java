package dev.ui;

import dev.applecontainer.CliResult;
import dev.tamboui.toolkit.Toolkit;
import dev.testing.TestScreen;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Supplier;

class TableViewTest {

    @Test
    void showsWhyTheListCouldNotBeLoaded() {
        var view = view(() -> CliResult.failure("connect: connection refused"));

        new TestScreen(100, 30).show(view::element)
                .assertShows("Cannot reach Apple Container.")
                .assertShows("connect: connection refused")
                .assertShows("press r to reload");
    }

    @Test
    void showsWhyAnActionFailed() {
        var controller = controller(() -> CliResult.success(List.of("dev-db")));
        var view = new TableView<>(controller, List.of(
                TableView.Column.of("NAME", Toolkit.length(20), name -> name)));

        controller.execute(() -> CliResult.failure("Error: container dev-db is not running"));

        new TestScreen(100, 30).show(view::element)
                .assertShows("dev-db")
                .assertShows("Error: container dev-db is not running");
    }

    @Test
    void keepsTheTableCleanWhenNothingFailed() {
        var view = view(() -> CliResult.success(List.of("dev-db")));

        new TestScreen(100, 30).show(view::element)
                .assertShows("dev-db")
                .assertDoesNotShow("Cannot reach Apple Container.");
    }

    private static TableView<String> view(java.util.function.Supplier<CliResult<List<String>>> source) {
        return new TableView<>(controller(source), List.of(
                TableView.Column.of("NAME", Toolkit.length(20), name -> name)));
    }

    private static TableController<String> controller(Supplier<CliResult<List<String>>> source) {
        return new TableController<>("Containers", source, new CliRunner(Runnable::run));
    }
}
