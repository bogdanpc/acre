package dev.volumes;

import dev.containers.Container;
import dev.testing.MockContainerCli;
import dev.testing.TestScreen;
import dev.applecontainer.CliResult;
import dev.ui.CliRunner;
import dev.ui.Loader;
import dev.ui.TableController;
import dev.tamboui.tui.event.KeyCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

class VolumesTabTest {

    @RegisterExtension
    final MockContainerCli cli = new MockContainerCli();

    private static final Volume PGDATA =
            new Volume("pgdata", "local", "ext4", 549755813888L, "/Users/me/volumes/pgdata/volume.img");

    private static final Container DB = new Container(
            "dev-db", "docker.io/library/postgres:18", "running", "192.168.64.3", 4, 1073741824L,
            new Container.Details("linux/arm64", "dev-db", "", "", List.of(), "", false, false, "", "",
                    List.of(new Container.Mount("pgdata", "/var/lib/postgresql", "/Users/me/volume.img"))));

    @Test
    void showsTheVolumeTableFirst() {
        new TestScreen(100, 30).show(tab(List.of(PGDATA), List.of(DB))::element)
                .assertShows("pgdata")
                .assertDoesNotShow("Used By Containers");
    }

    @Test
    void opensTheDetailPageOfTheSelectedVolume() {
        var terminal = new TestScreen(100, 30);
        terminal.show(tab(List.of(PGDATA), List.of(DB))::element);

        terminal.press(KeyCode.ENTER)
                .assertShows("Source")
                .assertShows("/Users/me/volumes/pgdata/volume.img")
                .assertShows("/var/lib/postgresql")
                .assertShows("Filesystem")
                .assertShows("ext4")
                .assertShows("Used By Containers")
                .assertShows("dev-db")
                .assertShows("192.168.64.3");
    }

    @Test
    void escapeGoesBackToTheTable() {
        var terminal = new TestScreen(100, 30);
        terminal.show(tab(List.of(PGDATA), List.of(DB))::element);

        terminal.press(KeyCode.ENTER).assertShows("Used By Containers");
        terminal.press(KeyCode.ESCAPE).assertDoesNotShow("Used By Containers");
    }

    @Test
    void saysWhenNoContainerMountsTheVolume() {
        var terminal = new TestScreen(100, 30);
        terminal.show(tab(List.of(PGDATA), List.of())::element);

        terminal.press(KeyCode.ENTER).assertShows("No container mounts this volume.");
    }

    private VolumesTab tab(List<Volume> volumes, List<Container> containers) {
        var table = new TableController<>("Volumes", () -> CliResult.success(volumes), new CliRunner(Runnable::run));
        return new VolumesTab(
                table,
                new Loader<>(() -> CliResult.success(containers), List.of(), new CliRunner(Runnable::run)),
                new VolumesController(new VolumeCommands(cli.missing()), table));
    }
}
