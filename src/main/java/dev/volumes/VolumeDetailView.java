package dev.volumes;

import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.element.StyledElement;
import dev.tamboui.widgets.table.Cell;
import dev.tamboui.widgets.table.Row;
import dev.ui.Format;

import java.util.ArrayList;
import java.util.List;

import static dev.tamboui.toolkit.Toolkit.*;

public final class VolumeDetailView {

    private static final Color ACCENT = Color.rgb(122, 162, 247);
    private static final Style LABEL = Style.EMPTY.fg(Color.rgb(0x56, 0x5F, 0x89));
    private static final Style HEADING = Style.EMPTY.fg(Color.WHITE).bold();

    private static final int LABEL_WIDTH = 15;
    private static final String NONE = "No container mounts this volume.";
    private static final String HINTS = "r reload · d delete · ↑ ↓ other volume · esc back";

    private VolumeDetailView() {
    }

    public static StyledElement<?> page(Volume volume, List<VolumeUse> uses) {
        return panel(" " + volume.name() + " ",
                heading("Overview"),
                overview(volume, uses),
                spacer(1),
                heading("Technical Details"),
                technical(volume),
                spacer(1),
                heading("Used By Containers"),
                usedBy(uses),
                spacer(),
                hint())
                .rounded()
                .borderColor(ACCENT)
                .padding(1);
    }

    private static Element overview(Volume volume, List<VolumeUse> uses) {
        var rows = new ArrayList<Row>();
        field(rows, "Source", volume.source());
        field(rows, "Destination", destination(uses));
        field(rows, "Type", volume.driver());
        field(rows, "Size", Format.bytes(volume.size()));
        field(rows, "Containers", String.valueOf(uses.size()));
        return fields(rows);
    }

    private static Element technical(Volume volume) {
        var rows = new ArrayList<Row>();
        field(rows, "Filesystem", volume.format());
        return fields(rows);
    }

    private static String destination(List<VolumeUse> uses) {
        return uses.isEmpty() ? "" : uses.getFirst().destination();
    }

    private static Element usedBy(List<VolumeUse> uses) {
        if (uses.isEmpty()) {
            return text(NONE).dim().length(1);
        }
        return table()
                .header(Row.from(header("CONTAINER"), header("IP ADDRESS"),
                        header("HOSTNAME"), header("MOUNT POINT")))
                .rows(uses.stream().map(VolumeDetailView::row).toList())
                .widths(length(30), length(16), length(20), fill())
                .columnSpacing(1)
                .length(uses.size() + 1);
    }

    private static Row row(VolumeUse use) {
        return Row.from(
                Cell.from(use.container()).style(Style.EMPTY.fg(Color.CYAN)),
                Cell.from(Format.valueOrPlaceholder(use.address())),
                Cell.from(Format.valueOrPlaceholder(use.hostname())),
                Cell.from(Format.valueOrPlaceholder(use.destination())));
    }

    private static Cell header(String title) {
        return Cell.from(title).style(LABEL);
    }

    private static Element heading(String title) {
        return text(title).style(HEADING).length(1);
    }

    private static Element fields(List<Row> rows) {
        return table()
                .rows(rows)
                .widths(length(LABEL_WIDTH), fill())
                .columnSpacing(0)
                .length(rows.size());
    }

    private static void field(List<Row> rows, String label, String value) {
        rows.add(Row.from(Cell.from(label).style(LABEL), Cell.from(Format.valueOrPlaceholder(value))));
    }

    private static Element hint() {
        return text(HINTS).dim().length(1);
    }
}
