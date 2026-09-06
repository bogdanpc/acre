package dev.ui;

import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.tui.bindings.ActionHandler;
import dev.tamboui.tui.bindings.Actions;
import dev.tamboui.tui.event.KeyEvent;

public class MainKeyHandler {

    private final ActionHandler actions;
    private final MainCommands mainCommands;
    private final MainController controller;

    public MainKeyHandler(MainController controller) {
        this.controller = controller;
        this.actions = actions(controller);
        this.mainCommands = new MainCommands(controller);
    }

    private ActionHandler actions(MainController controller) {
        var actions = new ActionHandler(KeyBindings.get())
                .on(Actions.MOVE_RIGHT, _ -> controller.nextTab())
                .on(Actions.MOVE_LEFT, _ -> controller.prevTab())
                .on(KeyBindings.TOGGLE_HELP, _ -> controller.help().toggle())
                .on(KeyBindings.OPEN_PALETTE, _ -> controller.palette().open(this.mainCommands.all()))
                .on(Actions.QUIT, _ -> controller.quit());

        var tabs = controller.tabs();
        for (int i = 0; i < Math.min(tabs.size(), KeyBindings.MAX_TABS); i++) {
            var index = i;
            actions.on(KeyBindings.selectTab(i + 1), _ -> controller.selectTab(index));
        }
        return actions;
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
