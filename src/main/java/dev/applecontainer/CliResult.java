package dev.applecontainer;

/**
 * What the Apple container CLI gave back: a value, or a message that says why it failed.
 *
 * <p>A command that exits with an error is a normal answer here, not an exception. The message
 * travels up as a value, so the caller can show it.
 */
public sealed interface CliResult<T> {

    record Success<T>(T value) implements CliResult<T> {
    }

    record Failure<T>(String message) implements CliResult<T> {
    }

    static <T> CliResult<T> success(T value) {
        return new Success<>(value);
    }

    static <T> CliResult<T> failure(String message) {
        return new Failure<>(message);
    }
}
