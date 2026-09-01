package dev.testing;

import dev.tamboui.buffer.Buffer;
import dev.tamboui.layout.Rect;
import dev.tamboui.terminal.Frame;
import dev.tamboui.toolkit.element.DefaultRenderContext;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.tui.RenderThreadTestSupport;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.tui.event.KeyModifiers;
import dev.ui.KeyBindings;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public final class TestScreen {

    private final Rect area;
    private final DefaultRenderContext context = DefaultRenderContext.createEmpty();

    private Supplier<? extends Element> view;

    public TestScreen() {
        this(80, 24);
    }

    public TestScreen(int width, int height) {
        area = Rect.of(width, height);

        // Because we test the Element layer
        RenderThreadTestSupport.markAsRenderThread();
    }

    public Screen show(Element view) {
        return show(() -> view);
    }

    public Screen show(Supplier<? extends Element> view) {
        this.view = view;
        return draw();
    }

    public Screen press(char key) {
        return send(KeyEvent.ofChar(key, KeyBindings.get()));
    }

    public Screen press(KeyCode key) {
        return send(KeyEvent.ofKey(key, KeyBindings.get()));
    }

    public Screen pressCtrl(char key) {
        return send(KeyEvent.ofChar(key, KeyModifiers.CTRL, KeyBindings.get()));
    }

    public Screen type(String text) {
        var screen = draw(); // the answer for an empty text
        for (char key : text.toCharArray()) {
            screen = press(key);
        }
        return screen;
    }

    private Screen send(KeyEvent key) {
        context.eventRouter().route(key);
        return draw();
    }

    private Screen draw() {
        if (view == null) {
            throw new IllegalStateException("call show(...) before you press a key");
        }
        context.eventRouter().clear();
        var buffer = Buffer.empty(area);
        view.get().render(Frame.forTesting(buffer), area, context);
        return read(buffer);
    }

    private static Screen read(Buffer buffer) {
        return new Screen(IntStream.range(0, buffer.height())
                .mapToObj(y -> row(buffer, y))
                .toList());
    }

    private static String row(Buffer buffer, int y) {
        var row = new StringBuilder(buffer.width());
        for (int x = 0; x < buffer.width(); x++) {
            row.append(buffer.get(x, y).symbol());
        }
        return row.toString();
    }

    public record Screen(List<String> lines) {

        public Screen assertShows(String text) {
            if (!toString().contains(text)) {
                throw new AssertionError("expected \"" + text + "\" on the screen:\n" + this);
            }
            return this;
        }

        public Screen assertDoesNotShow(String text) {
            if (toString().contains(text)) {
                throw new AssertionError("did not expect \"" + text + "\" on the screen:\n" + this);
            }
            return this;
        }

        public String line(String text) {
            return lines.stream()
                    .filter(line -> line.contains(text))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("no line with \"" + text + "\":\n" + this));
        }

        public int columnOf(String text) {
            return line(text).indexOf(text);
        }

        @Override
        public String toString() {
            return String.join("\n", lines);
        }
    }
}
