package dev.ui;

import dev.tamboui.tui.bindings.Actions;
import dev.tamboui.tui.bindings.BindingSets;
import dev.tamboui.tui.bindings.Bindings;
import dev.tamboui.tui.bindings.InputTrigger;
import dev.tamboui.tui.bindings.KeyTrigger;

public final class KeyBindings {

    public static final int MAX_TABS = 9;

    public static final String RELOAD = "reload";
    public static final String TOGGLE_HELP = "toggleHelp";
    public static final String OPEN_PALETTE = "openPalette";

    public static final String START = "startContainer";
    public static final String STOP = "stopContainer";
    public static final String RESTART = "restartContainer";
    public static final String PRUNE = "pruneContainers";
    public static final String LOGS = "showLogs";
    public static final String DELETE = "delete";

    private static final Bindings BINDINGS = build();

    private KeyBindings() {
    }

    public static Bindings get() {
        return BINDINGS;
    }

    public static String key(String action) {
        return BINDINGS.triggersFor(action).stream()
                .findFirst()
                .map(InputTrigger::describe)
                .orElse("");
    }

    /// Key bound to `action`, e.g. "r, R". Empty when none.
    public static String keys(String action) {
        return BINDINGS.describeBindings(action);
    }

    public static String selectTab(int number) {
        return "selectTab" + number;
    }

    private static Bindings build() {
        var builder = BindingSets.defaults().toBuilder()
                .bind(KeyTrigger.ch('r'), RELOAD)
                .bind(KeyTrigger.ch('R'), RELOAD)
                .bind(KeyTrigger.ch('?'), TOGGLE_HELP)
                .bind(KeyTrigger.ctrl('p'), OPEN_PALETTE)
                .bind(KeyTrigger.ch(':'), OPEN_PALETTE)
                .bind(KeyTrigger.ch('h'), Actions.MOVE_LEFT)
                .bind(KeyTrigger.ch('H'), Actions.MOVE_LEFT)
                .bind(KeyTrigger.ch('l'), LOGS)
                .bind(KeyTrigger.ch('s'), START)
                .bind(KeyTrigger.ch('x'), STOP)
                .bind(KeyTrigger.ch('t'), RESTART)
                .bind(KeyTrigger.ch('P'), PRUNE)
                .bind(KeyTrigger.ch('d'), DELETE);

        for (int number = 1; number <= MAX_TABS; number++) {
            builder.bind(KeyTrigger.ch((char) ('0' + number)), selectTab(number));
        }
        return builder.build();
    }
}
