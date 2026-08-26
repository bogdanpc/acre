package dev.ui;

import dev.tamboui.toolkit.Toolkit;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.widgets.table.TableState;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Controller for a TableView
 */
public final class TableController<T> {

    private final String title;
    private final Loader<List<T>> rows;
    private final TableState state = new TableState();

    public TableController(String title, Supplier<List<T>> source) {
        this(title, new Loader<>(source, List.of()));
    }

    public TableController(String title, Supplier<List<T>> source, Executor executor) {
        this(title, new Loader<>(source, List.of(), executor));
    }

    public TableController(String title, Loader<List<T>> rows) {
        this.title = title;
        this.rows = rows;
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
        rows.reload();
    }

    public void moveDown() {
        state.selectNext(items().size());
    }

    public void moveUp() {
        state.selectPrevious();
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
        return body.apply(items);
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
