package dev.images;

import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.toolkit.Toolkit;
import dev.ui.TableController;
import dev.ui.TableView;

import java.util.List;

/** The columns of the "Images" tab. */
public final class ImagesView {

    private ImagesView() {
    }

    public static TableView<ContainerImage> of(TableController<ContainerImage> controller) {
        return new TableView<>(controller, List.of(
                TableView.Column.of("IMAGE", Toolkit.fill(), ContainerImage::reference)
                        .style(_ -> Style.EMPTY.fg(Color.CYAN))));
    }
}
