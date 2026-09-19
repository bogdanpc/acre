package dev.ui;

import dev.applecontainer.CliResult;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public final class Loader<T> {

    private final Supplier<CliResult<T>> source;
    private final T initial;
    private final Executor executor;

    private final AtomicReference<T> value;
    private final AtomicReference<String> failure = new AtomicReference<>();
    private final AtomicBoolean loading = new AtomicBoolean();
    private final AtomicBoolean started = new AtomicBoolean();

    private volatile long lastLoad;
    private volatile Duration refreshAfter;

    public Loader(Supplier<CliResult<T>> source, T initial) {
        this(source, initial, CliRunner.DEFAULT_EXECUTOR);
    }

    public Loader(Supplier<CliResult<T>> source, T initial, Executor executor) {
        this.source = source;
        this.initial = initial;
        this.executor = executor;
        this.value = new AtomicReference<>(initial);
    }

    public static <T> Loader<T> of(T value) {
        return new Loader<>(() -> CliResult.success(value), value, Runnable::run);
    }


    public Loader<T> refreshEvery(Duration period) {
        this.refreshAfter = period;
        return this;
    }

    public T value() {
        startOrRefresh();
        return value.get();
    }

    public String failure() {
        startOrRefresh();
        return failure.get();
    }

    public boolean loading() {
        return loading.get();
    }

    public void reload() {
        started.set(true);
        if (!loading.compareAndSet(false, true)) {
            return;
        }
        executor.execute(() -> {
            try {
                switch (source.get()) {
                    case CliResult.Success<T>(var loaded) -> {
                        value.set(loaded);
                        failure.set(null);
                    }
                    case CliResult.Failure<T>(var message) -> {
                        value.set(initial);
                        failure.set(message);
                    }
                }
            } catch (RuntimeException e) {
                value.set(initial);
                failure.set(String.valueOf(e));
            } finally {
                lastLoad = System.currentTimeMillis();
                loading.set(false);
            }
        });
    }

    private void startOrRefresh() {
        if (started.compareAndSet(false, true)) {
            reload();
            return;
        }
        var period = refreshAfter;
        if (period != null && !loading.get()
                && System.currentTimeMillis() - lastLoad >= period.toMillis()) {
            reload();
        }
    }
}
