package dev.ui;

import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.tui.bindings.ActionHandler;
import dev.tamboui.tui.bindings.Actions;
import dev.tamboui.tui.event.KeyEvent;

public class MainKeyHandler {

    private final ActionHandler actions;
    private final MainController controller;

    public MainKeyHandler(MainController controller) {
        this.controller = controller;
        this.actions = Action.handler(controller.actions())
                .on(Actions.MOVE_RIGHT, _ -> controller.nextTab())
                .on(Actions.MOVE_LEFT, _ -> controller.prevTab())
                .on(KeyBindings.OPEN_PALETTE, _ -> controller.openPalette());
    }

    public EventResult handle(KeyEvent keyEvent) {
        if(controller.palette().visible()){
            controller.palette().handle(keyEvent);
            return EventResult.HANDLED;
        }
        if (controller.help().isVisible()) {
            controller.help().handle(keyEvent);
            return EventResult.HANDLED;
        }
        return actions.dispatch(keyEvent) ? EventResult.HANDLED : EventResult.UNHANDLED;
    }
}
