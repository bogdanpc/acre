package dev.ui;

import org.junit.jupiter.api.Test;

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormatTest {

    @Test
    void bytesSwitchesToTheNextUnitAtEveryStep() {
        assertEquals("1023 B", Format.bytes(1023));
        assertEquals("1.0 KB", Format.bytes(1024));
        assertEquals("1.5 MB", Format.bytes(1536 * 1024));
    }

    @Test
    void bytesStopsAtPetabytes() {
        assertEquals("1024.0 PB", Format.bytes(1024L * 1024 * 1024 * 1024 * 1024 * 1024));
    }

    @Test
    void dateKeepsTheOriginalTextWhenItIsNotAnInstant() {
        assertEquals("", Format.date("  ", ZoneId.of("UTC")));
        assertEquals("not a date", Format.date("not a date", ZoneId.of("UTC")));
        assertEquals("2 Mar 2026 at 14:30", Format.date("2026-03-02T14:30:00Z", ZoneId.of("UTC")));
    }

    @Test
    void digestDropsThePrefixAndKeepsTwelveCharacters() {
        assertEquals("d3e1620b530c", Format.digest("sha256:d3e1620b530c944afa6e887d22eb899824da68e19c52024bf98f5220c88a65b2"));
        assertEquals("short", Format.digest("short"));
    }

    @Test
    void blankBecomesTheMissingMarker() {
        assertEquals("-", Format.valueOrPlaceholder(null));
        assertEquals("-", Format.valueOrPlaceholder(" "));
        assertEquals("value", Format.valueOrPlaceholder("value"));
    }
}
