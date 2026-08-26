package dev.ui;

import dev.applecontainer.AppleContainerCliException;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public final class Loader<T> {

    private static final Executor DEFAULT_EXECUTOR = Executors.newThreadPerTaskExecutor(
            Thread.ofVirtual().name("acre-loader-", 0).factory());

    private final Supplier<T> source;
    private final T initial;
    private final Executor executor;

    private final AtomicReference<T> value;
    private final AtomicReference<String> failure = new AtomicReference<>();
    private final AtomicBoolean loading = new AtomicBoolean();
    private final AtomicBoolean started = new AtomicBoolean();

    private volatile long lastLoad;
    private volatile Duration refreshAfter;

    public Loader(Supplier<T> source, T initial) {
        this(source, initial, DEFAULT_EXECUTOR);
    }

    public Loader(Supplier<T> source, T initial, Executor executor) {
        this.source = source;
        this.initial = initial;
        this.executor = executor;
        this.value = new AtomicReference<>(initial);
    }

    public static <T> Loader<T> of(T value) {
        return new Loader<>(() -> value, value, Runnable::run);
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
                value.set(source.get());
                failure.set(null);
            } catch (AppleContainerCliException e) {
                value.set(initial);
                failure.set(e.getMessage());
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
