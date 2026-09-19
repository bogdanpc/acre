package dev.applecontainer;

import tools.jackson.jr.stree.JrsNumber;
import tools.jackson.jr.stree.JrsValue;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class CliJson {
    private CliJson() {
    }

    public static String text(JrsValue node, String key) {
        return text(node.path(key));
    }

    public static String text(JrsValue node) {
        var value = node.asText();
        return value == null ? "" : value;
    }

    /** JSON number or a numeric string. */
    public static long number(JrsValue node, String key) {
        var field = node.path(key);
        if (field instanceof JrsNumber value) {
            return value.getValue().longValue();
        }
        try {
            return Long.parseLong(text(field).strip());
        } catch (NumberFormatException _) {
            return 0L;
        }
    }

    public static boolean bool(JrsValue node, String key) {
        return "true".equals(text(node, key));
    }

    public static Stream<JrsValue> values(JrsValue array) {
        return IntStream.range(0, array.size()).mapToObj(array::path);
    }
}
