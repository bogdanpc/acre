package dev.ui;

import dev.applecontainer.AppleContainerCli;
import dev.applecontainer.CliResult;
import dev.applecontainer.SystemCommands;

import java.time.Duration;
import java.util.List;

public final class SystemController {

    private static final Duration STATUS_REFRESH = Duration.ofSeconds(5);

    private final SystemCommands system;
    private final Loader<AppleContainerStatus> status;
    private final CliRunner runner;

    public SystemController(SystemCommands system, Loader<AppleContainerStatus> status, CliRunner runner) {
        this.system = system;
        this.status = status;
        this.runner = runner;
    }

    public static SystemController of(AppleContainerCli cli) {
        return of(new SystemCommands(cli), new CliRunner());
    }

    public static SystemController of(SystemCommands system, CliRunner runner) {
        var status = new Loader<>(
                () -> CliResult.success(AppleContainerStatus.of(system.isRunning())), AppleContainerStatus.UNKNOWN,
                runner)
                .refreshEvery(STATUS_REFRESH);
        return new SystemController(system, status, runner);
    }

    public List<Action> actions() {
        return List.of(
                Action.unbound("start the container system", this::start),
                Action.unbound("stop the container system", this::stop));
    }

    public void start() {
        runner.run(system::start, _ -> status.reload());
    }

    public void stop() {
        runner.run(system::stop, _ -> status.reload());
    }

    public Loader<AppleContainerStatus> status() {
        return status;
    }
}
