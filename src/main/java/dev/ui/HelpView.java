package dev.ui;

import static dev.tamboui.toolkit.Toolkit.dialog;
import static dev.tamboui.toolkit.Toolkit.markupText;

import dev.tamboui.style.Color;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.tui.bindings.Actions;

import java.util.List;

/**
 * Help / About view
 */
public final class HelpView {

    private static final Color ACCENT = Color.rgb(122, 162, 247);

    private static final String SECTION_OPEN = "[bold #7aa2f7]";

    private static final String KEY_OPEN = "[#7aa2f7]";

    private static final int KEY_WIDTH = 14;

    private static final int PADDING = 2;

    private static final List<Action> MOVES = List.of(
            new Action(Actions.MOVE_UP, "up", () -> {}),
            new Action(Actions.MOVE_DOWN, "down", () -> {}),
            new Action(Actions.MOVE_RIGHT, "next tab", () -> {}),
            new Action(Actions.MOVE_LEFT, "previous tab", () -> {}),
            new Action(KeyBindings.OPEN_PALETTE, "this tab's commands and keys", () -> {}));

    public Element element(List<Action> app) {
        var body = body(app);
        return dialog(" Keys ", markupText(body))
                .rounded()
                .borderColor(ACCENT)
                .padding(PADDING)
                .width(longestLine(body) + PADDING * 2 + 2);
    }

    private static String body(List<Action> app) {
        StringBuilder out = new StringBuilder();

        section(out, "move");
        entries(out, MOVES);
        section(out, "app");
        entries(out, app);

        section(out, "about");
        line(out, AboutText.NAME + " " + AboutText.version() + " — " + AboutText.TITLE);
        line(out, AboutText.SUMMARY);
        line(out, AboutText.BUILT_WITH);

        out.append('\n');
        out.append(" [dim]press ? or esc to close[/]");
        return out.toString();
    }

    private static void section(StringBuilder out, String title) {
        out.append('\n').append(' ').append(SECTION_OPEN).append(title).append("[/]\n");
    }

    private static void entries(StringBuilder out, List<Action> actions) {
        actions.stream()
                .filter(action -> !action.keys().isEmpty())
                .forEach(action -> entry(out, action.keys(), action.label()));
    }

    private static void entry(StringBuilder out, String key, String action) {
        out.append("  ")
                .append(KEY_OPEN).append(escape(key)).append("[/]").repeat(" ", Math.max(1, KEY_WIDTH - key.length()))
                .append(action)
                .append('\n');
    }

    private static String escape(String text) {
        return text.replace("\\", "\\\\")
                .replace("[", "\\[")
                .replace("]", "\\]");
    }

    private static void line(StringBuilder out, String text) {
        out.append("   ").append(text).append('\n');
    }


    private static int longestLine(String markup) {
        int longest = 0;
        for (String line : markup.split("\n", -1)) {
            String plain = line.replaceAll("\\[[^]]*]", "").replace("\\\\", "\\");
            longest = Math.max(longest, plain.length());
        }
        return longest;
    }
}
