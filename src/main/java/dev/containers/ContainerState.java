package dev.containers;

import dev.tamboui.style.Color;

import java.util.Locale;

/**
 * States of Apple container
 *
 */
public enum ContainerState {

    CREATED(Color.rgb(0x7A, 0xA2, 0xF7)),

    STARTING(Color.rgb(0xE0, 0xAF, 0x68)),

    RUNNING(Color.rgb(0x9E, 0xCE, 0x6A)),

    PAUSED(Color.rgb(0xBB, 0x9A, 0xF7)),

    STOPPING(Color.rgb(0xE0, 0xAF, 0x68)),

    STOPPED(Color.rgb(0x56, 0x5F, 0x89)),

    ERROR(Color.rgb(0xF7, 0x76, 0x8E)),

    UNKNOWN(Color.rgb(0x56, 0x5F, 0x89));

    private final Color color;

    ContainerState(Color color) {
        this.color = color;
    }

    public Color color() {
        return color;
    }


    public static ContainerState of(String state) {
        if (state == null) {
            return UNKNOWN;
        }
        try {
            return valueOf(state.strip().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
