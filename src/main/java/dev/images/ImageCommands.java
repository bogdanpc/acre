package dev.images;

import dev.applecontainer.AppleContainerCli;
import dev.applecontainer.CliResult;
import tools.jackson.jr.stree.JrsNumber;
import tools.jackson.jr.stree.JrsValue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
                text(configuration.path("name")),
                text(descriptor.path("mediaType")),
                text(descriptor.path("digest")),
                number(descriptor.path("size")),
                configurations(node.path("variants")));
    }

    private static List<ContainerImage.Configuration> configurations(JrsValue variants) {
        return values(variants).map(ImageCommands::configuration).toList();
    }

    private static ContainerImage.Configuration configuration(JrsValue variant) {
        var config = variant.path("config").path("config");
        return new ContainerImage.Configuration(
                platform(variant.path("platform")),
                number(variant.path("size")),
                arguments(config.path("Entrypoint")),
                arguments(config.path("Cmd")),
                text(config.path("WorkingDir")),
                environment(config.path("Env")));
    }

    private static String platform(JrsValue node) {
        return Stream.of(text(node.path("os")), text(node.path("architecture")), text(node.path("variant")))
                .filter(part -> !part.isEmpty())
                .collect(Collectors.joining("/"));
    }

    private static String arguments(JrsValue array) {
        return values(array).map(ImageCommands::text).collect(Collectors.joining(" "));
    }

    private static List<ContainerImage.Variable> environment(JrsValue array) {
        return values(array).map(ImageCommands::text).map(ImageCommands::variable).toList();
    }

    private static ContainerImage.Variable variable(String entry) {
        var equals = entry.indexOf('=');
        return equals < 0
                ? new ContainerImage.Variable(entry, "")
                : new ContainerImage.Variable(entry.substring(0, equals), entry.substring(equals + 1));
    }

    private static Stream<JrsValue> values(JrsValue array) {
        return IntStream.range(0, array.size()).mapToObj(array::path);
    }

    private static String text(JrsValue node) {
        var value = node.asText();
        return value == null ? "" : value;
    }

    private static long number(JrsValue node) {
        return node instanceof JrsNumber value ? value.getValue().longValue() : 0;
    }
}
