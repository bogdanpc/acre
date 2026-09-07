package dev.images;

import static dev.tamboui.toolkit.Toolkit.stack;

import dev.applecontainer.AppleContainerCli;
import dev.containers.Container;
import dev.containers.ContainerCommands;
import dev.palette.Command;
import dev.tamboui.toolkit.element.StyledElement;
import dev.tamboui.tui.bindings.ActionHandler;
import dev.tamboui.tui.bindings.Actions;
import dev.ui.KeyBindings;
import dev.ui.Loader;
import dev.ui.Tab;
import dev.ui.TableController;
import dev.ui.TableView;

import java.util.List;
import java.util.stream.Stream;

public final class ImagesTab {

    private static final String TITLE = "Images";

    private final TableView<ContainerImage> tableView;
    private final ImageDetailController detail;
    private final ImageDetailView detailView;
    private final ActionHandler detailActions;

    public static ImagesTab of(AppleContainerCli cli) {
        var images = new TableController<>(TITLE, new ImageCommands(cli)::list);
        return new ImagesTab(images, new Loader<>(new ContainerCommands(cli)::list, List.of()));
    }

    public ImagesTab(TableController<ContainerImage> table, Loader<List<Container>> containers) {
        this.detail = new ImageDetailController(table, containers);
        this.detailView = new ImageDetailView();
        this.tableView = ImagesView.of(table);
        this.tableView.on(Actions.SELECT, detail::open);
        this.detailActions = new ActionHandler(KeyBindings.get())
                .on(Actions.CANCEL, _ -> detail.close())
                .on(KeyBindings.RELOAD, _ -> detail.reload());
    }

    public Tab tab() {
        return new Tab(TITLE, this::element, this::commands);
    }

    public StyledElement<?> element() {
        return detail.detail()
                .map(shown -> stack(detailView.element(shown))
                        .id(TITLE)
                        .focusable()
                        .onAction(detailActions))
                .orElseGet(tableView::element);
    }

    public List<Command> commands() {
        return Stream.concat(
                        tableView.commands().stream(),
                        Stream.of(detail.detail().isPresent()
                                ? new Command("back to the image list",
                                        KeyBindings.shortcutKey(Actions.CANCEL), detail::close)
                                : new Command("show image details",
                                        KeyBindings.shortcutKey(Actions.SELECT), detail::open)))
                .toList();
    }
}
