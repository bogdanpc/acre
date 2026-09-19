package dev.containers;

import static dev.tamboui.toolkit.Toolkit.stack;

import dev.applecontainer.AppleContainerCli;
import dev.tamboui.toolkit.Toolkit;
import dev.tamboui.toolkit.element.StyledElement;
import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.tui.bindings.Actions;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import dev.ui.Action;
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
    private final ContainersController controller;
    private final ContainersView tableView;
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
        this.controller = new ContainersController(commands, table);
        this.tableView = ContainersView.of(table);
    }

    public Tab tab() {
        return new Tab(TITLE, this::element, this::actions);
    }

    public StyledElement<?> element() {
        var container = page == Page.TABLE ? Optional.<Container>empty() : table.selected();
        if (container.isEmpty()) {
            close();
            return tableView.element(actions());
        }
        if (page == Page.LOGS) {
            return stack(logsView.element())
                    .id(TITLE)
                    .focusable()
                    .onKeyEvent(this::onLogsKey);
        }
        return tableView.page(ContainerDetailView.page(container.get()), actions());
    }

    public List<Action> actions() {
        var actions = new ArrayList<Action>();
        actions.addAll(tableView.actions());
        actions.addAll(controller.actions());
        actions.add(switch (page) {
            case TABLE -> new Action(Actions.SELECT, "show container details", this::open);
            case DETAIL -> new Action(Actions.CANCEL, "back to the container list", this::close);
            case LOGS -> new Action(Actions.CANCEL, "close the logs", this::closeLogs);
        });
        if (page != Page.LOGS) {
            actions.add(new Action(KeyBindings.LOGS, "show container logs", this::openLogs));
        }
        return List.copyOf(actions);
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
