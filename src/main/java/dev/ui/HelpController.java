package dev.ui;

import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.tui.bindings.ActionHandler;
import dev.tamboui.tui.bindings.Actions;
import dev.tamboui.tui.event.KeyEvent;

public class HelpController {

    private boolean visible;
    private final ActionHandler actions = new ActionHandler(KeyBindings.get())
            .on(Actions.CANCEL, _ -> close())
            .on(KeyBindings.TOGGLE_HELP, _ -> close());

    public void toggle() {
        visible = !visible;
    }

    public void close() {
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public EventResult handle(KeyEvent event) {
        return actions.dispatch(event) ? EventResult.HANDLED : EventResult.UNHANDLED;
    }
}
