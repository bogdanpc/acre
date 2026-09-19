package dev.volumes;

import dev.applecontainer.AppleContainerCli;
import dev.containers.Container;
import dev.containers.ContainerCommands;
import dev.tamboui.toolkit.element.StyledElement;
import dev.tamboui.tui.bindings.Actions;
import dev.ui.Action;
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

    private boolean detail;

    public static VolumesTab of(AppleContainerCli cli) {
        var volumes = new TableController<>(TITLE, new VolumeCommands(cli)::list);
        return new VolumesTab(volumes, new Loader<>(new ContainerCommands(cli)::list, List.of()));
    }

    public VolumesTab(TableController<Volume> table, Loader<List<Container>> containers) {
        this.table = table;
        this.containers = containers;
        this.tableView = VolumesView.of(table);
    }

    public Tab tab() {
        return new Tab(TITLE, this::element, this::actions);
    }

    public StyledElement<?> element() {
        var volume = detail ? table.selected() : Optional.<Volume>empty();
        if (volume.isEmpty()) {
            detail = false;
            return tableView.element(actions());
        }
        return tableView.page(VolumeDetailView.page(volume.get(), VolumeUse.of(containers.value(), volume.get())),
                actions());
    }

    public List<Action> actions() {
        if (detail) {
            return List.of(
                    new Action(KeyBindings.RELOAD, "reload the volumes list", this::reload),
                    new Action(Actions.CANCEL, "back to the volume list", this::close));
        }
        return Stream.concat(
                        tableView.actions().stream(),
                        Stream.of(new Action(Actions.SELECT, "show volume details", this::open)))
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
