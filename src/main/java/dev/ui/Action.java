package dev.ui;

import dev.palette.Command;
import dev.tamboui.tui.bindings.ActionHandler;

import java.util.List;

public record Action(String id, String label, Runnable run) {

    public static Action unbound(String label, Runnable run) {
        return new Action(null, label, run);
    }

    public static ActionHandler handler(List<Action> actions) {
        var handler = new ActionHandler(KeyBindings.get());
        actions.stream()
                .filter(action -> action.id != null)
                .forEach(action -> handler.on(action.id, _ -> action.run.run()));
        return handler;
    }

    public String keys() {
        return id == null ? "" : KeyBindings.keys(id);
    }

    public Command command() {
        var key = id == null ? "" : KeyBindings.key(id);
        return key.isEmpty() ? new Command(label, run) : new Command(label, key, run);
    }
}
