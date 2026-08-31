package dev.palette;

import dev.tamboui.toolkit.Toolkit;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.widgets.input.TextInputState;
import dev.tamboui.widgets.table.TableState;

import java.util.List;

public final class PaletteController {

    private final TextInputState query = new TextInputState();

    private final TableState rows = new TableState();

    private List<Command> commands = List.of();
    private boolean visible;

    public boolean visible() {
        return visible;
    }

    public void open(List<Command> commands) {
        this.commands = List.copyOf(commands);
        this.query.clear();
        this.rows.select(0);
        this.visible = true;
    }

    public void close() {
        this.commands = List.of();
        this.query.clear();
        this.rows.select(0);
        this.visible = false;
    }

    public TextInputState query() {
        return query;
    }

    public TableState rows() {
        return rows;
    }

    public int index() {
        var selected = rows.selected();
        return selected == null ? 0 : selected;
    }

    public List<Command> matches() {
        return Fuzzy.rank(query.text(), commands);
    }

    public Command selected() {
        var matches = matches();
        return matches.isEmpty() || index() >= matches.size() ? null : matches.get(index());
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
            rows.select(0);
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
        rows.select(count == 0 ? 0 : Math.floorMod(index() + delta, count));
    }
}
