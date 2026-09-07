package dev.ui;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public final class Format {

    private static final String UNITS = "KMGTP";
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("d MMM yyyy 'at' HH:mm", Locale.ENGLISH);
    private static final int DIGEST_LENGTH = 12;
    private static final String DIGEST_PREFIX = "sha256:";
    private static final String MISSING = "-";

    private Format() {
    }

    public static String bytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        var unit = Math.min((int) (Math.log(bytes) / Math.log(1024)), UNITS.length());
        return "%.1f %sB".formatted(bytes / Math.pow(1024, unit), UNITS.charAt(unit - 1));
    }

    public static String date(String iso, ZoneId zone) {
        if (iso == null || iso.isBlank()) {
            return "";
        }
        try {
            return DATE.format(Instant.parse(iso).atZone(zone));
        } catch (DateTimeParseException _) {
            return iso;
        }
    }

    public static String digest(String digest) {
        var hex = digest.startsWith(DIGEST_PREFIX) ? digest.substring(DIGEST_PREFIX.length()) : digest;
        return hex.length() <= DIGEST_LENGTH ? hex : hex.substring(0, DIGEST_LENGTH);
    }

    public static String valueOrPlaceholder(String value) {
        return value == null || value.isBlank() ? MISSING : value;
    }
}
