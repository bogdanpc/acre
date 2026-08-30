package dev.palette;

import static dev.tamboui.toolkit.Toolkit.dialog;
import static dev.tamboui.toolkit.Toolkit.list;
import static dev.tamboui.toolkit.Toolkit.richText;
import static dev.tamboui.toolkit.Toolkit.row;
import static dev.tamboui.toolkit.Toolkit.spacer;
import static dev.tamboui.toolkit.Toolkit.text;

import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.text.Line;
import dev.tamboui.text.Span;
import dev.tamboui.text.Text;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.element.StyledElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Command palette view:  Display matched list with a prompt.
 * Enter to run selected command
 */
public final class PaletteView {

    private static final Color ACCENT = Color.rgb(122, 162, 247);

    private static final Style SELECTED = Style.EMPTY.bg(Color.rgb(0x26, 0x4F, 0x78)).bold();
    private static final Style HIT = Style.EMPTY.fg(ACCENT).bold();

    private static final String MARKER = " ❯ ";
    private static final int MARKER_WIDTH = 3;

    private static final int WIDTH = 62;
    private static final int ROWS = 12;
    private static final int LABEL_WIDTH = WIDTH - 2 - MARKER_WIDTH;

    private static final int CHROME_HEIGHT = 6;

    private final PaletteController controller;

    public PaletteView(PaletteController controller) {
        this.controller = controller;
    }

    public Element element() {
        var matches = controller.matches();
        int rows = Math.clamp(matches.size(), 1, ROWS);
        return dialog(" Run a command ",
                prompt(), spacer(1), body(matches, rows), spacer(1), hint())
                .rounded()
                .borderColor(ACCENT)
                .width(WIDTH)
                .length(rows + CHROME_HEIGHT);
    }

    private Element body(List<Command> matches, int rows) {
        if (matches.isEmpty()) {
            return indented(text("no command matches").dim());
        }
        var items = new ArrayList<StyledElement<?>>(matches.size());
        for (var match : matches) {
            items.add(richText(highlight(match)));
        }
        return list(items.toArray(StyledElement<?>[]::new))
                .highlightSymbol(MARKER)
                .highlightStyle(SELECTED)
                .selected(controller.index())
                .autoScroll()
                .length(rows);
    }

    private Element prompt() {
        var text = controller.query().text();
        int caret = Math.clamp(controller.query().cursorPosition(), 0, text.length());
        var under = caret < text.length() ? text.substring(caret, caret + 1) : " ";
        var after = caret < text.length() ? text.substring(caret + 1) : "";
        return richText(Text.from(Line.from(
                Span.styled(" > ", Style.EMPTY.fg(ACCENT).bold()),
                Span.raw(text.substring(0, caret)),
                Span.styled(under, Style.EMPTY.reversed()),
                Span.raw(after)))).length(1);
    }

    private Element hint() {
        return indented(text("enter run · ↑ ↓ pick · esc close").dim());
    }

    private Element indented(Element element) {
        return row(spacer(MARKER_WIDTH), element).length(1);
    }

    /**
     * Draws the command label with the matched characters .
     */
    private Text highlight(Command command) {
        var label = truncate(command.label());
        var hit = new boolean[label.length()];
        var positions = Fuzzy.match(controller.query().text(), label)
                .map(Fuzzy.Match::positions)
                .orElse(new int[0]);
        for (int position : positions) {
            hit[position] = true;
        }

        var spans = createSpans(label, hit);
        return Text.from(Line.from(spans));
    }

    /**
     * Splits the label into spans. {@code hits} holds one flag per character: true where
     * the query matched.
     * e.g. "restart", type "rt", resulted  r | es | t | ar | t.
     */
    private List<Span> createSpans(String label, boolean[] hits) {
        var spans = new ArrayList<Span>();
        for (int start = 0; start < label.length(); ) {
            int end = start + 1;
            while (end < label.length() && hits[end] == hits[start]) {
                end++;
            }
            spans.add(Span.styled(label.substring(start, end), hits[start] ? HIT : Style.EMPTY));
            start = end;
        }
        return spans;
    }

    private static String truncate(String text) {
        return text.length() <= LABEL_WIDTH ? text : text.substring(0, Math.max(0, LABEL_WIDTH - 1)) + "…";
    }
}
