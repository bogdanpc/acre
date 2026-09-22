package dev.images;

import dev.ui.Action;
import dev.ui.KeyBindings;
import dev.ui.TableController;

import java.util.List;

public class ImagesController {

    private final ImageCommands commands;
    private final TableController<ContainerImage> controller;

    ImagesController(ImageCommands commands, TableController<ContainerImage> controller) {
        this.commands = commands;
        this.controller = controller;
    }

    List<Action> actions() {
        return List.of(new Action(KeyBindings.DELETE, "delete image", this::delete));
    }

    void delete() {
        controller.selected()
                .map(ContainerImage::reference)
                .ifPresent(r -> controller.execute(() -> commands.delete(r)));
    }
}
