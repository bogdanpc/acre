package dev.ui;

import static dev.tamboui.toolkit.Toolkit.dock;
import static dev.tamboui.toolkit.Toolkit.length;
import static dev.tamboui.toolkit.Toolkit.stack;
import static dev.tamboui.toolkit.Toolkit.tabs;

import dev.palette.Command;
import dev.palette.PaletteController;
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
import dev.tamboui.tui.bindings.ActionHandler;
import dev.tamboui.tui.bindings.Actions;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.widgets.tabs.TabsState;

import java.util.ArrayList;
import java.util.List;

public final class MainView implements Element {

    private final TabsState tabsState = new TabsState(0);

    private final List<Tab> tabs;
    private final HeaderView header;
    private final HelpView help = new HelpView();
    private final PaletteController palette = new PaletteController();
    private final PaletteView paletteView = new PaletteView(palette);
    private final FooterView footer = new FooterView();

    private final ActionHandler actions;
    private final ActionHandler helpActions;
    private final Runnable quit;

    public MainView(Runnable onQuit, List<Tab> tabs) {
        this(onQuit, Loader.of(AppleContainerStatus.UNKNOWN), tabs);
    }

    public MainView(Runnable onQuit, Loader<AppleContainerStatus> status, List<Tab> tabs) {
        this.tabs = List.copyOf(tabs);
        this.header = new HeaderView(status);
        this.quit = onQuit;
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
                .center(content())
                .bottom(footer.element(), length(1))
                .id("root")
                .onKeyEvent(this::onKey);
        if (palette.visible()) {
            return stack(base, paletteView.element());
        }
        if (help.visible()) {
            return stack(base, help.element());
        }
        return base;
    }

    private StyledElement<?> content() {
        var element = selected().content().get();
        if (help.visible() || palette.visible()) {
            element.onKeyEvent(_ -> EventResult.UNHANDLED);
        }
        return element;
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
        if (palette.visible()) {
            palette.handle(event);
            return EventResult.HANDLED;
        }
        if (help.visible()) {
            helpActions.dispatch(event);
            return EventResult.HANDLED;
        }
        return actions.dispatch(event) ? EventResult.HANDLED : EventResult.UNHANDLED;
    }

    private ActionHandler actions(Runnable onQuit) {
        var handler = new ActionHandler(KeyBindings.get())
                .on(KeyBindings.TOGGLE_HELP, _ -> help.toggle())
                .on(KeyBindings.OPEN_PALETTE, _ -> palette.open(commands()))
                .on(Actions.MOVE_RIGHT, _ -> tabsState.selectNext(tabs.size()))
                .on(Actions.MOVE_LEFT, _ -> tabsState.selectPrevious(tabs.size()))
                .on(Actions.QUIT, _ -> onQuit.run());
        for (int i = 0; i < Math.min(tabs.size(), KeyBindings.MAX_TABS); i++) {
            var index = i;
            handler.on(KeyBindings.selectTab(i + 1), _ -> tabsState.select(index));
        }
        return handler;
    }

    /**
     * Palette's commands list. Current tab's commands are displayed first
     */
    private List<Command> commands() {
        var commands = new ArrayList<>(selected().commands().get());
        for (int i = 0; i < Math.min(tabs.size(), KeyBindings.MAX_TABS); i++) {
            var index = i;
            var tab = tabs.get(i);
            commands.add(new Command("open " + tab.title().toLowerCase(),
                    () -> tabsState.select(index)));
        }
        commands.add(new Command("show the keys", help::toggle));
        commands.add(new Command("quit", quit));
        return commands;
    }

    private ActionHandler helpActions(Runnable onQuit) {
        return new ActionHandler(KeyBindings.get())
                .on(KeyBindings.TOGGLE_HELP, _ -> help.toggle())
                .on(Actions.CANCEL, _ -> help.hide())
                .on(Actions.QUIT, _ -> onQuit.run());
    }
}
