package dev.palette;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FuzzyTest {


    @Test
    void rankKeepsTheOriginalOrderForABlankQuery() {
        var commands = commands("b", "a", "c");
        assertEquals(List.of("b", "a", "c"), labels(Fuzzy.rank("", commands)));
    }

    @Test
    void blankQueryMatchesEverything() {
        var hit = Fuzzy.match("  ", "anything");
        assertNotNull(hit);
        assertEquals(0, hit.orElseThrow().score());
        assertEquals(0, hit.orElseThrow().positions().length);
    }

    private static List<Command> commands(String... labels) {
        return Arrays.stream(labels)
                .map(label -> new Command(label, () -> {})).toList();
    }

    private static List<String> labels(List<Command> commands) {
        return commands.stream().map(Command::label).toList();
    }
}
