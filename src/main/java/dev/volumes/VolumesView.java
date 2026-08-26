package dev.volumes;

import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.toolkit.Toolkit;
import dev.ui.Align;
import dev.ui.TableController;
import dev.ui.TableView;

import java.util.List;

/** The columns of the "Volumes" tab. */
public final class VolumesView {

    private static final String UNITS = "KMGTP";

    private VolumesView() {
    }

    public static TableView<Volume> of(TableController<Volume> controller) {
        return new TableView<>(controller, List.of(
                TableView.Column.of("NAME", Toolkit.length(20), Volume::name)
                        .style(_ -> Style.EMPTY.fg(Color.CYAN)),
                TableView.Column.of("DRIVER", Toolkit.length(8), Volume::driver),
                TableView.Column.of("FORMAT", Toolkit.length(8), Volume::format),
                TableView.Column.<Volume>of("SIZE", Toolkit.length(10), volume -> size(volume.size()))
                        .align(Align.RIGHT),
                TableView.Column.of("SOURCE", Toolkit.fill(), Volume::source)
                        .style(_ -> Style.EMPTY.dim())));
    }

    static String size(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        var unit = Math.min((int) (Math.log(bytes) / Math.log(1024)), UNITS.length());
        return "%.1f %sB".formatted(bytes / Math.pow(1024, unit), UNITS.charAt(unit - 1));
    }
}
