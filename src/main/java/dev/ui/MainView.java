package dev.ui;

import static dev.tamboui.toolkit.Toolkit.dock;
import static dev.tamboui.toolkit.Toolkit.length;
import static dev.tamboui.toolkit.Toolkit.stack;
import static dev.tamboui.toolkit.Toolkit.tabs;

import dev.containers.ContainersView;
import dev.images.ImagesView;
import dev.tamboui.layout.Rect;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.terminal.Frame;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.element.RenderContext;
import dev.tamboui.toolkit.element.Size;
import dev.tamboui.toolkit.elements.TabsElement;
import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.widgets.tabs.TabsState;
import dev.volumes.VolumesView;

public final class MainView implements Element {

    private static final String[] TAB_TITLES = {"Containers", "Images", "Volumes"};

    private final TabsState tabsState = new TabsState(0);

    private final ContainersView containers = new ContainersView();
    private final ImagesView images = new ImagesView();
    private final VolumesView volumes = new VolumesView();
    private final HeaderView header = new HeaderView();
    private final HelpView help = new HelpView();
    private final FooterView footer = new FooterView();

    private final Runnable onQuit;

    public MainView(Runnable onQuit) {
        this.onQuit = onQuit;
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
        int[] counts = {containers.count(), images.count(), volumes.count()};
        String[] titles = new String[TAB_TITLES.length];
        for (int i = 0; i < TAB_TITLES.length; i++) {
            titles[i] = TAB_TITLES[i] + " (" + counts[i] + ")";
        }
        return titles;
    }

    private Element content() {
        int index = tabsState.selected() == null ? 0 : tabsState.selected();
        return switch (index) {
            case 1 -> images.element();
            case 2 -> volumes.element();
            default -> containers.element();
        };
    }

    private EventResult onKey(KeyEvent event) {
        if (event.isChar('?')) {
            help.toggle();
            return EventResult.HANDLED;
        }
        if (help.visible()) {
            onHelpKey(event);
            return EventResult.HANDLED;
        }
        if (event.isRight() || event.isCharIgnoreCase('l')) {
            tabsState.selectNext(TAB_TITLES.length);
            return EventResult.HANDLED;
        }
        if (event.isLeft() || event.isCharIgnoreCase('h')) {
            tabsState.selectPrevious(TAB_TITLES.length);
            return EventResult.HANDLED;
        }
        for (int i = 0; i < TAB_TITLES.length; i++) {
            if (event.isChar((char) ('1' + i))) {
                tabsState.select(i);
                return EventResult.HANDLED;
            }
        }
        if (event.isQuit() || event.isCharIgnoreCase('q')) {
            onQuit.run();
            return EventResult.HANDLED;
        }
        return EventResult.UNHANDLED;
    }

    private void onHelpKey(KeyEvent event) {
        if (event.isCancel()) {
            help.hide();
            return;
        }
        if (event.isQuit() || event.isCharIgnoreCase('q')) {
            onQuit.run();
        }
    }
}
