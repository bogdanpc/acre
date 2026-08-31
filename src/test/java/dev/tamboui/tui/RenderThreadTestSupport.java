package dev.tamboui.tui;

/**
 * {@link RenderThread#markAsRenderThread()} is package-private, so this shim has to live in
 * TamboUI's package. Without the mark, an {@code Element} refuses to render off the event loop.
 */
public final class RenderThreadTestSupport {

    private RenderThreadTestSupport() {
    }

    public static void markAsRenderThread() {
        RenderThread.markAsRenderThread();
    }
}
