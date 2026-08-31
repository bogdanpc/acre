package dev;

import dev.applecontainer.AppleContainerCli;
import dev.applecontainer.SystemCommands;
import dev.containers.ContainersTab;
import dev.images.ImageCommands;
import dev.images.ImagesView;
import dev.tamboui.toolkit.app.ToolkitApp;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.tui.TuiConfig;
import dev.ui.AppleContainerStatus;
import dev.ui.KeyBindings;
import dev.ui.Loader;
import dev.ui.MainView;
import dev.ui.Tab;
import dev.ui.TableController;
import dev.volumes.VolumeCommands;
import dev.volumes.VolumesView;

import java.time.Duration;
import java.util.List;


public class AppTui extends ToolkitApp {

    private static final Duration STATUS_REFRESH = Duration.ofSeconds(5);

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
                .mouseCapture(true)
                .bindings(KeyBindings.get())
                .pollTimeout(Duration.ofMillis(10))
                .tickRate(Duration.ofMillis(33))
                .build();
    }

    public AppTui(TuiConfig config, AppleContainerCli cli) {
        this.config = config;

        var containers = ContainersTab.of(cli);

        var images = new TableController<>("Images", new ImageCommands(cli)::list);
        var volumes = new TableController<>("Volumes", new VolumeCommands(cli)::list);

        var tabs = List.of(
                containers.tab(),
                Tab.of(ImagesView.of(images)),
                Tab.of(VolumesView.of(volumes)));

        var system = new SystemCommands(cli);
        var status = new Loader<>(() -> AppleContainerStatus.of(system.isRunning()), AppleContainerStatus.UNKNOWN)
                .refreshEvery(STATUS_REFRESH);

        this.view = new MainView(this::quit, status, tabs);
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
