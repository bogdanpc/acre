package dev.palette;

import dev.tamboui.toolkit.Toolkit;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.widgets.input.TextInputState;

import java.util.List;

public final class PaletteController {

    private final TextInputState query = new TextInputState();

    private List<Command> commands = List.of();
    private int index;
    private boolean visible;

    public boolean visible() {
        return visible;
    }

    public void open(List<Command> commands) {
        this.commands = List.copyOf(commands);
        this.query.clear();
        this.index = 0;
        this.visible = true;
    }

    public void close() {
        this.commands = List.of();
        this.query.clear();
        this.index = 0;
        this.visible = false;
    }

    public TextInputState query() {
        return query;
    }

    public int index() {
        return index;
    }

    public List<Command> matches() {
        return Fuzzy.rank(query.text(), commands);
    }

    public Command selected() {
        var matches = matches();
        return matches.isEmpty() || index >= matches.size() ? null : matches.get(index);
    }

    public void handle(KeyEvent event) {
        if (event.isKey(KeyCode.ESCAPE)) {
            close();
            return;
        }
        if (event.isKey(KeyCode.UP) || (event.hasCtrl() && event.isCharIgnoreCase('p'))) {
            move(-1);
            return;
        }
        if (event.isKey(KeyCode.DOWN) || event.isKey(KeyCode.TAB)
                || (event.hasCtrl() && event.isCharIgnoreCase('n'))) {
            move(1);
            return;
        }
        if (event.isKey(KeyCode.ENTER)) {
            run();
            return;
        }
        if (Toolkit.handleTextInputKey(query, event)) {
            index = 0;
        }
    }

    private void run() {
        var command = selected();
        close();
        if (command != null) {
            command.run().run();
        }
    }

    private void move(int delta) {
        int count = matches().size();
        index = count == 0 ? 0 : Math.floorMod(index + delta, count);
    }
}
