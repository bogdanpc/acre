package dev.ui;

import dev.applecontainer.CliResult;
import dev.tamboui.widgets.table.TableState;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Controller for a TableView
 */
public final class TableController<T> {

    private final String title;
    private final Loader<List<T>> rows;
    private final CliRunner runner;
    private final TableState state = new TableState();
    private final AtomicReference<Optional<String>> actionFailure = new AtomicReference<>(Optional.empty());

    public sealed interface Content<T> {
        record Loading<T>() implements Content<T> {
        }

        record Failure<T>(String message) implements Content<T> {
        }

        record Rows<T>(List<T> items, Optional<String> actionFailure) implements Content<T> {
        }
    }

    public TableController(String title, Supplier<CliResult<List<T>>> source) {
        this(title, source, CliRunner.DEFAULT_EXECUTOR);
    }

    public TableController(String title, Supplier<CliResult<List<T>>> source, Executor executor) {
        this.title = title;
        this.rows = new Loader<>(source, List.of(), executor);
        this.runner = new CliRunner(executor);
    }

    public String title() {
        return title;
    }

    public TableState state() {
        return state;
    }

    public List<T> items() {
        return rows.value();
    }

    public void reload() {
        clearActionFailure();
        rows.reload();
    }

    public void execute(Supplier<CliResult<?>> action) {
        clearActionFailure();
        runner.run(action, failure -> {
            failure.ifPresent(message -> actionFailure.set(Optional.of(message)));
            rows.reload();
        });
    }

    public Content<T> content() {
        var items = items();
        var failure = rows.failure();
        if (failure != null) return new Content.Failure<>(failure);
        if (items.isEmpty() && rows.loading()) return new Content.Loading<>();
        syncSelectedRow(items.size());
        return new Content.Rows<>(items, actionFailure.get());
    }

    public void moveDown() {
        clearActionFailure();
        state.selectNext(items().size());
    }

    public void moveUp() {
        clearActionFailure();
        state.selectPrevious();
    }

    private void clearActionFailure() {
        actionFailure.set(Optional.empty());
    }


    public Optional<T> selected() {
        var index = state.selected();
        var items = items();
        return index == null || index < 0 || index >= items.size()
                ? Optional.empty()
                : Optional.of(items.get(index));
    }

    private void syncSelectedRow(int rowCount) {
        if (rowCount == 0) {
            state.clearSelection();
            return;
        }
        var selected = state.selected();
        if (selected == null) {
            state.select(0);
        } else if (selected >= rowCount) {
            state.select(rowCount - 1);
        }
    }
}
