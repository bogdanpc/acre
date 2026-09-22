package dev.volumes;

import dev.ui.Action;
import dev.ui.KeyBindings;
import dev.ui.TableController;

import java.util.List;

public class VolumesController {


    private final VolumeCommands commands;
    private final TableController<Volume> controller;

    VolumesController(VolumeCommands commands, TableController<Volume> controller) {
        this.commands = commands;
        this.controller = controller;
    }

    List<Action> actions() {
        return List.of(new Action(KeyBindings.DELETE, "delete the volume", this::remove));
    }

    void remove() {
        controller.selected()
                .map(Volume::name)
                .ifPresent(n -> controller.execute(() -> commands.remove(n)));
    }
}
