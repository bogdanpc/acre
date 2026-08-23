package dev.containers;

import static dev.tamboui.toolkit.Toolkit.fill;
import static dev.tamboui.toolkit.Toolkit.length;
import static dev.tamboui.toolkit.Toolkit.table;

import java.util.List;

import dev.tamboui.style.Color;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.elements.TableElement;
import dev.tamboui.widgets.table.TableState;

/** Table of containers, shown under the "Containers" tab. */
public final class ContainersView {

    private static final List<String[]> ROWS = List.of(
        new String[] {"web-01", "nginx:1.27", "running", "192.168.64.3"},
        new String[] {"api-01", "eclipse-temurin:25", "running", "192.168.64.4"},
        new String[] {"cache-01", "redis:7", "stopped", "-"});

    private final TableState state = new TableState();

    /**
     * Returns how many containers the table lists.
     *
     * @return the row count, shown next to the tab title
     */
    public int count() {
        return ROWS.size();
    }

    /**
     * Builds the table element.
     *
     * @return the element to render
     */
    public Element element() {
        TableElement table = table().header("ID", "IMAGE", "STATE", "ADDRESS");
        ROWS.forEach(table::row);
        return table
            .widths(length(12), fill(), length(10), length(16))
            .state(state)
            .highlightColor(Color.CYAN)
            .highlightSymbol("> ")
            .title("Containers")
            .rounded();
    }
}
