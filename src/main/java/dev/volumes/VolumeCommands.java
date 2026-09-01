package dev.volumes;

import dev.applecontainer.AppleContainerCli;
import dev.applecontainer.CliResult;
import tools.jackson.jr.stree.JrsNumber;
import tools.jackson.jr.stree.JrsValue;

import java.util.List;
import java.util.stream.IntStream;

/** The volume commands of the Apple container CLI, mapped from its JSON output. */
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

    private static List<Volume> volumes(JrsValue listed) {
        return IntStream.range(0, listed.size())
                .mapToObj(i -> volume(listed.path(i)))
                .toList();
    }

    private static Volume volume(JrsValue node) {
        var configuration = node.path("configuration");
        return new Volume(
                text(configuration.path("name")),
                text(configuration.path("driver")),
                text(configuration.path("format")),
                number(configuration.path("sizeInBytes")),
                text(configuration.path("source")));
    }

    /** The simple tree has no null-safe accessors: a missing or null node answers {@code null}. */
    private static String text(JrsValue node) {
        var value = node.asText();
        return value == null ? "" : value;
    }

    private static long number(JrsValue node) {
        return node instanceof JrsNumber value ? value.getValue().longValue() : 0;
    }
}
