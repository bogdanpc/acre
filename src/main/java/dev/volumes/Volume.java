package dev.volumes;

/**
 * Volume from container command output
 */
public record Volume(String name, String driver, String format, long size, String source) {
}
