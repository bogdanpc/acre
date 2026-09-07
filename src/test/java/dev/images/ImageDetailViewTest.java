package dev.images;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageDetailViewTest {

    @Test
    void sizesTheColumnToTheLongestValue() {
        var uses = List.of(
                new ImageUse("dff-shared-mailpit", "-", "dff-shared-mailpit"),
                new ImageUse("dff-dff-434-report-cycle-mailpit", "192.168.64.11", "dff-dff-434-report-cycle-mailpit"));

        assertEquals("dff-dff-434-report-cycle-mailpit".length() + 2,
                ImageDetailView.columnWidth("CONTAINER", uses, ImageUse::container));
    }

    @Test
    void keepsTheHeadingReadableWhenValuesAreShorter() {
        var uses = List.of(new ImageUse("db", "-", "db"));

        assertEquals("IP ADDRESS".length() + 2,
                ImageDetailView.columnWidth("IP ADDRESS", uses, ImageUse::address));
    }

    @Test
    void capsRunawayValues() {
        var uses = List.of(new ImageUse("c".repeat(120), "-", "-"));

        assertEquals(50, ImageDetailView.columnWidth("CONTAINER", uses, ImageUse::container));
    }
}
