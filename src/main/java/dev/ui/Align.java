package dev.ui;

public enum Align {

    LEFT,
    RIGHT;

    String pad(String text, int width) {
        return this == RIGHT ? width + "s" : text;
    }
}
