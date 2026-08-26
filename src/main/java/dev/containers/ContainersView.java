package dev.containers;

import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.toolkit.Toolkit;
import dev.ui.Align;
import dev.ui.TableController;
import dev.ui.TableView;

import java.util.List;

/** The columns of the "Containers" tab. */
public final class ContainersView {

    private ContainersView() {
    }

    public static TableView<Container> of(TableController<Container> controller) {
        return new TableView<>(controller, List.of(
                TableView.Column.of("ID", Toolkit.length(40), Container::id)
                        .style(_ -> Style.EMPTY.fg(Color.CYAN)),
                TableView.Column.of("IMAGE", Toolkit.fill(), Container::image),
                TableView.Column.of("STATE", Toolkit.length(10), Container::state)
                        .style(ContainersView::stateStyle),
                TableView.Column.of("ADDRESS", Toolkit.length(16), Container::address),
                TableView.Column.of("CPUS", Toolkit.length(5), (Container c) -> String.valueOf(c.cpus()))
                        .align(Align.RIGHT),
                TableView.Column.of("MEMORY", Toolkit.length(9), Container::memory)
                        .align(Align.RIGHT)));
    }

    static Style stateStyle(Container container) {
        var state = ContainerState.of(container.state());
        var style = Style.EMPTY.fg(state.color());
        return state == ContainerState.RUNNING || state == ContainerState.ERROR ? style.bold() : style;
    }
}
