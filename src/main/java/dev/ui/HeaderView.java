package dev.ui;

import static dev.tamboui.toolkit.Toolkit.row;
import static dev.tamboui.toolkit.Toolkit.text;

import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.elements.TabsElement;


public final class HeaderView {

    private static final String BRAND = " acre ";
    private static final String DOT = "●";

    /**
     * Header
     */
    public Element element(TabsElement tabBar) {
        var status = AppleContainerStatus.UNKNOWN;
        return row(
                text(BRAND).bold().fit(),
                tabBar.fill(),
                text("  " + DOT + " ").fg(status.color()).fit(),
                text(status.label()).fg(status.color()).fit()
        );
    }
}
