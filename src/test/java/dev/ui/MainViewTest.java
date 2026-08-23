package dev.ui;

import dev.tamboui.buffer.Buffer;
import dev.tamboui.layout.Rect;
import dev.tamboui.terminal.Frame;
import dev.tamboui.toolkit.element.DefaultRenderContext;
import dev.tamboui.toolkit.event.EventRouter;
import dev.tamboui.tui.RenderThreadTestSupport;
import dev.tamboui.tui.event.KeyEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainViewTest {

    private static final Rect SCREEN = Rect.of(80, 24);

    private final DefaultRenderContext context = DefaultRenderContext.createEmpty();
    private final EventRouter router = context.eventRouter();

    @BeforeEach
    void markRenderThread() {
        RenderThreadTestSupport.markCurrentThreadAsRenderThread();
    }

    @AfterEach
    void releaseRenderThread() {
        router.close();
        RenderThreadTestSupport.unmarkCurrentThread();
    }

    @Test
    void showsTheThreeTabs() {
        var view = new MainView(() -> {});

        var screen = render(view);

        assertTrue(screen.contains("Containers ("), screen);
        assertTrue(screen.contains("Images ("), screen);
        assertTrue(screen.contains("Volumes ("), screen);
    }

    @Test
    void quitsWhenTheUserPressesQ() {
        var quit = new Runnable() {
            boolean called;

            @Override
            public void run() {
                called = true;
            }
        };
        var view = new MainView(quit);

        render(view);
        router.route(KeyEvent.ofChar('q'));

        assertTrue(quit.called);
    }

    @Test
    void opensAndClosesTheHelpOverlayWithQuestionMark() {
        var view = new MainView(() -> {});
        render(view);

        router.route(KeyEvent.ofChar('?'));
        var withHelp = render(view);
        router.route(KeyEvent.ofChar('?'));
        var withoutHelp = render(view);

        assertTrue(withHelp.contains("press ? or esc to close"), withHelp);
        assertFalse(withoutHelp.contains("press ? or esc to close"), withoutHelp);
    }

    private String render(MainView view) {
        router.clear();
        var buffer = Buffer.empty(SCREEN);
        view.render(Frame.forTesting(buffer), SCREEN, context);
        return text(buffer);
    }

    private String text(Buffer buffer) {
        var out = new StringBuilder();
        for (int y = 0; y < buffer.height(); y++) {
            for (int x = 0; x < buffer.width(); x++) {
                out.append(buffer.get(x, y).symbol());
            }
            out.append('\n');
        }
        return out.toString();
    }

}
