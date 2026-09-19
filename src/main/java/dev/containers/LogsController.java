package dev.containers;

import dev.applecontainer.CliResult;
import dev.tamboui.widgets.input.TextInputState;
import dev.ui.CliRunner;
import dev.ui.Loader;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public final class LogsController {

    static final int TAIL = 1000;

    static final Duration REFRESH = Duration.ofSeconds(2);

    private final ContainerCommands commands;
    private final Executor executor;
    private final TextInputState filter = new TextInputState();

    private String container;
    private Loader<List<String>> lines;

    public LogsController(ContainerCommands commands) {
        this(commands, CliRunner.DEFAULT_EXECUTOR);
    }

    public LogsController(ContainerCommands commands, Executor executor) {
        this.commands = commands;
        this.executor = executor;
    }

    public String container() {
        return container == null ? "" : container;
    }

    public void open(String container) {
        this.container = container;
        this.filter.clear();
        this.lines = loader(container);
    }

    public void close() {
        this.container = null;
        this.lines = null;
        this.filter.clear();
    }

    public TextInputState filter() {
        return filter;
    }

    public void reload() {
        if (lines != null) {
            lines.reload();
        }
    }

    public boolean loading() {
        return lines != null && lines.loading();
    }

    public String failure() {
        return lines == null ? null : lines.failure();
    }

    public List<String> lines() {
        if (lines == null) {
            return List.of();
        }
        var all = lines.value();
        var query = filter.text().strip().toLowerCase(Locale.ROOT);
        return query.isEmpty()
                ? all
                : all.stream().filter(line -> line.toLowerCase(Locale.ROOT).contains(query)).toList();
    }

    private Loader<List<String>> loader(String id) {
        Supplier<CliResult<List<String>>> source = () -> commands.logs(id, TAIL);
        return new Loader<>(source, List.of(), executor).refreshEvery(REFRESH);
    }
}
