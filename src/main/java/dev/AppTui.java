package dev;

import dev.applecontainer.AppleContainerCli;
import dev.applecontainer.CliResult;
import dev.applecontainer.SystemCommands;
import dev.containers.ContainersTab;
import dev.images.ImagesTab;
import dev.tamboui.toolkit.app.ToolkitApp;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.tui.TuiConfig;
import dev.ui.*;
import dev.volumes.VolumesTab;

import java.time.Duration;
import java.util.List;


public class AppTui extends ToolkitApp {

    private static final Duration STATUS_REFRESH = Duration.ofSeconds(5);
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

        var containers = ContainersTab.of(cli);

        var images = ImagesTab.of(cli);
        var volumes = VolumesTab.of(cli);

        var tabs = List.of(
                containers.tab(),
                images.tab(),
                volumes.tab());

        var system = new SystemCommands(cli);
        var status = new Loader<>(
                () -> CliResult.success(AppleContainerStatus.of(system.isRunning())),
                AppleContainerStatus.UNKNOWN)
                .refreshEvery(STATUS_REFRESH);

        var mainController = new MainController(tabs, status, this::quit);
        this.view = new MainView(mainController, new MainKeyHandler(mainController));
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
