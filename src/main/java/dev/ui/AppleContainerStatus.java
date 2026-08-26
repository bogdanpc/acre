package dev.ui;

import dev.tamboui.style.Color;

public enum AppleContainerStatus {

    UP("up", Color.GREEN),

    DOWN("down", Color.RED),

    UNKNOWN("unknown", Color.DARK_GRAY);

    private final String label;
    private final Color color;

    AppleContainerStatus(String label, Color color) {
        this.label = label;
        this.color = color;
    }

    public Color color() {
        return color;
    }

    public String label() {
        return label;
    }

    public static AppleContainerStatus of(boolean running) {
        return running ? UP : DOWN;
    }
}
