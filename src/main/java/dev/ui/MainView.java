package dev.ui;

import static dev.tamboui.toolkit.Toolkit.dock;
import static dev.tamboui.toolkit.Toolkit.length;
import static dev.tamboui.toolkit.Toolkit.stack;
import static dev.tamboui.toolkit.Toolkit.tabs;

import dev.tamboui.layout.Rect;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.terminal.Frame;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.element.RenderContext;
import dev.tamboui.toolkit.element.Size;
import dev.tamboui.toolkit.elements.TabsElement;
import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.tui.bindings.ActionHandler;
import dev.tamboui.tui.bindings.Actions;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.widgets.tabs.TabsState;

import java.util.List;

public final class MainView implements Element {

    private final TabsState tabsState = new TabsState(0);

    private final List<Tab> tabs;
    private final HeaderView header;
    private final HelpView help = new HelpView();
    private final FooterView footer = new FooterView();

    private final ActionHandler actions;
    private final ActionHandler helpActions;

    public MainView(Runnable onQuit, List<Tab> tabs) {
        this(onQuit, Loader.of(AppleContainerStatus.UNKNOWN), tabs);
    }

    public MainView(Runnable onQuit, Loader<AppleContainerStatus> status, List<Tab> tabs) {
        this.tabs = List.copyOf(tabs);
        this.header = new HeaderView(status);
        this.actions = actions(onQuit);
        this.helpActions = helpActions(onQuit);
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
        Element base = dock()
                .top(header.element(tabBar()), length(1))
                .center(selected().content().get())
                .bottom(footer.element(), length(1))
                .id("root")
                .onKeyEvent(this::onKey);
        if (!help.visible()) {
            return base;
        }
        return stack(base, help.element());
    }

    private TabsElement tabBar() {
        return tabs(tabTitles())
                .state(tabsState)
                .divider("  ")
                .highlightStyle(Style.EMPTY.bold().fg(Color.CYAN));
    }

    private String[] tabTitles() {
        return tabs.stream()
                .map(Tab::title)
                .toArray(String[]::new);
    }

    private Tab selected() {
        var index = tabsState.selected();
        return tabs.get(index == null ? 0 : index);
    }

    private EventResult onKey(KeyEvent event) {
        if (help.visible()) {
            helpActions.dispatch(event);
            return EventResult.HANDLED;
        }
        return actions.dispatch(event) ? EventResult.HANDLED : EventResult.UNHANDLED;
    }

    private ActionHandler actions(Runnable onQuit) {
        var handler = new ActionHandler(KeyBindings.get())
                .on(KeyBindings.TOGGLE_HELP, event -> help.toggle())
                .on(Actions.MOVE_RIGHT, event -> tabsState.selectNext(tabs.size()))
                .on(Actions.MOVE_LEFT, event -> tabsState.selectPrevious(tabs.size()))
                .on(Actions.QUIT, event -> onQuit.run());
        for (int i = 0; i < Math.min(tabs.size(), KeyBindings.MAX_TABS); i++) {
            var index = i;
            handler.on(KeyBindings.selectTab(i + 1), event -> tabsState.select(index));
        }
        return handler;
    }

    private ActionHandler helpActions(Runnable onQuit) {
        return new ActionHandler(KeyBindings.get())
                .on(KeyBindings.TOGGLE_HELP, event -> help.toggle())
                .on(Actions.CANCEL, event -> help.hide())
                .on(Actions.QUIT, event -> onQuit.run());
    }
}
