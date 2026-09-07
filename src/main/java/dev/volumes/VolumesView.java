package dev.volumes;

import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.toolkit.Toolkit;
import dev.ui.Align;
import dev.ui.Format;
import dev.ui.TableController;
import dev.ui.TableView;

import java.util.List;

/** The columns of the "Volumes" tab. */
public final class VolumesView {

    private VolumesView() {
    }

    public static TableView<Volume> of(TableController<Volume> controller) {
        return new TableView<>(controller, List.of(
                TableView.Column.of("NAME", Toolkit.length(20), Volume::name)
                        .style(_ -> Style.EMPTY.fg(Color.CYAN)),
                TableView.Column.of("DRIVER", Toolkit.length(8), Volume::driver),
                TableView.Column.of("FORMAT", Toolkit.length(8), Volume::format),
                TableView.Column.<Volume>of("SIZE", Toolkit.length(10), volume -> Format.bytes(volume.size()))
                        .align(Align.RIGHT),
                TableView.Column.of("SOURCE", Toolkit.fill(), Volume::source)
                        .style(_ -> Style.EMPTY.dim())));
    }
}
