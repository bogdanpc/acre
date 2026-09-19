package dev.ui;

import dev.applecontainer.CliResult;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

/// Runs CLI actions on virtual threads
public final class CliRunner {

    public static final Executor DEFAULT_EXECUTOR = Executors.newThreadPerTaskExecutor(
            Thread.ofVirtual().name("acre-cli-", 0).factory());

    private final Executor executor;

    public CliRunner() {
        this(DEFAULT_EXECUTOR);
    }

    public CliRunner(Executor executor) {
        this.executor = executor;
    }

    /// Runs `action`, then hands `onDone` the failure message, if any
    public void run(Supplier<? extends CliResult<?>> action, Consumer<Optional<String>> onDone) {
        executor.execute(() -> {
            var failure = Optional.<String>empty();
            try {
                if (action.get() instanceof CliResult.Failure<?>(var message)) {
                    failure = Optional.of(message);
                }
            } catch (RuntimeException e) {
                failure = Optional.of(String.valueOf(e));
            } finally {
                onDone.accept(failure);
            }
        });
    }
}
