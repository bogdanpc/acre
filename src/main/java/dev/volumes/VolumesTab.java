package dev.volumes;

import static dev.tamboui.toolkit.Toolkit.stack;

import dev.applecontainer.AppleContainerCli;
import dev.containers.Container;
import dev.containers.ContainerCommands;
import dev.palette.Command;
import dev.tamboui.toolkit.element.StyledElement;
import dev.tamboui.tui.bindings.ActionHandler;
import dev.tamboui.tui.bindings.Actions;
import dev.ui.KeyBindings;
import dev.ui.Loader;
import dev.ui.Tab;
import dev.ui.TableController;
import dev.ui.TableView;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class VolumesTab {

    private static final String TITLE = "Volumes";

    private final TableController<Volume> table;
    private final TableView<Volume> tableView;
    private final Loader<List<Container>> containers;
    private final ActionHandler detailActions;

    private boolean detail;

    public static VolumesTab of(AppleContainerCli cli) {
        var volumes = new TableController<>(TITLE, new VolumeCommands(cli)::list);
        return new VolumesTab(volumes, new Loader<>(new ContainerCommands(cli)::list, List.of()));
    }

    public VolumesTab(TableController<Volume> table, Loader<List<Container>> containers) {
        this.table = table;
        this.containers = containers;
        this.tableView = VolumesView.of(table);
        this.tableView.on(Actions.SELECT, this::open);
        this.detailActions = new ActionHandler(KeyBindings.get())
                .on(Actions.CANCEL, _ -> close())
                .on(Actions.MOVE_DOWN, _ -> table.moveDown())
                .on(Actions.MOVE_UP, _ -> table.moveUp())
                .on(KeyBindings.RELOAD, _ -> reload());
    }

    public Tab tab() {
        return new Tab(TITLE, this::element, this::commands);
    }

    public StyledElement<?> element() {
        var volume = detail ? table.selected() : Optional.<Volume>empty();
        if (volume.isEmpty()) {
            detail = false;
            return tableView.element();
        }
        return stack(VolumeDetailView.page(volume.get(), VolumeUse.of(containers.value(), volume.get())))
                .id(TITLE)
                .focusable()
                .onAction(detailActions);
    }

    public List<Command> commands() {
        return Stream.concat(
                        tableView.commands().stream(),
                        Stream.of(detail
                                ? new Command("back to the volume list",
                                        KeyBindings.shortcutKey(Actions.CANCEL), this::close)
                                : new Command("show volume details",
                                        KeyBindings.shortcutKey(Actions.SELECT), this::open)))
                .toList();
    }

    private void reload() {
        table.reload();
        containers.reload();
    }

    private void open() {
        containers.reload();
        detail = true;
    }

    private void close() {
        detail = false;
    }
}
