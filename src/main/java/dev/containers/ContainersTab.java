package dev.containers;

import static dev.tamboui.toolkit.Toolkit.stack;

import dev.applecontainer.AppleContainerCli;
import dev.palette.Command;
import dev.tamboui.toolkit.element.StyledElement;
import dev.tamboui.tui.bindings.ActionHandler;
import dev.tamboui.tui.bindings.Actions;
import dev.ui.KeyBindings;
import dev.ui.Tab;
import dev.ui.TableController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class ContainersTab {

    private static final String TITLE = "Containers";

    private final TableController<Container> table;
    private final ContainersController actions;
    private final ContainersView tableView;
    private final ActionHandler detailActions;

    private boolean detail;

    public static ContainersTab of(AppleContainerCli cli) {
        var commands = new ContainerCommands(cli);
        return new ContainersTab(commands, new TableController<>(TITLE, commands::list));
    }

    public ContainersTab(ContainerCommands commands, TableController<Container> table) {
        this.table = table;
        this.actions = new ContainersController(commands, table);
        this.tableView = ContainersView.of(table, actions);
        this.tableView.on(Actions.SELECT, this::open);
        this.detailActions = new ActionHandler(KeyBindings.get())
                .on(Actions.CANCEL, _ -> close())
                .on(Actions.MOVE_DOWN, _ -> table.moveDown())
                .on(Actions.MOVE_UP, _ -> table.moveUp())
                .on(KeyBindings.RELOAD, _ -> table.reload());
        actions.shortcuts().forEach((action, run) -> this.detailActions.on(action, _ -> run.run()));
    }

    public Tab tab() {
        return new Tab(TITLE, this::element, this::commands);
    }

    public StyledElement<?> element() {
        var container = detail ? table.selected() : Optional.<Container>empty();
        if (container.isEmpty()) {
            detail = false;
            return tableView.element();
        }
        return stack(ContainerDetailView.page(container.get()))
                .id(TITLE)
                .focusable()
                .onAction(detailActions);
    }

    public List<Command> commands() {
        return Stream.of(
                        tableView.commands().stream(),
                        actions.commands().stream(),
                        Stream.of(detail
                                ? new Command("back to the container list",
                                        KeyBindings.shortcutKey(Actions.CANCEL), this::close)
                                : new Command("show container details",
                                        KeyBindings.shortcutKey(Actions.SELECT), this::open)))
                .flatMap(commands -> commands)
                .toList();
    }

    private void open() {
        detail = true;
    }

    private void close() {
        detail = false;
    }
}
