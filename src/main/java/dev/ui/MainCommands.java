package dev.ui;

import dev.palette.Command;
import dev.tamboui.tui.bindings.Actions;

import java.util.ArrayList;
import java.util.List;

public class MainCommands {
    private final MainController controller;

    public MainCommands(MainController controller) {
        this.controller = controller;
    }

    List<Command> all() {
        var commands = new ArrayList<>(controller.selectedTab().commands().get());
        for (int i = 0; i < Math.min(controller.tabs().size(), KeyBindings.MAX_TABS); i++) {
            var index = i;
            var tab = controller.tabs().get(i);
            commands.add(new Command("open " + tab.title().toLowerCase(),
                    String.valueOf(i + 1), () -> controller.selectTab(index)));
        }
        commands.add(new Command("show the keys", KeyBindings.shortcutKey(KeyBindings.TOGGLE_HELP), controller.help()::toggle));
        commands.add(new Command("quit", KeyBindings.shortcutKey(Actions.QUIT), controller::quit));
        return commands;
    }
}
