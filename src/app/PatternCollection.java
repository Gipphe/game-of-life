package app;

import java.util.ArrayList;
import java.util.List;

/**
 * Container class for Patterns
 *
 * Holds the name and actual array information for the specified pattern.
 */
class Pattern {
    /**
     * Name of the pattern.
     */
    private final String name;

    /**
     * Actual pattern table.
     */
    private final byte[][] pattern;

    /**
     * Constructor
     * @param name Name of the pattern in question.
     * @param pattern Two-dimensional array representing the pattern.
     */
    Pattern(String name, byte[][] pattern) {
        this.name = name;
        this.pattern = pattern;
    }

    /**
     * Getter for the name.
     * @return Name of the pattern.
     */
    String getName() {
        return name;
    }

    /**
     * Getter for the pattern information.
     * @return The pattern array.
     */
    byte[][] getPattern() {
        return pattern;
    }
}

/**
 * Container class for all Patterns.
 */
class PatternCollection {
    private static final Pattern Clear = new Pattern("Clear", new byte[][] {});
    private static final Pattern Glider = new Pattern("Glider", new byte[][] {
            {0,1,0},
            {1,0,0},
            {1,1,1}
    });
    private static final Pattern Blinker = new Pattern("Blinker", new byte[][] {
            {1,1,1}
    });
    private static final Pattern Toad = new Pattern("Toad", new byte[][]{
            {0,1,1,1},
            {1,1,1,0}
    });
    private static final Pattern Beacon = new Pattern("Beacon", new byte[][] {
            {1,1,0,0},
            {1,0,0,0},
            {0,0,0,1},
            {0,0,1,1}
    });
    private static final Pattern Pulsar = new Pattern("Pulsar", new byte[][] {
            {0,0,1,1,1,0,0,0,1,1,1,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0},
            {1,0,0,0,0,1,0,1,0,0,0,0,1},
            {1,0,0,0,0,1,0,1,0,0,0,0,1},
            {1,0,0,0,0,1,0,1,0,0,0,0,1},
            {0,0,1,1,1,0,0,0,1,1,1,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,1,1,1,0,0,0,1,1,1,0,0},
            {1,0,0,0,0,1,0,1,0,0,0,0,1},
            {1,0,0,0,0,1,0,1,0,0,0,0,1},
            {1,0,0,0,0,1,0,1,0,0,0,0,1},
            {0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,1,1,1,0,0,0,1,1,1,0,0}
    });
    private static final Pattern Pentadecathlon = new Pattern("Pentadecathlon", new byte[][] {
            {0,0,1,0,0,0,0,1,0,0},
            {1,1,0,1,1,1,1,0,1,1},
            {0,0,1,0,0,0,0,1,0,0}
    });
    private static final Pattern LightweightSpaceship = new Pattern("Lightweight spaceship", new byte[][] {
            {0,1,0,0,1},
            {1,0,0,0,0},
            {1,0,0,0,1},
            {1,1,1,1,0}
    });
    private static final Pattern[] collection = new Pattern[] {
            Clear,
            Glider,
            Blinker,
            Toad,
            Beacon,
            Pulsar,
            Pentadecathlon,
            LightweightSpaceship
    };
    private static final List<String> names = new ArrayList<>();
    static {
        for (Pattern pattern : collection) {
            names.add(pattern.getName());
        }
    }

    /**
     * Getter for the entire pattern collection.
     * @return An array with the collection.
     */
    static Pattern[] getCollection() {
        return collection;
    }

    /**
     * Getter for the list of names.
     * @return An array with the names.
     */
    static List<String> getNames() {
        return names;
    }
}
