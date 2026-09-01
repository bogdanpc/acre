package dev.images;

import dev.applecontainer.AppleContainerCli;
import dev.applecontainer.CliResult;
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
     */
    public CliResult<List<ContainerImage>> list() {
        return cli.runJson(ImageCommands::images, "image", "list");
    }

    private static List<ContainerImage> images(JrsValue listed) {
        return IntStream.range(0, listed.size())
                .mapToObj(i -> new ContainerImage(text(listed.path(i).path("configuration").path("name"))))
                .toList();
    }

    private static String text(JrsValue node) {
        var value = node.asText();
        return value == null ? "" : value;
    }
}
