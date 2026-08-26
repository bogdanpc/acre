package dev.ui;

import dev.tamboui.layout.Constraint;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.text.Span;
import dev.tamboui.toolkit.Toolkit;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.tui.bindings.ActionHandler;
import dev.tamboui.tui.bindings.Actions;
import dev.tamboui.widgets.table.Cell;
import dev.tamboui.widgets.table.Row;

import java.util.List;
import java.util.function.Function;

public final class TableView<T> {

    private static final String SELECTED_SYMBOL = "» ";
    private static final Style SELECTED_ROW =
            Style.EMPTY.fg(Color.WHITE).bg(Color.rgb(0x26, 0x4F, 0x78)).bold();

    /**
     * Table column
     */
    public record Column<T>(String header, Constraint width, Align align,
                            Function<T, String> value, Function<T, Style> style) {

        public static <T> Column<T> of(String header, Constraint width, Function<T, String> value) {
            return new Column<>(header, width, Align.LEFT, value, _ -> Style.EMPTY);
        }

        public Column<T> align(Align align) {
            return new Column<>(header, width, align, value, style);
        }

        public Column<T> style(Function<T, Style> style) {
            return new Column<>(header, width, align, value, style);
        }

        Cell headerCell() {
            return Cell.from(fit(header));
        }

        Cell cell(T row) {
            return Cell.from(Span.styled(fit(value.apply(row)), style.apply(row)));
        }

        private String fit(String text) {
            return width instanceof Constraint.Length length ? align.pad(text, length.value()) : text;
        }
    }

    private final TableController<T> controller;
    private final List<Column<T>> columns;
    private final ActionHandler actions;

    public TableView(TableController<T> controller, List<Column<T>> columns) {
        this.controller = controller;
        this.columns = List.copyOf(columns);
        this.actions = new ActionHandler(KeyBindings.get())
                .on(KeyBindings.RELOAD, _ -> controller.reload())
                .on(Actions.MOVE_DOWN, _ -> controller.moveDown())
                .on(Actions.MOVE_UP, _ -> controller.moveUp());
    }

    public String title() {
        return controller.title();
    }

    public Element element() {
        return Toolkit.stack(controller.element(this::body))
                .id(controller.title())
                .focusable()
                .onAction(actions);
    }

    private Element body(List<T> rows) {
        var tableRows = rows.stream().map(this::cells).toList();
        return Toolkit.table()
                .header(headers())
                .rows(tableRows)
                .widths(widths())
                .state(controller.state())
                .highlightStyle(SELECTED_ROW)
                .highlightSymbol(SELECTED_SYMBOL)
                .title(" " + controller.title() + " ");
    }

    private Row headers() {
        return Row.from(columns.stream().map(Column::headerCell).toArray(Cell[]::new));
    }

    private Row cells(T row) {
        return Row.from(columns.stream().map(column -> column.cell(row)).toArray(Cell[]::new));
    }

    private Constraint[] widths() {
        return columns.stream().map(Column::width).toArray(Constraint[]::new);
    }
}
