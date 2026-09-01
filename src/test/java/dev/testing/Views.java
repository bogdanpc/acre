package dev.testing;

import dev.applecontainer.CliResult;
import dev.images.ContainerImage;
import dev.images.ImagesView;
import dev.ui.MainView;
import dev.ui.Tab;
import dev.ui.TableController;
import dev.volumes.Volume;
import dev.volumes.VolumesView;

import java.util.List;

/** Views for tests */
public final class Views {

    private Views() {
    }

    /** App with an empty Images and an empty Volumes tab. */
    public static MainView app(Runnable onQuit) {
        return new MainView(onQuit, imagesAndVolumes());
    }

    public static List<Tab> imagesAndVolumes() {
        var images = new TableController<ContainerImage>(
                "Images", () -> CliResult.success(List.of()), Runnable::run);
        var volumes = new TableController<Volume>(
                "Volumes", () -> CliResult.success(List.of()), Runnable::run);
        return List.of(Tab.of(ImagesView.of(images)), Tab.of(VolumesView.of(volumes)));
    }
}
