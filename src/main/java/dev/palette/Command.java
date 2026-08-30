package dev.palette;

/**
 * Palette Command
 */
public record Command(String label, Runnable run) {
}
