package dev.containers;

import dev.applecontainer.AppleContainerCli;
import dev.applecontainer.CliJson;
import dev.applecontainer.CliResult;
import tools.jackson.jr.stree.JrsValue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.applecontainer.CliJson.*;

/// `container ls --all` command, mapped from its JSON output.
public class ContainerCommands {

    private final AppleContainerCli cli;

    public ContainerCommands(AppleContainerCli cli) {
        this.cli = cli;
    }

    /**
     * Lists the containers.
     */
    public CliResult<List<Container>> list() {
        return cli.runJson(ContainerCommands::containers, "ls", "--all");
    }

    public CliResult<List<String>> logs(String containerId, int tail) {
        return cli.run(ContainerCommands::lines, "logs", "-n", String.valueOf(tail), containerId);
    }

    public CliResult<String> start(String id) {
        return cli.run("start", id);
    }

    public CliResult<String> stop(String id) {
        return cli.run("stop", id);
    }

    public CliResult<String> restart(String id) {
        return cli.run("restart", id);
    }

    public CliResult<String> delete(String id) {
        return cli.run("delete", id);
    }

    public CliResult<String> prune() {
        return cli.run("prune");
    }

    private static List<Container> containers(JrsValue listed) {
        return values(listed).map(ContainerCommands::container).toList();
    }

    private static List<String> lines(String output) {
        return output.isBlank() ? List.of() : List.of(output.stripTrailing().split("\\R", -1));
    }

    private static Container container(JrsValue node) {
        var configuration = node.path("configuration");
        var resources = configuration.path("resources");
        var status = node.path("status");
        return new Container(
                text(node, "id"),
                text(configuration.path("image"), "reference"),
                text(status, "state"),
                address(status.path("networks").path(0)),
                (int) number(resources, "cpus"),
                number(resources, "memoryInBytes"),
                details(configuration, status));
    }

    private static Container.Details details(JrsValue configuration, JrsValue status) {
        var initProcess = configuration.path("initProcess");
        return new Container.Details(
                platform(configuration.path("platform")),
                text(configuration.path("networks").path(0).path("options"), "hostname"),
                user(initProcess.path("user").path("id")),
                command(initProcess),
                ports(configuration.path("publishedPorts")),
                text(configuration, "runtimeHandler"),
                bool(configuration, "virtualization"),
                bool(configuration, "rosetta"),
                text(configuration, "creationDate"),
                text(status, "startedDate"),
                mounts(configuration.path("mounts")));
    }

    private static List<Container.Mount> mounts(JrsValue mounts) {
        return values(mounts).map(mount -> new Container.Mount(
                        text(mount.path("type").path("volume"), "name"),
                        text(mount, "destination"),
                        text(mount, "source")))
                .toList();
    }

    private static String platform(JrsValue node) {
        var os = text(node, "os");
        var architecture = text(node, "architecture");
        return os.isEmpty() || architecture.isEmpty() ? os + architecture : os + "/" + architecture;
    }

    private static String user(JrsValue id) {
        var uid = text(id, "uid");
        return uid.isEmpty() ? "" : "uid %s, gid %s".formatted(uid, text(id, "gid"));
    }

    private static String command(JrsValue initProcess) {
        var arguments = initProcess.path("arguments");
        return Stream.concat(
                        Stream.of(text(initProcess, "executable")),
                        values(arguments).map(CliJson::text))
                .filter(part -> !part.isEmpty())
                .collect(Collectors.joining(" "));
    }

    private static List<String> ports(JrsValue published) {
        return values(published).map(port -> "%s:%d → %d/%s".formatted(
                        text(port, "hostAddress"),
                        number(port, "hostPort"),
                        number(port, "containerPort"),
                        text(port, "proto")))
                .toList();
    }

    /**
     * Show only the IP. Address format is CIDR, for example {@code 192.168.64.3/24}.
     */
    private static String address(JrsValue network) {
        var cidr = text(network, "address");
        var slash = cidr.indexOf('/');
        return slash < 0 ? cidr : cidr.substring(0, slash);
    }
}
