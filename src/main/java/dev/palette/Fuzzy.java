package dev.palette;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Fuzzy {

    public record Match(int score, int[] positions) {
    }

    private static final Match EMPTY = new Match(0, new int[0]);

    private Fuzzy() {
    }

    public static Optional<Match> match(String query, String text) {
        if (query == null || query.isBlank()) {
            return Optional.of(EMPTY);
        }
        if (text == null) {
            return Optional.of(EMPTY);
        }

        var positions = new int[query.length()];
        int typed = 0;

        for (var i = 0; i < text.length() && typed < query.length(); i++) {
            if (text.charAt(i) == query.charAt(typed)) {
                positions[typed++] = i;
            }
        }

        if (typed < query.length()) {
            return Optional.empty();
        }

        return Optional.of(new Match(0, positions));
    }

    public static List<Command> rank(String query, List<Command> commands) {
        if (query == null || query.isBlank()) {
            return commands;
        }

        var matched = new ArrayList<Command>();
        for (var command : commands) {
            if (match(query, command.label()).isPresent()) {
                matched.add(command);
            }
        }

        return matched;
    }
}
