package dev.containers;

import dev.applecontainer.AppleContainerCli;
import tools.jackson.jr.stree.JrsValue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/// `container ls --all` command, mapped from its JSON output.
public class ContainerCommands {

    private final AppleContainerCli cli;

    public ContainerCommands(AppleContainerCli cli) {
        this.cli = cli;
    }

    /**
     * Lists the containers.
     */
    public List<Container> list() {
        var listed = cli.runJson("ls", "--all");
        return values(listed).map(ContainerCommands::container).toList();
    }

    public void start(String id) {
        cli.runChecked("start", id);
    }

    public void stop(String id) {
        cli.runChecked("stop", id);
    }

    public void restart(String id) {
        cli.runChecked("restart", id);
    }

    public void delete(String id) {
        cli.runChecked("delete", id);
    }

    public void prune() {
        cli.runChecked("prune");
    }

    private static Container container(JrsValue node) {
        var configuration = node.path("configuration");
        var resources = configuration.path("resources");
        var status = node.path("status");
        return new Container(
                text(node.path("id")),
                text(configuration.path("image").path("reference")),
                text(status.path("state")),
                address(status.path("networks").path(0)),
                (int) number(resources.path("cpus")),
                number(resources.path("memoryInBytes")),
                details(configuration, status));
    }

    private static Container.Details details(JrsValue configuration, JrsValue status) {
        var initProcess = configuration.path("initProcess");
        return new Container.Details(
                platform(configuration.path("platform")),
                text(configuration.path("networks").path(0).path("options").path("hostname")),
                user(initProcess.path("user").path("id")),
                command(initProcess),
                ports(configuration.path("publishedPorts")),
                text(configuration.path("runtimeHandler")),
                flag(configuration.path("virtualization")),
                flag(configuration.path("rosetta")),
                text(configuration.path("creationDate")),
                text(status.path("startedDate")));
    }

    private static String platform(JrsValue node) {
        var os = text(node.path("os"));
        var architecture = text(node.path("architecture"));
        return os.isEmpty() || architecture.isEmpty() ? os + architecture : os + "/" + architecture;
    }

    private static String user(JrsValue id) {
        var uid = text(id.path("uid"));
        return uid.isEmpty() ? "" : "uid %s, gid %s".formatted(uid, text(id.path("gid")));
    }

    private static String command(JrsValue initProcess) {
        var arguments = initProcess.path("arguments");
        return Stream.concat(
                        Stream.of(text(initProcess.path("executable"))),
                        values(arguments).map(ContainerCommands::text))
                .filter(part -> !part.isEmpty())
                .collect(Collectors.joining(" "));
    }

    private static List<String> ports(JrsValue published) {
        return values(published).map(port -> "%s:%d → %d/%s".formatted(
                        text(port.path("hostAddress")),
                        number(port.path("hostPort")),
                        number(port.path("containerPort")),
                        text(port.path("proto"))))
                .toList();
    }

    private static Stream<JrsValue> values(JrsValue array) {
        return IntStream.range(0, array.size()).mapToObj(array::path);
    }

    private static boolean flag(JrsValue node) {
        return "true".equals(text(node));
    }

    /**
     * Show only the IP. Address format is CIDR, for example {@code 192.168.64.3/24}.
     */
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
