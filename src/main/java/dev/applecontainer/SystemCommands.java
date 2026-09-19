package dev.applecontainer;

/// The `container system commands` of the Apple container CLI.
public class SystemCommands {

    private final AppleContainerCli cli;

    public SystemCommands(AppleContainerCli cli) {
        this.cli = cli;
    }

    /**
     * @return true when {@code container system status} succeeded
     */
    public boolean isRunning() {
        return cli.run("system", "status") instanceof CliResult.Success;
    }

    public CliResult<String> start() {
        return cli.run("system", "start", "--disable-kernel-install");
    }

    public CliResult<String> stop() {
        return cli.run("system", "stop");
    }
}
