package rules;

/**
 * Represents a set of rules that determines the new state of a given cell for each generation.
 */
public class RuleSet {
    /**
     * An array of ranges where an alive cell will survive onto the next generation.
     */
    private final Range[] surviveRanges;
    /**
     * An array of ranges where a dead cell will be birthed into an alive cell in the coming generation.
     */
    private final Range[] birthRanges;
    /**
     * An array of states where a cell is considered "alive".
     * Conway's and HighLife only has one state, "1".
     */
    private final byte[] aliveStates;
    /**
     * An array of states where a cell is considered "dead".
     * Conway's and HighLife only has one state, "0".
     */
    private final byte[] deadStates;
    /**
     * Name for the rule set.
     */
    private final String name;

    /**
     * Constructor accepting an array of ranges where an alive cell will survive on, as well as an array of ranges
     * where a dead cell will come to life.
     *
     * @param surviveRanges Ranges where an alive cell remains alive.
     * @param birthRanges Ranges where a dead cell comes to life.
     */
    public RuleSet(String name, Range[] surviveRanges, Range[] birthRanges) {
        this.name = name;
        this.surviveRanges = surviveRanges;
        this.birthRanges = birthRanges;
        aliveStates = new byte[]{1};
        deadStates = new byte[]{0};
    }

    /**
     * Constructor accepting an array of ranges where an alive cell will survive on, as well as an array of ranges
     * where a dead cell will come to life. On top of this, it accepts an array of states where a cell is considered
     * "alive", as well as an array of states where a cell is considered "dead".
     *
     * @param surviveRanges Ranges where an alive cell remains alive.
     * @param birthRanges Ranges where a dead cell comes to life.
     * @param aliveStates States where a cell is considered alive.
     * @param deadStates States where a cell is considered dead.
     */
    public RuleSet(String name, Range[] surviveRanges, Range[] birthRanges, byte[] aliveStates, byte[] deadStates) {
        this.name = name;
        this.surviveRanges = surviveRanges;
        this.birthRanges = birthRanges;
        this.aliveStates = aliveStates;
        this.deadStates = deadStates;
    }

    /**
     * Checks whether an array contains the passed value.
     *
     * @param array Array to check.
     * @param value Value to check.
     * @return True if the value is in the array, false otherwise.
     */
    private boolean contains(byte[] array, byte value) {
        for (byte val : array) {
            if (val == value) return true;
        }
        return false;
    }

    /**
     * Evaluates the new state of a given cell in the coming generation based on the cell's current state as well as
     * the contained ranges.
     *
     * @param cellState Current state of the cell before the coming generation.
     * @param numNeighbors Number of neighbors the cell currently has.
     * @return The new state of the cell.
     */
    public byte getNewState(byte cellState, int numNeighbors) {
        boolean isAlive = contains(aliveStates, cellState);

        // Initialize new state at first dead state, not taking into account rule sets where a cell can have more than
        // two states.
        byte newState = deadStates[0];
        if (isAlive) {
            for (Range range : surviveRanges) {
                int min = range.getMin();
                int max = range.getMax();
                if (numNeighbors <= max && numNeighbors >= min) {
                    newState = 1;
                }
            }
        } else {
            for (Range range : birthRanges) {
                int min = range.getMin();
                int max = range.getMax();
                if (numNeighbors <= max && numNeighbors >= min) {
                    newState = 1;
                }
            }
        }
        return newState;
    }

    /**
     * Getter for the rule set's name.
     *
     * @return The name of the rule set.
     */
    public String getName() {
        return name;
    }
}