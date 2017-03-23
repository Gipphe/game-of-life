package app;

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
     * Horizontal size of the pattern.
     */
    private final int sizeX;

    /**
     * Vertical size of the pattern.
     */
    private final int sizeY;

    /**
     * Constructor
     * @param name Name of the pattern in question.
     * @param pattern Two-dimensional array representing the pattern.
     */
    Pattern(String name, byte[][] pattern) {
        this.name = name;
        this.pattern = pattern;
        this.sizeY = pattern.length;
        if (pattern.length == 0) {
            this.sizeX = 0;
        } else {
            this.sizeX = pattern[0].length;
        }
    }

    /**
     * Getter for the name.
     * @return Name of the pattern.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the pattern information.
     * @return The pattern array.
     */
    public byte[][] getPattern() {
        return pattern;
    }

    /**
     * Getter for the horizontal size.
     * @return The horizontal size.
     */
    public int getSizeX() {
        return sizeX;
    }

    /**
     * Getter for the vertical size.
     * @return The vertical size.
     */
    public int getSizeY() {
        return sizeY;
    }
}

/**
 * Container class for all Patterns.
 */
public class PatternCollection {
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
    private static final Pattern[] Collection = new Pattern[] {
            Clear,
            Glider,
            Blinker,
            Toad,
            Beacon,
            Pulsar,
            Pentadecathlon,
            LightweightSpaceship
    };

    /**
     * Getter for the entire pattern collection.
     * @return An array with the collection.
     */
    public static Pattern[] getCollection() {
        return Collection;
    }
}
