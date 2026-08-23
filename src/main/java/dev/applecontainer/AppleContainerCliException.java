package dev.applecontainer;

public class AppleContainerCliException extends RuntimeException {

    public AppleContainerCliException(String message) {
        super(message);
    }

    public AppleContainerCliException(String message, Throwable cause) {
        super(message, cause);
    }
}
