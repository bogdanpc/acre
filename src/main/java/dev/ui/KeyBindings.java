package dev.ui;

import dev.tamboui.tui.bindings.Actions;
import dev.tamboui.tui.bindings.BindingSets;
import dev.tamboui.tui.bindings.Bindings;
import dev.tamboui.tui.bindings.KeyTrigger;

import java.util.Map;

public final class KeyBindings {

    public static final int MAX_TABS = 9;

    public static final String RELOAD = "reload";
    public static final String TOGGLE_HELP = "toggleHelp";
    public static final String OPEN_PALETTE = "openPalette";

    public static final String START = "startContainer";
    public static final String STOP = "stopContainer";
    public static final String RESTART = "restartContainer";
    public static final String PRUNE = "pruneContainers";

    private static final Map<String, String> KEYS = Map.ofEntries(
            Map.entry(RELOAD, "r"),
            Map.entry(TOGGLE_HELP, "?"),
            Map.entry(OPEN_PALETTE, ":"),
            Map.entry(START, "s"),
            Map.entry(STOP, "x"),
            Map.entry(RESTART, "t"),
            Map.entry(PRUNE, "P"),
            Map.entry(Actions.QUIT, "q"),
            Map.entry(Actions.SELECT, "enter"),
            Map.entry(Actions.CANCEL, "esc"));

    private static final Bindings BINDINGS = build();

    private KeyBindings() {
    }

    public static Bindings get() {
        return BINDINGS;
    }

    public static String shortcutKey(String action) {
        return KEYS.getOrDefault(action, "");
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
                .bind(KeyTrigger.ch('l'), Actions.MOVE_RIGHT)
                .bind(KeyTrigger.ch('L'), Actions.MOVE_RIGHT)
                .bind(KeyTrigger.ch('s'), START)
                .bind(KeyTrigger.ch('x'), STOP)
                .bind(KeyTrigger.ch('t'), RESTART)
                .bind(KeyTrigger.ch('P'), PRUNE);
        for (int number = 1; number <= MAX_TABS; number++) {
            builder.bind(KeyTrigger.ch((char) ('0' + number)), selectTab(number));
        }
        return builder.build();
    }
}
