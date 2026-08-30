package dev.ui;

import dev.palette.Command;
import dev.tamboui.toolkit.element.StyledElement;

import java.util.List;
import java.util.function.Supplier;

public record Tab(String title, Supplier<StyledElement<?>> content, Supplier<List<Command>> commands) {

    public static Tab of(TableView<?> view) {
        return new Tab(view.title(), view::element, view::commands);
    }
}
