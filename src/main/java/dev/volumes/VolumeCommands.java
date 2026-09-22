package dev.volumes;

import dev.applecontainer.AppleContainerCli;
import dev.applecontainer.CliResult;
import tools.jackson.jr.stree.JrsValue;

import java.util.List;

import static dev.applecontainer.CliJson.number;
import static dev.applecontainer.CliJson.text;
import static dev.applecontainer.CliJson.values;

/**
 * The volume commands of the Apple container CLI, mapped from its JSON output.
 */
public class VolumeCommands {

    private final AppleContainerCli cli;

    public VolumeCommands(AppleContainerCli cli) {
        this.cli = cli;
    }

    /**
     * Lists the volumes held locally.
     *
     * @return one entry per volume in the order the CLI reported them, or the reason it failed
     */
    public CliResult<List<Volume>> list() {
        return cli.runJson(VolumeCommands::volumes, "volume", "list");
    }

    public CliResult<String> remove(String name) {
        return cli.run("volume", "rm", name);
    }

    private static List<Volume> volumes(JrsValue listed) {
        return values(listed).map(VolumeCommands::volume).toList();
    }

    private static Volume volume(JrsValue node) {
        var configuration = node.path("configuration");
        return new Volume(
                text(configuration, "name"),
                text(configuration, "driver"),
                text(configuration, "format"),
                number(configuration, "sizeInBytes"),
                text(configuration, "source"));
    }
}
