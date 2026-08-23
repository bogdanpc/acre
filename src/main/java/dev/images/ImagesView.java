package dev.images;

import static dev.tamboui.toolkit.Toolkit.fill;
import static dev.tamboui.toolkit.Toolkit.length;
import static dev.tamboui.toolkit.Toolkit.table;

import java.util.List;

import dev.tamboui.style.Color;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.elements.TableElement;
import dev.tamboui.widgets.table.TableState;

/** Table of images, shown under the "Images" tab. */
public final class ImagesView {

    private static final List<String[]> ROWS = List.of(
        new String[] {"nginx", "1.27", "68 MB"},
        new String[] {"eclipse-temurin", "25", "412 MB"},
        new String[] {"redis", "7", "41 MB"});

    private final TableState state = new TableState();

    /**
     * Returns how many images the table lists.
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
        TableElement table = table().header("REPOSITORY", "TAG", "SIZE");
        ROWS.forEach(table::row);
        return table
            .widths(fill(), length(12), length(12))
            .state(state)
            .highlightColor(Color.CYAN)
            .highlightSymbol("> ")
            .title("Images")
            .rounded();
    }
}
