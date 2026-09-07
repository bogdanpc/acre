package dev.images;

import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.text.Line;
import dev.tamboui.text.Span;
import dev.tamboui.text.Text;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.element.StyledElement;
import dev.tamboui.toolkit.elements.ListElement;
import dev.tamboui.widgets.common.ScrollBarPolicy;
import dev.ui.Format;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static dev.tamboui.toolkit.Toolkit.*;

public final class ImageDetailView {

    private static final Color ACCENT = Color.rgb(122, 162, 247);
    private static final Style LABEL = Style.EMPTY.fg(Color.rgb(0x56, 0x5F, 0x89));
    private static final Style HEADING = Style.EMPTY.fg(Color.WHITE).bold();
    private static final Style NAME = Style.EMPTY.fg(Color.CYAN);

    private static final int LABEL_WIDTH = 12;
    private static final int COLUMN_GUTTER = 2;
    private static final int COLUMN_CEILING = 48;
    private static final String NONE = "No container runs this image.";
    private static final String HINTS = "r reload · ↑ ↓ scroll · esc back";

    private ListElement<?> body;
    private ImageDetail rendered;

    public StyledElement<?> element(ImageDetail detail) {
        if (!detail.equals(rendered)) {
            rendered = detail;
            body = list(lines(detail.image(), detail.uses()).toArray(StyledElement<?>[]::new))
                    .displayOnly()
                    .autoScroll()
                    .scrollbar(ScrollBarPolicy.AS_NEEDED);
        }
        return panel(" " + detail.image().reference() + " ", body.fill(), hint())
                .rounded()
                .borderColor(ACCENT)
                .padding(1);
    }

    private static List<StyledElement<?>> lines(ContainerImage image, List<ImageUse> uses) {
        var lines = new ArrayList<StyledElement<?>>();
        lines.add(columns(heading("Overview"), heading("Technical Details")));
        lines.add(blank());
        overview(lines, image);
        for (var configuration : image.configurations()) {
            lines.add(blank());
            configuration(lines, configuration);
        }
        lines.add(blank());
        lines.add(heading("Used By Containers"));
        usedBy(lines, uses);
        return lines;
    }

    private static void overview(List<StyledElement<?>> lines, ContainerImage image) {
        var overview = List.of(
                field("Reference", image.reference()),
                field("Name", image.name()),
                field("Tag", image.tag()),
                field("Size", Format.bytes(image.size())));
        var technical = List.of(
                field("Media Type", image.mediaType()),
                field("Digest", Format.digest(image.digest())),
                field("Index Size", Format.bytes(image.indexSize())));
        for (int i = 0; i < overview.size(); i++) {
            lines.add(columns(overview.get(i), i < technical.size() ? technical.get(i) : text("")));
        }
    }

    private static void configuration(List<StyledElement<?>> lines, ContainerImage.Configuration configuration) {
        lines.add(configurationHeading(configuration));
        add(lines, "Entrypoint", configuration.entrypoint());
        add(lines, "Cmd", configuration.cmd());
        add(lines, "Working Dir", configuration.workingDir());
        if (configuration.environment().isEmpty()) {
            return;
        }
        lines.add(label("Environment"));
        configuration.environment().stream().map(ImageDetailView::variable).forEach(lines::add);
    }

    private static void add(List<StyledElement<?>> lines, String label, String value) {
        if (!value.isBlank()) {
            lines.add(field(label, value));
        }
    }

    private static StyledElement<?> configurationHeading(ContainerImage.Configuration configuration) {
        return line(Span.styled("Configuration ", HEADING),
                Span.styled("(" + configuration.platform() + ")", LABEL));
    }

    private static void usedBy(List<StyledElement<?>> lines, List<ImageUse> uses) {
        if (uses.isEmpty()) {
            lines.add(text(NONE).dim().length(1));
            return;
        }
        var containerWidth = columnWidth("CONTAINER", uses, ImageUse::container);
        var addressWidth = columnWidth("IP ADDRESS", uses, ImageUse::address);
        lines.add(usedByRow(containerWidth, addressWidth,
                Span.styled("CONTAINER", LABEL),
                Span.styled("IP ADDRESS", LABEL),
                Span.styled("HOSTNAME", LABEL)));
        for (var use : uses) {
            lines.add(usedByRow(containerWidth, addressWidth,
                    Span.styled(Format.valueOrPlaceholder(use.container()), NAME),
                    Span.raw(Format.valueOrPlaceholder(use.address())),
                    Span.raw(Format.valueOrPlaceholder(use.hostname()))));
        }
    }

    static int columnWidth(String heading, List<ImageUse> uses, Function<ImageUse, String> cell) {
        var widest = uses.stream()
                .map(use -> Format.valueOrPlaceholder(cell.apply(use)).length())
                .reduce(heading.length(), Math::max);
        return Math.min(widest, COLUMN_CEILING) + COLUMN_GUTTER;
    }

    private static StyledElement<?> usedByRow(int containerWidth, int addressWidth,
                                              Span container, Span address, Span hostname) {
        return row(
                line(container).max(containerWidth),
                line(address).max(addressWidth),
                line(hostname).fill())
                .length(1);
    }

    private static StyledElement<?> columns(StyledElement<?> left, StyledElement<?> right) {
        return row(left.fill(3), right.fill(2)).length(1);
    }

    private static StyledElement<?> field(String label, String value) {
        return line(Span.styled(("%-" + LABEL_WIDTH + "s").formatted(label), LABEL), Span.raw(Format.valueOrPlaceholder(value)));
    }

    private static StyledElement<?> variable(ContainerImage.Variable variable) {
        return line(
                Span.raw("  "),
                Span.styled(variable.name(), NAME),
                Span.styled(" = ", LABEL),
                Span.raw(variable.value()));
    }

    private static StyledElement<?> heading(String title) {
        return text(title).style(HEADING).length(1);
    }

    private static StyledElement<?> label(String title) {
        return text(title).style(LABEL).length(1);
    }

    private static StyledElement<?> blank() {
        return text("").length(1);
    }

    private static StyledElement<?> line(Span... spans) {
        return richText(Text.from(Line.from(spans))).length(1);
    }

    private static Element hint() {
        return text(HINTS).dim().length(1);
    }
}
