package dev.testing;

import dev.applecontainer.CliResult;

import static org.junit.jupiter.api.Assertions.fail;

/** Unwraps a {@link CliResult} in a test, and fails the test when it is the other case. */
public final class CliResults {

    private CliResults() {
    }

    public static <T> T value(CliResult<T> result) {
        return switch (result) {
            case CliResult.Success<T>(var value) -> value;
            case CliResult.Failure<T>(var message) -> fail("expected a value but got the failure: " + message);
        };
    }

    public static String message(CliResult<?> result) {
        return switch (result) {
            case CliResult.Success<?>(var value) -> fail("expected a failure but got the value: " + value);
            case CliResult.Failure<?>(var message) -> message;
        };
    }
}
