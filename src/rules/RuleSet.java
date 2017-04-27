package rules;

/**
 * Represents a set of rules that determines the new state of a given cell for each generation.
 */
public class RuleSet {
    /**
     * An array of ranges where an alive cell will survive onto the next generation.
     */
    private final StateRange[] surviveStateRanges;
    /**
     * An array of ranges where a dead cell will be birthed into an alive cell in the coming generation.
     */
    private final StateRange[] birthStateRanges;
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
     * A string representing the conditions for each state change of a cell.
     */
    private String ruleString;
    /**
     * Name for the rule set.
     */
    private final String name;

    /**
     * Compares this rule set's rule string with the passed rule string and determined whether they represent the same
     * set of rules.
     * @param otherRuleString Other rule set's string to compare against.
     * @return True if both rule sets represent the same rules, false otherwise.
     */
    public boolean isEqual(String otherRuleString) {
        String thisRule = ruleString.toLowerCase();
        String otherRule = otherRuleString.toLowerCase();
        System.out.println(thisRule);
        System.out.println(otherRule);
        String[] thisRuleBits = thisRule.split("/");
        String[] otherRuleBits = otherRule.split("/");

        boolean surviveIsFirstBit = thisRuleBits[0].matches("s\\d*");
        if (surviveIsFirstBit) {
            thisRule = thisRuleBits[0] + thisRuleBits[1];
        } else {
            thisRule = thisRuleBits[1] + thisRuleBits[0];
        }

        surviveIsFirstBit = otherRuleBits[0].matches("s\\d*");
        if (surviveIsFirstBit) {
            otherRule = otherRuleBits[0] + otherRuleBits[1];
        } else {
            otherRule = otherRuleBits[1] + otherRuleBits[0];
        }

        return otherRule.equals(thisRule);
    }

    /**
     * Constructs the rule string for the rule set. "B3/S23" for Conway's.
     */
    private void initRuleString() {
        StringBuilder birthConditions = new StringBuilder();
        for (StateRange stateRange : birthStateRanges) {
            for (int i = stateRange.getMin(); i < stateRange.getMax() + 1; i++) {
                birthConditions.append(i);
            }
        }
        StringBuilder surviveConditions = new StringBuilder();
        for (StateRange stateRange : surviveStateRanges) {
            for (int i = stateRange.getMin(); i < stateRange.getMax() + 1; i++) {
                surviveConditions.append(i);
            }
        }

        this.ruleString = "B" + birthConditions.toString() + "/S" + surviveConditions.toString();
    }

    /**
     * Constructor accepting an array of ranges where an alive cell will survive on, as well as an array of ranges
     * where a dead cell will come to life.
     *
     * @param name Name of this rule set.
     * @param surviveStateRanges Ranges where an alive cell remains alive.
     * @param birthStateRanges Ranges where a dead cell comes to life.
     */
    public RuleSet(String name, StateRange[] surviveStateRanges, StateRange[] birthStateRanges) {
        this.name = name;
        this.surviveStateRanges = surviveStateRanges;
        this.birthStateRanges = birthStateRanges;
        aliveStates = new byte[]{1};
        deadStates = new byte[]{0};
        initRuleString();
    }

    /**
     * Constructor accepting an array of ranges where an alive cell will survive on, as well as an array of ranges
     * where a dead cell will come to life. On top of this, it accepts an array of states where a cell is considered
     * "alive", as well as an array of states where a cell is considered "dead".
     *
     * @param name Name of this rule set.
     * @param surviveStateRanges Ranges where an alive cell remains alive.
     * @param birthStateRanges Ranges where a dead cell comes to life.
     * @param aliveStates States where a cell is considered alive.
     * @param deadStates States where a cell is considered dead.
     */
    public RuleSet(String name, StateRange[] surviveStateRanges, StateRange[] birthStateRanges, byte[] aliveStates, byte[] deadStates) {
        this.name = name;
        this.surviveStateRanges = surviveStateRanges;
        this.birthStateRanges = birthStateRanges;
        this.aliveStates = aliveStates;
        this.deadStates = deadStates;
        initRuleString();
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
            for (StateRange stateRange : surviveStateRanges) {
                int min = stateRange.getMin();
                int max = stateRange.getMax();
                if (numNeighbors <= max && numNeighbors >= min) {
                    newState = 1;
                }
            }
        } else {
            for (StateRange stateRange : birthStateRanges) {
                int min = stateRange.getMin();
                int max = stateRange.getMax();
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

    /**
     * Getter for the rule set's rule string.
     *
     * @return Rule string representing the states for this rule set.
     */
    public String getRuleString() {
        return ruleString;
    }
}