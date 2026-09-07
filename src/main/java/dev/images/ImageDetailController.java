package dev.images;

import dev.containers.Container;
import dev.ui.Loader;
import dev.ui.TableController;

import java.util.List;
import java.util.Optional;

public final class ImageDetailController {

    private final TableController<ContainerImage> table;
    private final Loader<List<Container>> containers;

    private boolean opened;

    public ImageDetailController(TableController<ContainerImage> table, Loader<List<Container>> containers) {
        this.table = table;
        this.containers = containers;
    }

    /**
     * Show the page. Empty if the page is closed.
     */
    public Optional<ImageDetail> detail() {
        return opened
                ? table.selected().map(image -> new ImageDetail(image, ImageUse.of(containers.value(), image)))
                : Optional.empty();
    }

    public void open() {
        containers.reload();
        opened = true;
    }

    public void close() {
        opened = false;
    }

    public void reload() {
        table.reload();
        containers.reload();
    }
}
