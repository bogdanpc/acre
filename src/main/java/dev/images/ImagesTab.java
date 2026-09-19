package dev.images;

import static dev.tamboui.toolkit.Toolkit.stack;

import dev.applecontainer.AppleContainerCli;
import dev.containers.Container;
import dev.containers.ContainerCommands;
import dev.tamboui.toolkit.element.StyledElement;
import dev.tamboui.tui.bindings.Actions;
import dev.ui.Action;
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

    public static ImagesTab of(AppleContainerCli cli) {
        var images = new TableController<>(TITLE, new ImageCommands(cli)::list);
        return new ImagesTab(images, new Loader<>(new ContainerCommands(cli)::list, List.of()));
    }

    public ImagesTab(TableController<ContainerImage> table, Loader<List<Container>> containers) {
        this.detail = new ImageDetailController(table, containers);
        this.detailView = new ImageDetailView();
        this.tableView = ImagesView.of(table);
    }

    public Tab tab() {
        return new Tab(TITLE, this::element, this::actions);
    }

    public StyledElement<?> element() {
        return detail.detail()
                .map(shown -> stack(detailView.element(shown))
                        .id(TITLE)
                        .focusable()
                        .onAction(Action.handler(actions())))
                .orElseGet(() -> tableView.element(actions()));
    }

    public List<Action> actions() {
        if (detail.detail().isPresent()) {
            return List.of(
                    new Action(KeyBindings.RELOAD, "reload the images list", detail::reload),
                    new Action(Actions.CANCEL, "back to the image list", detail::close));
        }
        return Stream.concat(
                        tableView.actions().stream(),
                        Stream.of(new Action(Actions.SELECT, "show image details", detail::open)))
                .toList();
    }
}
