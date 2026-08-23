package dev.tamboui.tui;

/**
 * Marks the current thread as a TamboUI render thread.
 *
 * <p>TamboUI only lets the render thread touch elements, and only
 * {@code TuiRunner} can set that mark. This helper lives in the framework
 * package so tests can render without starting a terminal.
 */
public final class RenderThreadTestSupport {

    private RenderThreadTestSupport() {
    }

    public static void markCurrentThreadAsRenderThread() {
        RenderThread.markAsRenderThread();
    }

    public static void unmarkCurrentThread() {
        RenderThread.clearRenderThread();
    }

}
