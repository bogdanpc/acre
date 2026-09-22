package dev.images;

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

import java.util.ArrayList;
import java.util.List;

public final class ImagesTab {

    private static final String TITLE = "Images";

    private final TableView<ContainerImage> tableView;
    private final ImageDetailController detail;
    private final ImagesController controller;
    private final ImageDetailView detailView;

    public static ImagesTab of(AppleContainerCli cli) {
        var commands = new ImageCommands(cli);
        var images = new TableController<>(TITLE, commands::list);
        return new ImagesTab(images, new Loader<>(new ContainerCommands(cli)::list, List.of()), new ImagesController(commands, images));
    }

    public ImagesTab(TableController<ContainerImage> table, Loader<List<Container>> containers, ImagesController controller) {
        this.detail = new ImageDetailController(table, containers);
        this.detailView = new ImageDetailView();
        this.tableView = ImagesView.of(table);
        this.controller = controller;
    }

    public Tab tab() {
        return new Tab(TITLE, this::element, this::actions);
    }

    public StyledElement<?> element() {
        return detail.detail()
                .map(shown -> tableView.page(detailView.element(shown), actions()))
                .orElseGet(() -> tableView.element(actions()));
    }

    public List<Action> actions() {

        if (detail.detail().isPresent()) {
            var actions = new ArrayList<>(List.of(
                    new Action(KeyBindings.RELOAD, "reload the images list", detail::reload),
                    new Action(KeyBindings.DELETE, "delete image", this::deleteImage),
                    new Action(Actions.CANCEL, "back to the image list", detail::close)));
            actions.addAll(detailView.actions());
            return actions;
        }
        var actions = new ArrayList<Action>();
        actions.addAll(controller.actions());
        actions.addAll(tableView.actions());
        actions.add(new Action(Actions.SELECT, "show image details", detail::open));

        return actions;
    }

    private void deleteImage() {
        controller.delete();
        detail.close();
    }
}
