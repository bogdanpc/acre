package dev.ui;

import dev.applecontainer.AppleContainerCliException;
import dev.tamboui.toolkit.Toolkit;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.widgets.table.TableState;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Controller for a TableView
 */
public final class TableController<T> {

    private static final Executor DEFAULT_EXECUTOR = Executors.newThreadPerTaskExecutor(
            Thread.ofVirtual().name("acre-action-", 0).factory());

    private static final int MAX_FAILURE_WIDTH = 200;

    private final String title;
    private final Loader<List<T>> rows;
    private final Executor executor;
    private final TableState state = new TableState();
    private final AtomicReference<Optional<String>> actionFailure = new AtomicReference<>(Optional.empty());

    public TableController(String title, Supplier<List<T>> source) {
        this(title, new Loader<>(source, List.of()), DEFAULT_EXECUTOR);
    }

    public TableController(String title, Supplier<List<T>> source, Executor executor) {
        this(title, new Loader<>(source, List.of(), executor), executor);
    }

    public TableController(String title, Loader<List<T>> rows, Executor executor) {
        this.title = title;
        this.rows = rows;
        this.executor = executor;
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

    public void execute(Runnable action) {
        clearActionFailure();
        executor.execute(() -> {
            try {
                action.run();
            } catch (AppleContainerCliException e) {
                actionFailure.set(Optional.of(String.valueOf(e.getMessage())));
            } catch (RuntimeException e) {
                actionFailure.set(Optional.of(String.valueOf(e)));
            } finally {
                rows.reload();
            }
        });
    }

    /** @return the last failed action message, or empty when the last action was fine */
    public Optional<String> actionFailure() {
        return actionFailure.get();
    }

    public void moveDown() {
        clearActionFailure();
        state.selectNext(items().size());
    }

    public void moveUp() {
        clearActionFailure();
        state.selectPrevious();
    }

    /** Drops the message once the user moves on, so it never outlives the row it is about. */
    private void clearActionFailure() {
        actionFailure.set(Optional.empty());
    }

    public Element element(Function<List<T>, Element> body) {
        var items = rows.value();
        if (rows.failure() != null) {
            return Toolkit.panel(title, failure()).rounded();
        }
        if (items.isEmpty() && rows.loading()) {
            return Toolkit.panel(title, Toolkit.text("Loading...").dim()).rounded();
        }
        syncSelectedRow(items.size());
        return dockActionFailure(body.apply(items));
    }

    public Optional<T> selected() {
        var index = state.selected();
        var items = items();
        return index == null || index < 0 || index >= items.size()
                ? Optional.empty()
                : Optional.of(items.get(index));
    }

    /** Display error / failed action on the last row. */
    private Element dockActionFailure(Element table) {
        return actionFailure.get()
                .<Element>map(message -> Toolkit.dock()
                        .center(table)
                        .bottom(Toolkit.text(oneLine(message)).red(), Toolkit.length(1)))
                .orElse(table);
    }

    private static String oneLine(String message) {
        var flat = message.replace('\n', ' ').replace('\r', ' ').strip();
        return flat.length() <= MAX_FAILURE_WIDTH ? flat : flat.substring(0, MAX_FAILURE_WIDTH - 1) + "…";
    }

    /**
     * Generic error text
     */
    private Element failure() {
        return Toolkit.column(
                Toolkit.text("Cannot reach Apple Container.").red(),
                Toolkit.text("Check it is running, then press r to reload.").dim());
    }

    /**
     * Matches the selected row to the current row count and preserve the column layout to display the row selected marker
     */
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
