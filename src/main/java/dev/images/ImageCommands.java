package dev.images;

import dev.applecontainer.AppleContainerCli;
import tools.jackson.jr.stree.JrsValue;

import java.util.List;
import java.util.stream.IntStream;

/** The image commands of the Apple container CLI, mapped from its JSON output. */
public class ImageCommands {

    private final AppleContainerCli cli;

    public ImageCommands(AppleContainerCli cli) {
        this.cli = cli;
    }

    /**
     * Lists the images held locally.
     *
     * @return one entry per image, in the order the CLI reported them
     * @throws dev.applecontainer.AppleContainerCliException if the CLI failed or printed something that is not JSON
     */
    public List<ContainerImage> list() {
        var listed = cli.runJson("image", "list");
        return IntStream.range(0, listed.size())
                .mapToObj(i -> new ContainerImage(text(listed.path(i).path("configuration").path("name"))))
                .toList();
    }

    /** The simple tree has no null-safe accessors: a missing or null node answers {@code null}. */
    private static String text(JrsValue node) {
        var value = node.asText();
        return value == null ? "" : value;
    }
}
