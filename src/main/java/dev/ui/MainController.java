package dev.ui;

import dev.palette.PaletteController;
import dev.tamboui.tui.bindings.Actions;
import dev.tamboui.widgets.tabs.TabsState;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MainController {
    private final List<Tab> tabs;
    private final Runnable quit;
    private final TabsState state;
    private final SystemController system;
    private final PaletteController palette = new PaletteController();
    private final HelpController helpController = new HelpController();

    public MainController(List<Tab> tabs, SystemController system, Runnable onQuit) {
        this.tabs = List.copyOf(tabs);
        this.quit = onQuit;
        this.system = system;
        this.state = new TabsState(0);
    }

    public List<Tab> tabs() {
        return tabs;
    }

    /// What the user can do from anywhere, whichever tab is shown.
    public List<Action> actions() {
        var actions = new ArrayList<>(system.actions());
        IntStream.range(0, Math.min(tabs.size(), KeyBindings.MAX_TABS))
                .mapToObj(index -> new Action(KeyBindings.selectTab(index + 1),
                        "open " + tabs.get(index).title().toLowerCase(), () -> selectTab(index)))
                .forEach(actions::add);
        actions.add(new Action(KeyBindings.TOGGLE_HELP, "show the keys", helpController::toggle));
        actions.add(new Action(Actions.QUIT, "quit", this::quit));
        return actions;
    }

    public void openPalette() {
        palette.open(Stream.concat(selectedTab().actions().get().stream(), actions().stream())
                .map(Action::command)
                .toList());
    }

    public PaletteController palette() {
        return palette;
    }

    public HelpController help() {
        return helpController;
    }

    public boolean panelVisible() {
        return palette.visible() || helpController.isVisible();
    }

    public TabsState tabsState() {
        return state;
    }

    public Tab selectedTab() {
        var index = state.selected();
        return tabs.get(index == null ? 0 : index);
    }

    public void quit() {
        quit.run();
    }

    public void selectTab(int index) {
        if (index >= 0 && index < tabs.size()) {
            state.select(index);
        }
    }

    public void nextTab() {
        state.selectNext(tabs.size());
    }

    public void prevTab() {
        state.selectPrevious(tabs.size());
    }

    AppleContainerStatus status() {
        return system.status().value();
    }
}
