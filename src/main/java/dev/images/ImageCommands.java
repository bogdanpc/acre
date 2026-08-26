package dev.images;

import dev.applecontainer.AppleContainerCli;
import tools.jackson.jr.stree.JrsValue;

import java.util.List;
import java.util.stream.IntStream;

/// `container image list` command, mapped from its JSON output.
public class ImageCommands {

    private final AppleContainerCli cli;

    public ImageCommands(AppleContainerCli cli) {
        this.cli = cli;
    }

    /**
     * Apple container images
     *
     * @throws dev.applecontainer.AppleContainerCliException if the CLI failed or printed something that is not JSON
     */
    public List<ContainerImage> list() {
        var listed = cli.runJson("image", "list");
        return IntStream.range(0, listed.size())
                .mapToObj(i -> new ContainerImage(text(listed.path(i).path("configuration").path("name"))))
                .toList();
    }

    private static String text(JrsValue node) {
        var value = node.asText();
        return value == null ? "" : value;
    }
}
