package dev.images;

import dev.applecontainer.AppleContainerCli;
import dev.applecontainer.CliJson;
import dev.applecontainer.CliResult;
import tools.jackson.jr.stree.JrsValue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.applecontainer.CliJson.number;
import static dev.applecontainer.CliJson.text;
import static dev.applecontainer.CliJson.values;

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
        return values(listed).map(ImageCommands::image).toList();
    }

    private static ContainerImage image(JrsValue node) {
        var configuration = node.path("configuration");
        var descriptor = configuration.path("descriptor");
        return new ContainerImage(
                text(configuration, "name"),
                text(descriptor, "mediaType"),
                text(descriptor, "digest"),
                number(descriptor, "size"),
                configurations(node.path("variants")));
    }

    private static List<ContainerImage.Configuration> configurations(JrsValue variants) {
        return values(variants).map(ImageCommands::configuration).toList();
    }

    private static ContainerImage.Configuration configuration(JrsValue variant) {
        var config = variant.path("config").path("config");
        return new ContainerImage.Configuration(
                platform(variant.path("platform")),
                number(variant, "size"),
                arguments(config.path("Entrypoint")),
                arguments(config.path("Cmd")),
                text(config, "WorkingDir"),
                environment(config.path("Env")));
    }

    private static String platform(JrsValue node) {
        return Stream.of(text(node, "os"), text(node, "architecture"), text(node, "variant"))
                .filter(part -> !part.isEmpty())
                .collect(Collectors.joining("/"));
    }

    private static String arguments(JrsValue array) {
        return values(array).map(CliJson::text).collect(Collectors.joining(" "));
    }

    private static List<ContainerImage.Variable> environment(JrsValue array) {
        return values(array).map(CliJson::text).map(ImageCommands::variable).toList();
    }

    private static ContainerImage.Variable variable(String entry) {
        var equals = entry.indexOf('=');
        return equals < 0
                ? new ContainerImage.Variable(entry, "")
                : new ContainerImage.Variable(entry.substring(0, equals), entry.substring(equals + 1));
    }
}
