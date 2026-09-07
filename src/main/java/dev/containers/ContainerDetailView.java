package dev.containers;

import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.text.Line;
import dev.tamboui.text.Span;
import dev.tamboui.text.Text;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.element.StyledElement;
import dev.tamboui.widgets.table.Cell;
import dev.tamboui.widgets.table.Row;
import dev.ui.Format;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static dev.tamboui.toolkit.Toolkit.*;

public final class ContainerDetailView {

    private static final Color ACCENT = Color.rgb(122, 162, 247);
    private static final Style LABEL = Style.EMPTY.fg(Color.rgb(0x56, 0x5F, 0x89));

    private static final int LABEL_WIDTH = 15;

    private static final Style KEY = Style.EMPTY.fg(ACCENT).bold();
    private static final Style HINT = Style.EMPTY.dim();
    private static final String SEPARATOR = " · ";
    private static final List<String[]> HINTS = List.of(
            new String[] {"l", "logs"},
            new String[] {"s", "start"},
            new String[] {"x", "stop"},
            new String[] {"t", "restart"},
            new String[] {"r", "reload"},
            new String[] {"↑ ↓", "other container"},
            new String[] {"esc", "back"});

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
                Format.valueOrPlaceholder(container.state()), ContainersView.stateStyle(container))));
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
        field(rows, "Created", Format.date(details.created(), ZoneId.systemDefault()));
        field(rows, "Started", Format.date(details.started(), ZoneId.systemDefault()));
        return table()
                .rows(rows)
                .widths(length(LABEL_WIDTH), fill())
                .columnSpacing(0)
                .length(rows.size());
    }

    private static void field(List<Row> rows, String label, String value) {
        rows.add(fieldRow(label, Span.raw(Format.valueOrPlaceholder(value))));
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

    private static String enabled(boolean flag) {
        return flag ? "Enabled" : "Disabled";
    }

    private static Element hint() {
        var spans = new ArrayList<Span>();
        for (var entry : HINTS) {
            if (!spans.isEmpty()) {
                spans.add(Span.styled(SEPARATOR, HINT));
            }
            spans.add(Span.styled(entry[0], KEY));
            spans.add(Span.styled(" " + entry[1], HINT));
        }
        return richText(Text.from(Line.from(spans))).length(1);
    }
}
