package dev.ui;

import dev.palette.PaletteView;
import dev.tamboui.layout.Rect;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.terminal.Frame;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.element.RenderContext;
import dev.tamboui.toolkit.element.Size;
import dev.tamboui.toolkit.element.StyledElement;
import dev.tamboui.toolkit.elements.TabsElement;
import dev.tamboui.toolkit.event.EventResult;

import java.util.List;

import static dev.tamboui.toolkit.Toolkit.*;

public final class MainView implements Element {

    private final List<Tab> tabs;

    private final HeaderView header;
    private final HelpView helpView = new HelpView();
    private final FooterView footer = new FooterView();
    private final PaletteView paletteView;

    private final MainController controller;
    private final MainKeyHandler keyHandler;

    public MainView(MainController controller, MainKeyHandler keyHandler) {
        this.controller = controller;
        this.keyHandler = keyHandler;
        this.tabs = List.copyOf(controller.tabs());
        this.header = new HeaderView(controller.status());
        this.paletteView = new PaletteView(controller.palette());
    }

    @Override
    public void render(Frame frame, Rect area, RenderContext context) {
        context.renderChild(root(), frame, area);
    }

    @Override
    public Size preferredSize(int availableWidth, int availableHeight, RenderContext context) {
        return Size.ZERO;
    }

    private Element root() {
        var base = dock()
                .top(header.element(tabBar()), length(1))
                .center(content())
                .bottom(footer.element(), length(1))
                .id("root")
                .onKeyEvent(keyHandler::handle);
        
        if (controller.palette().visible()) {
            return stack(base, paletteView.element());
        }
        if (controller.help().isVisible()) {
            return stack(base, helpView.element());
        }
        return base;
    }

    private StyledElement<?> content() {
        var element = selected().content().get();
        if (controller.panelVisible()) {
            element.onKeyEvent(_ -> EventResult.UNHANDLED);
        }
        return element;
    }

    private TabsElement tabBar() {
        return tabs(tabTitles())
                .state(controller.tabsState())
                .divider("  ")
                .highlightStyle(Style.EMPTY.bold().fg(Color.CYAN));
    }

    private String[] tabTitles() {
        return tabs.stream()
                .map(Tab::title)
                .toArray(String[]::new);
    }

    private Tab selected() {
        return controller.selectedTab();
    }
}
