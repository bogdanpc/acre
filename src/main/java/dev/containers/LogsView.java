package dev.containers;

import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.text.Line;
import dev.tamboui.text.Span;
import dev.tamboui.text.Text;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.element.StyledElement;
import dev.tamboui.toolkit.elements.ListElement;
import dev.tamboui.widgets.common.ScrollBarPolicy;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;

import java.util.ArrayList;
import java.util.List;

import static dev.tamboui.toolkit.Toolkit.*;

public final class LogsView {

    private static final Color ACCENT = Color.rgb(122, 162, 247);

    private static final String SEARCH = "/";
    private static final String PLACEHOLDER = "Filter logs...";
    private static final String HINTS = "type to filter · ↑ ↓ scroll · esc back";
    private static final String EMPTY = "no log line yet";
    private static final String NO_MATCH = "no log line holds that text";
    private static final int MAX_FAILURE_WIDTH = 200;

    private static final List<KeyCode> SCROLL_KEYS =
            List.of(KeyCode.UP, KeyCode.DOWN, KeyCode.PAGE_UP, KeyCode.PAGE_DOWN, KeyCode.HOME, KeyCode.END);

    private final LogsController controller;

    private final ListElement<?> lines = list().displayOnly().stickyScroll().scrollbar(ScrollBarPolicy.AS_NEEDED);

    public LogsView(LogsController controller) {
        this.controller = controller;
    }

    public StyledElement<?> element() {
        return panel(" " + controller.container() + " logs ",
                dock().top(filter(), length(1))
                        .center(body())
                        .bottom(hint(), length(1))
                        .fill())
                .rounded()
                .borderColor(ACCENT)
                .padding(1);
    }

    public boolean scroll(KeyEvent event) {
        return SCROLL_KEYS.stream().anyMatch(event::isKey) && lines.handleKeyEvent(event, true).isHandled();
    }

    private Element body() {
        var failure = controller.failure();
        if (failure != null) {
            return column(
                    text("Cannot read the logs.").red(),
                    text(oneLine(failure)).dim());
        }
        var rows = controller.lines();
        if (rows.isEmpty()) {
            return text(message()).dim();
        }
        return lines.items(rows);
    }

    private String message() {
        if (controller.loading()) {
            return "Loading...";
        }
        return controller.filter().text().isBlank() ? EMPTY : NO_MATCH;
    }

    private Element filter() {
        var query = controller.filter();
        var text = query.text();
        int caret = Math.clamp(query.cursorPosition(), 0, text.length());
        var under = caret < text.length() ? text.substring(caret, caret + 1) : " ";
        var after = caret < text.length() ? text.substring(caret + 1) : "";

        var spans = new ArrayList<Span>();
        spans.add(Span.styled(SEARCH + " ", Style.EMPTY.fg(ACCENT).bold()));
        spans.add(Span.raw(text.substring(0, caret)));
        spans.add(Span.styled(under, Style.EMPTY.reversed()));
        spans.add(Span.raw(after));
        if (text.isEmpty()) {
            spans.add(Span.styled(PLACEHOLDER, Style.EMPTY.dim()));
        }
        return richText(Text.from(Line.from(spans))).length(1);
    }

    private Element hint() {
        return text(HINTS).dim().length(1);
    }

    private static String oneLine(String message) {
        var flat = message.replace('\n', ' ').replace('\r', ' ').strip();
        return flat.length() <= MAX_FAILURE_WIDTH ? flat : flat.substring(0, MAX_FAILURE_WIDTH - 1) + "…";
    }
}
