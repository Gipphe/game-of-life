package app;

class Pattern {
    private final String name;
    private final byte[][] pattern;

    public Pattern(String name, byte[][] pattern) {
        this.name = name;
        this.pattern = pattern;
    }
}

public class PatternCollection {
    private static final Pattern Clear;
    private static final Pattern Glider;
    private static final Pattern Blinker;
    private static final Pattern Toad;
    private static final Pattern Beacon;
    private static final Pattern Pulsar;
    private static final Pattern Pentadecathlon;
    private static final Pattern LightweightSpaceship;
    private static final Pattern[] Collection;

    static {
        Clear = new Pattern("Clear", new byte[][] {});
        Glider = new Pattern("Glider", new byte[][] {{0,2}, {1,0}, {1,2}, {2,1}, {2,2}});
        Blinker = new Pattern("Blinker", new byte[][] {{0,1}, {1,1}, {2,1}});
        Toad = new Pattern ("Toad", new byte[][] {{0,1}, {1,0}, {1,1}, {2,0}, {2,1}, {3,0}});
        Beacon = new Pattern ("Beacon", new byte[][] {{0,0}, {0,1}, {1,0}, {2,3}, {3,2}, {3,3}});
        Pulsar = new Pattern ("Pulsar", new byte[][] {{0,2}, {0,3}, {0,4}, {0,8}, {0,9}, {0,10},
                {2,0}, {2,5}, {2,7}, {2,12}, {3,0}, {3,5}, {3,7}, {3,12}, {4,0},
                    {4,5}, {4,7}, {4,12}, {5,2}, {5,3}, {5,4}, {5,8}, {5,9}, {5,10},
                    {7,2}, {7,3}, {7,4}, {7,8}, {7,9}, {7,10}, {8,0}, {8,5}, {8,7},
                    {8,12}, {9,0}, {9,5}, {9,7}, {9,12}, {10,0},
                    {10,5}, {10,7}, {10,12}, {12,2}, {12,3}, {12,4}, {12,8}, {12,9}, {12,10}});
        Pentadecathlon = new Pattern ("Pentadecathlon", new byte[][] {{0,1}, {1,1}, {2,0}, {2,2}, {3,1},
                    {4,1}, {5,1}, {6,1}, {7,0}, {7,2}, {8,1}, {9,1}});
        LightweightSpaceship = new Pattern ("LightweightSpaceship", new byte[][] {{0,1}, {0,3}, {1,0}, {2,0}, {3,0},
                    {3,3}, {4,0}, {4,1}, {4,2}});
        Collection = new Pattern[] {Clear, Glider, Blinker, Toad, Beacon, Pulsar, Pentadecathlon, LightweightSpaceship};
    }

    public static Pattern[] getCollection() {
        return Collection;
    }
}
