package dev.containers;

import dev.applecontainer.CliResult;
import dev.ui.Action;
import dev.ui.KeyBindings;
import dev.ui.TableController;

import java.util.List;
import java.util.function.Function;

public class ContainersController {

    private final ContainerCommands commands;
    private final TableController<Container> controller;

    public ContainersController(ContainerCommands commands, TableController<Container> controller) {
        this.commands = commands;
        this.controller = controller;
    }

    public List<Action> actions() {
        return List.of(
                new Action(KeyBindings.START, "start container", this::start),
                new Action(KeyBindings.STOP, "stop container", this::stop),
                new Action(KeyBindings.RESTART, "restart container", this::restart),
                Action.unbound("delete container", this::delete),
                new Action(KeyBindings.PRUNE, "prune", this::prune));
    }

    public void start() {
        onSelected(commands::start);
    }

    public void stop() {
        onSelected(commands::stop);
    }

    public void restart() {
        onSelected(commands::restart);
    }

    public void delete() {
        onSelected(commands::delete);
    }

    public void prune() {
        controller.execute(commands::prune);
    }

    private void onSelected(Function<String, CliResult<?>> action) {
        controller.selected()
                .map(Container::id)
                .ifPresent(id -> controller.execute(() -> action.apply(id)));
    }
}
