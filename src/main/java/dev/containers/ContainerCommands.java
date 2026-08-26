package dev.containers;

import dev.applecontainer.AppleContainerCli;
import tools.jackson.jr.stree.JrsValue;

import java.util.List;
import java.util.stream.IntStream;

/// `container ls --all` command, mapped from its JSON output.
public class ContainerCommands {

    private final AppleContainerCli cli;

    public ContainerCommands(AppleContainerCli cli) {
        this.cli = cli;
    }

    /**
     * Lists the containers.
     *
     * @throws dev.applecontainer.AppleContainerCliException if the CLI failed or printed something that is not JSON
     */
    public List<Container> list() {
        var listed = cli.runJson("ls", "--all");
        return IntStream.range(0, listed.size())
                .mapToObj(i -> container(listed.path(i)))
                .toList();
    }

    private static Container container(JrsValue node) {
        var configuration = node.path("configuration");
        var resources = configuration.path("resources");
        return new Container(
                text(node.path("id")),
                text(configuration.path("image").path("reference")),
                text(node.path("status").path("state")),
                address(node.path("status").path("networks").path(0)),
                (int) number(resources.path("cpus")),
                number(resources.path("memoryInBytes")));
    }

    /** Show only the IP. Address format is CIDR, for example {@code 192.168.64.3/24}. */
    private static String address(JrsValue network) {
        var cidr = text(network.path("address"));
        var slash = cidr.indexOf('/');
        return slash < 0 ? cidr : cidr.substring(0, slash);
    }

    private static String text(JrsValue node) {
        var value = node.asText();
        return value == null ? "" : value;
    }

    private static long number(JrsValue node) {
        try {
            return Long.parseLong(text(node).strip());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
