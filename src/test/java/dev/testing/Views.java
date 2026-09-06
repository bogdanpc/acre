package dev.testing;

import dev.applecontainer.CliResult;
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
        var controller = new MainController(imagesAndVolumes(), Loader.of(AppleContainerStatus.UNKNOWN), onQuit);
        return new MainView(controller, new MainKeyHandler(controller));
    }

    public static List<Tab> imagesAndVolumes() {
        var images = new TableController<ContainerImage>(
                "Images", () -> CliResult.success(List.of()), Runnable::run);
        var volumes = new TableController<Volume>(
                "Volumes", () -> CliResult.success(List.of()), Runnable::run);
        return List.of(Tab.of(ImagesView.of(images)), Tab.of(VolumesView.of(volumes)));
    }
}
