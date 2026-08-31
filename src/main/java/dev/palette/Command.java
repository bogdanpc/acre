package dev.palette;

/**
 * Palette Command
 */
public record Command(String label, String shortcut, Runnable run) {

    public Command(String label, Runnable run) {
        this(label, null, run);
    }

    public boolean hasShortcut() {
        return shortcut != null;
    }
}
