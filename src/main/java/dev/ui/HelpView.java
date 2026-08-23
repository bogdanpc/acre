package dev.ui;

import static dev.tamboui.toolkit.Toolkit.dialog;
import static dev.tamboui.toolkit.Toolkit.markupText;

import dev.tamboui.style.Color;
import dev.tamboui.toolkit.element.Element;

/**
 * Help / About view
 */
public final class HelpView {

    private static final Color ACCENT = Color.rgb(122, 162, 247);

    private static final String SECTION_OPEN = "[bold #7aa2f7]";

    private static final String KEY_OPEN = "[#7aa2f7]";

    private static final int KEY_WIDTH = 14;

    private static final int PADDING = 2;

    private static final String BODY = body();

    private static final int WIDTH = longestLine(BODY) + PADDING * 2 + 2;

    private boolean visible;

    public boolean visible() {
        return visible;
    }

    public void toggle() {
        visible = !visible;
    }

    public void hide() {
        visible = false;
    }

    public Element element() {
        return dialog(" Keys ", markupText(BODY))
                .rounded()
                .borderColor(ACCENT)
                .padding(PADDING)
                .width(WIDTH);
    }

    private static String body() {
        StringBuilder out = new StringBuilder();

        section(out, "move");
        entry(out, "j / k / ↑ ↓", "up and down");
        entry(out, "g / G", "first / last row");
        entry(out, "1 2 3", "containers / images / volumes");
        entry(out, "tab", "next tab");
        section(out, "app");

        entry(out, "^K / :", "command palette");
        entry(out, "?", "this help");
        entry(out, "q", "quit");

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
