package dev.containers;

import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.text.Span;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.element.StyledElement;
import dev.tamboui.widgets.table.Cell;
import dev.tamboui.widgets.table.Row;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static dev.tamboui.toolkit.Toolkit.*;

public final class ContainerDetailView {

    private static final Color ACCENT = Color.rgb(122, 162, 247);
    private static final Style LABEL = Style.EMPTY.fg(Color.rgb(0x56, 0x5F, 0x89));

    private static final int LABEL_WIDTH = 15;
    private static final String MISSING = "-";
    private static final String HINTS = "s start · x stop · t restart · r reload · ↑ ↓ other container · esc back";

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("d MMM yyyy 'at' HH:mm", Locale.ENGLISH);

    private ContainerDetailView() {
    }

    public static StyledElement<?> page(Container container) {
        return panel(" " + container.id() + " ", body(container), spacer(), hint())
                .rounded()
                .borderColor(ACCENT)
                .padding(1);
    }

    private static Element body(Container container) {
        var rows = new ArrayList<Row>();
        var details = container.details();
        field(rows, "Name", container.id());
        rows.add(fieldRow("Status", Span.styled(
                value(container.state()), ContainersView.stateStyle(container))));
        field(rows, "Image", container.image());
        field(rows, "Platform", details.platform());
        field(rows, "CPUs", String.valueOf(container.cpus()));
        field(rows, "Memory", container.memory());
        field(rows, "IP Address", container.address());
        field(rows, "Hostname", details.hostname());
        field(rows, "User", details.user());
        field(rows, "Command", details.command());
        fields(rows, "Ports", details.ports());
        field(rows, "Nested Virt", enabled(details.nestedVirtualization()));
        field(rows, "Rosetta", enabled(details.rosetta()));
        field(rows, "Runtime", details.runtime());
        field(rows, "Created", date(details.created(), ZoneId.systemDefault()));
        field(rows, "Started", date(details.started(), ZoneId.systemDefault()));
        return table()
                .rows(rows)
                .widths(length(LABEL_WIDTH), fill())
                .columnSpacing(0)
                .length(rows.size());
    }

    private static void field(List<Row> rows, String label, String value) {
        rows.add(fieldRow(label, Span.raw(value(value))));
    }

    private static void fields(List<Row> rows, String label, List<String> values) {
        if (values.isEmpty()) {
            field(rows, label, "");
            return;
        }
        for (int i = 0; i < values.size(); i++) {
            field(rows, i == 0 ? label : "", values.get(i));
        }
    }

    private static Row fieldRow(String label, Span value) {
        return Row.from(Cell.from(label).style(LABEL), Cell.from(value));
    }

    private static String value(String value) {
        return value == null || value.isBlank() ? MISSING : value;
    }

    private static String enabled(boolean flag) {
        return flag ? "Enabled" : "Disabled";
    }

    private static Element hint() {
        return text(HINTS).dim().length(1);
    }

    static String date(String iso, ZoneId zone) {
        if (iso == null || iso.isBlank()) {
            return "";
        }
        try {
            return DATE.format(Instant.parse(iso).atZone(zone));
        } catch (DateTimeParseException _) {
            return iso;
        }
    }
}
