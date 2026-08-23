package dev.volumes;

import static dev.tamboui.toolkit.Toolkit.fill;
import static dev.tamboui.toolkit.Toolkit.length;
import static dev.tamboui.toolkit.Toolkit.table;

import java.util.List;

import dev.tamboui.style.Color;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.elements.TableElement;
import dev.tamboui.widgets.table.TableState;

/** Table of volumes, shown under the "Volumes" tab. */
public final class VolumesView {

    private static final List<String[]> ROWS = List.of(
        new String[] {"pgdata", "local", "/var/lib/volumes/pgdata"},
        new String[] {"cache", "local", "/var/lib/volumes/cache"},
        new String[] {"logs", "local", "/var/lib/volumes/logs"});

    private final TableState state = new TableState();

    /**
     * Returns how many volumes the table lists.
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
        TableElement table = table().header("NAME", "DRIVER", "MOUNTPOINT");
        ROWS.forEach(table::row);
        return table
            .widths(length(16), length(10), fill())
            .state(state)
            .highlightColor(Color.CYAN)
            .highlightSymbol("> ")
            .title("Volumes")
            .rounded();
    }
}
