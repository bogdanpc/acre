package dev;

import dev.applecontainer.AppleContainerCli;
import dev.containers.ContainersTab;
import dev.images.ImagesTab;
import dev.tamboui.toolkit.app.ToolkitApp;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.tui.TuiConfig;
import dev.ui.KeyBindings;
import dev.ui.MainController;
import dev.ui.MainView;
import dev.ui.SystemController;
import dev.volumes.VolumesTab;

import java.time.Duration;
import java.util.List;


public class AppTui extends ToolkitApp {

    private static final String WINDOW_TITLE = "acre";

    private final MainView view;
    private final TuiConfig config;

    public static AppTui create() {
        return create(AppleContainerCli.builder().build());
    }

    public static AppTui create(AppleContainerCli cli) {
        return new AppTui(defaultConfig(), cli);
    }

    public static TuiConfig defaultConfig() {
        return TuiConfig.builder()
                .mouseCapture(false)
                .bindings(KeyBindings.get())
                .pollTimeout(Duration.ofMillis(10))
                .tickRate(Duration.ofMillis(33))
                .build();
    }

    public AppTui(TuiConfig config, AppleContainerCli cli) {
        this.config = config;

        var tabs = List.of(ContainersTab.of(cli).tab(), ImagesTab.of(cli).tab(), VolumesTab.of(cli).tab());

        this.view = MainView.of(new MainController(tabs, SystemController.of(cli), this::quit));
    }

    @Override
    protected void onStart() {
        setWindowTitle(WINDOW_TITLE);
    }

    @Override
    protected Element render() {
        return view;
    }

    @Override
    protected TuiConfig configure() {
        return config;
    }
}
