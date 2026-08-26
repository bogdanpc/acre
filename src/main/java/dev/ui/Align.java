package dev.ui;

public enum Align {

    LEFT,
    RIGHT,
    CENTER;

    String pad(String text, int width) {
        var gap = width - text.length();
        if (gap <= 0) {
            return text;
        }
        return switch (this) {
            case LEFT -> text;
            case RIGHT -> " ".repeat(gap) + text;
            case CENTER -> " ".repeat(gap / 2) + text;
        };
    }
}
