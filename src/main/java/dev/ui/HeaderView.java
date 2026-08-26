package dev.ui;

import static dev.tamboui.toolkit.Toolkit.row;
import static dev.tamboui.toolkit.Toolkit.text;

import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.elements.TabsElement;


public final class HeaderView {

    private static final String BRAND = " acre ";
    private static final String DOT = "●";

    private final Loader<AppleContainerStatus> status;

    public HeaderView(Loader<AppleContainerStatus> status) {
        this.status = status;
    }

    /**
     * Header. The status starts as {@link AppleContainerStatus#UNKNOWN} and turns green or red on
     * a later frame, once the loader has an answer.
     */
    public Element element(TabsElement tabBar) {
        var current = status.value();
        return row(
                text(BRAND).bold().fit(),
                tabBar.fill(),
                text("  " + DOT + " ").fg(current.color()).fit(),
                text(current.label()).fg(current.color()).fit()
        );
    }
}
