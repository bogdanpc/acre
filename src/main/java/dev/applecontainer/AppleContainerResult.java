package dev.applecontainer;

public record AppleContainerResult(int exitCode, String stdOut, String stdErr) {

    public boolean isSuccess() {
        return exitCode == 0;
    }
}
