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
        try {
            return cli.run("system", "status").isSuccess();
        } catch (AppleContainerCliException e) {
            return false;
        }
    }
}
