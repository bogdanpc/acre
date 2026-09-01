package dev.containers;

import static dev.tamboui.toolkit.Toolkit.stack;

import dev.applecontainer.AppleContainerCli;
import dev.palette.Command;
import dev.tamboui.toolkit.Toolkit;
import dev.tamboui.toolkit.element.StyledElement;
import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.tui.bindings.ActionHandler;
import dev.tamboui.tui.bindings.Actions;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import dev.ui.KeyBindings;
import dev.ui.Tab;
import dev.ui.TableController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ContainersTab {

    private static final String TITLE = "Containers";

    private enum Page {
        TABLE, DETAIL, LOGS
    }

    private final TableController<Container> table;
    private final ContainersController actions;
    private final ContainersView tableView;
    private final ActionHandler detailActions;
    private final LogsController logs;
    private final LogsView logsView;

    private Page page = Page.TABLE;

    private Page beforeLogs = Page.TABLE;

    public static ContainersTab of(AppleContainerCli cli) {
        var commands = new ContainerCommands(cli);
        return new ContainersTab(commands, new TableController<>(TITLE, commands::list));
    }

    public ContainersTab(ContainerCommands commands, TableController<Container> table) {
        this(commands, table, new LogsController(commands));
    }

    public ContainersTab(ContainerCommands commands, TableController<Container> table, LogsController logs) {
        this.table = table;
        this.logs = logs;
        this.logsView = new LogsView(logs);
        this.actions = new ContainersController(commands, table);
        this.tableView = ContainersView.of(table, actions);
        this.tableView.on(Actions.SELECT, this::open);
        this.tableView.on(KeyBindings.LOGS, this::openLogs);
        this.detailActions = new ActionHandler(KeyBindings.get())
                .on(Actions.CANCEL, _ -> close())
                .on(Actions.MOVE_DOWN, _ -> table.moveDown())
                .on(Actions.MOVE_UP, _ -> table.moveUp())
                .on(KeyBindings.RELOAD, _ -> table.reload())
                .on(KeyBindings.LOGS, _ -> openLogs());
        actions.shortcuts().forEach((action, run) -> this.detailActions.on(action, _ -> run.run()));
    }

    public Tab tab() {
        return new Tab(TITLE, this::element, this::commands);
    }

    public StyledElement<?> element() {
        var container = page == Page.TABLE ? Optional.<Container>empty() : table.selected();
        if (container.isEmpty()) {
            close();
            return tableView.element();
        }
        if (page == Page.LOGS) {
            return stack(logsView.element())
                    .id(TITLE)
                    .focusable()
                    .onKeyEvent(this::onLogsKey);
        }
        return stack(ContainerDetailView.page(container.get()))
                .id(TITLE)
                .focusable()
                .onAction(detailActions);
    }

    public List<Command> commands() {
        var commands = new ArrayList<Command>();
        commands.addAll(tableView.commands());
        commands.addAll(actions.commands());
        switch (page) {
            case TABLE -> commands.add(new Command("show container details",
                    KeyBindings.shortcutKey(Actions.SELECT), this::open));
            case DETAIL -> commands.add(new Command("back to the container list",
                    KeyBindings.shortcutKey(Actions.CANCEL), this::close));
            case LOGS -> commands.add(new Command("close the logs",
                    KeyBindings.shortcutKey(Actions.CANCEL), this::closeLogs));
        }
        if (page != Page.LOGS) {
            commands.add(new Command("show container logs",
                    KeyBindings.shortcutKey(KeyBindings.LOGS), this::openLogs));
        }
        return List.copyOf(commands);
    }

    private EventResult onLogsKey(KeyEvent event) {
        if (event.isKey(KeyCode.ESCAPE)) {
            closeLogs();
            return EventResult.HANDLED;
        }
        if (logsView.scroll(event)) {
            return EventResult.HANDLED;
        }
        return Toolkit.handleTextInputKey(logs.filter(), event)
                ? EventResult.HANDLED
                : EventResult.UNHANDLED;
    }

    private void open() {
        page = Page.DETAIL;
    }

    private void close() {
        page = Page.TABLE;
        logs.close();
    }

    private void openLogs() {
        table.selected().ifPresent(container -> {
            beforeLogs = page == Page.LOGS ? beforeLogs : page;
            logs.open(container.id());
            page = Page.LOGS;
        });
    }

    private void closeLogs() {
        logs.close();
        page = beforeLogs;
    }
}
