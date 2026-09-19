package dev.containers;

import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.toolkit.Toolkit;
import dev.ui.Align;
import dev.ui.TableController;
import dev.ui.TableView;

import java.util.List;

/** The table of the "Containers" tab, with its columns. */
public final class ContainersView extends TableView<Container> {

    private ContainersView(TableController<Container> controller) {
        super(controller, columns());
    }

    public static ContainersView of(TableController<Container> controller) {
        return new ContainersView(controller);
    }

    private static List<Column<Container>> columns() {
        return List.of(
                Column.of("ID", Toolkit.length(40), Container::id)
                        .style(_ -> Style.EMPTY.fg(Color.CYAN)),
                Column.of("IMAGE", Toolkit.fill(), Container::image),
                Column.of("STATE", Toolkit.length(10), Container::state)
                        .style(ContainersView::stateStyle),
                Column.of("ADDRESS", Toolkit.length(16), Container::address),
                Column.of("CPUS", Toolkit.length(5), (Container c) -> String.valueOf(c.cpus()))
                        .align(Align.RIGHT),
                Column.of("MEMORY", Toolkit.length(9), Container::memory)
                        .align(Align.RIGHT));
    }

    static Style stateStyle(Container container) {
        var state = ContainerState.of(container.state());
        var style = Style.EMPTY.fg(state.color());
        return state == ContainerState.RUNNING || state == ContainerState.ERROR ? style.bold() : style;
    }
}
