package dev.ui;

import dev.palette.PaletteController;
import dev.tamboui.widgets.tabs.TabsState;

import java.util.List;

public class MainController {
    private final List<Tab> tabs;
    private final Runnable quit;
    private final TabsState state;
    private final Loader<AppleContainerStatus> status;
    private final PaletteController palette = new PaletteController();
    private final HelpController helpController = new HelpController();

    public MainController(List<Tab> tabs, Loader<AppleContainerStatus> status, Runnable onQuit) {
        this.tabs = List.copyOf(tabs);
        this.quit = onQuit;
        this.status = status;
        this.state = new TabsState(0);
    }

    public List<Tab> tabs() {
        return tabs;
    }

    Loader<AppleContainerStatus> status() {
        return status;
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
}
