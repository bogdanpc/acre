package dev.ui;

import dev.tamboui.toolkit.element.Element;

import java.util.function.Supplier;

public record Tab(String title, Supplier<Element> content) {

    public static Tab of(TableView<?> view) {
        return new Tab(view.title(), view::element);
    }
}
