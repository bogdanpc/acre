package dev.containers;

import dev.palette.Command;
import dev.ui.KeyBindings;
import dev.ui.TableController;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ContainersController {

    private final ContainerCommands commands;
    private final TableController<Container> controller;

    public ContainersController(ContainerCommands commands, TableController<Container> controller) {
        this.commands = commands;
        this.controller = controller;
    }

    public Map<String, Runnable> shortcuts() {
        return Map.of(
                KeyBindings.START, this::start,
                KeyBindings.STOP, this::stop,
                KeyBindings.RESTART, this::restart);
    }

    public List<Command> commands() {

        return List.of(
                new Command("start container", KeyBindings.shortcutKey(KeyBindings.START), this::start),
                new Command("stop container", KeyBindings.shortcutKey(KeyBindings.STOP), this::stop),
                new Command("restart container", KeyBindings.shortcutKey(KeyBindings.RESTART), this::restart),
                new Command("delete container", this::delete),
                new Command("prune", KeyBindings.shortcutKey(KeyBindings.PRUNE), this::prune));
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

    private void onSelected(Consumer<String> action) {
        controller.selected()
                .map(Container::id)
                .ifPresent(id -> controller.execute(() -> action.accept(id)));
    }
}
