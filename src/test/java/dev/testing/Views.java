package dev.testing;

import dev.applecontainer.AppleContainerCli;
import dev.applecontainer.CliResult;
import dev.applecontainer.SystemCommands;
import dev.images.ContainerImage;
import dev.images.ImagesView;
import dev.ui.*;
import dev.volumes.Volume;
import dev.volumes.VolumesView;

import java.util.List;

/**
 * Views for tests
 */
public final class Views {

    private Views() {
    }

    /**
     * App with an empty Images and an empty Volumes tab.
     */
    public static MainView app(Runnable onQuit) {
        return app(imagesAndVolumes(), noSystem(), onQuit);
    }

    /**
     * App whose system commands run on the test thread.
     */
    public static MainView app(SystemCommands system, Runnable onQuit) {
        return app(imagesAndVolumes(), system, onQuit);
    }

    public static MainView app(List<Tab> tabs) {
        return app(tabs, noSystem(), () -> {});
    }

    private static MainView app(List<Tab> tabs, SystemCommands system, Runnable onQuit) {
        return MainView.of(new MainController(tabs, SystemController.of(system, Runnable::run), onQuit));
    }

    private static SystemCommands noSystem() {
        return new SystemCommands(AppleContainerCli.builder().executable("no-such-container").build());
    }

    public static List<Tab> imagesAndVolumes() {
        var images = new TableController<ContainerImage>(
                "Images", () -> CliResult.success(List.of()), Runnable::run);
        var volumes = new TableController<Volume>(
                "Volumes", () -> CliResult.success(List.of()), Runnable::run);
        return List.of(Tab.of(ImagesView.of(images)), Tab.of(VolumesView.of(volumes)));
    }
}
