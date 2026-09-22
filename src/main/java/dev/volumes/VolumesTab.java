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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class VolumesTab {

    private static final String TITLE = "Volumes";

    private final TableController<Volume> table;
    private final TableView<Volume> tableView;
    private final Loader<List<Container>> containers;
    private final VolumesController controller;

    private boolean detail;

    public static VolumesTab of(AppleContainerCli cli) {
        var commands = new VolumeCommands(cli);
        var table = new TableController<>(TITLE, commands::list);
        return new VolumesTab(table, new Loader<>(new ContainerCommands(cli)::list, List.of()),
                new VolumesController(commands, table));
    }

    public VolumesTab(TableController<Volume> table, Loader<List<Container>> containers, VolumesController controller) {
        this.table = table;
        this.containers = containers;
        this.controller = controller;
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
                    new Action(KeyBindings.DELETE, "remove volume", this::removeVolume),
                    new Action(Actions.CANCEL, "back to the volume list", this::close));
        }
        var actions = new ArrayList<Action>();
        actions.addAll(controller.actions());
        actions.addAll(tableView.actions());
        actions.add(new Action(Actions.SELECT, "show volume details", this::open));
        return actions;
    }

    private void reload() {
        table.reload();
        containers.reload();
    }

    private void open() {
        containers.reload();
        detail = true;
    }

    private void removeVolume() {
        containers.value();
        controller.remove();
    }

    private void close() {
        detail = false;
    }
}
