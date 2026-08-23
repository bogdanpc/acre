package dev.ui;

import static dev.tamboui.toolkit.Toolkit.text;

import dev.tamboui.toolkit.element.Element;

public final class FooterView {

    private static final String HINTS = String.join(
            "  ·  ", "1-3 tabs", "h/l switch", "←/→ switch", "? help", "q quit");

    public Element element() {
        return text(HINTS).dim();
    }
}
