package rules;

import model.state.ByteState;
import model.state.State;

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
    private final State aliveState = new ByteState();
    /**
     * An array of states where a cell is considered "dead".
     * Conway's and HighLife only has one state, "0".
     */
    private final State deadState = new ByteState(true);
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
        initRuleString();
    }

    /**
     * Evaluates the new state of a given cell in the coming generation based on the cell's current state as well as
     * the contained ranges.
     *
     * @param currentState Current state of the cell before the coming generation.
     * @param numNeighbors Number of neighbors the cell currently has.
     * @return The new state of the cell.
     */
    public State getNewState(State currentState, int numNeighbors) {
        // Initialize new state at first dead state, not taking into account rule sets where a cell can have more than
        // two states.
        State newState = new ByteState();
        if (currentState.isAlive()) {
            for (StateRange stateRange : surviveStateRanges) {
                int min = stateRange.getMin();
                int max = stateRange.getMax();
                if (numNeighbors <= max && numNeighbors >= min) {
                    newState.setAlive(true);
                }
            }
        } else {
            for (StateRange stateRange : birthStateRanges) {
                int min = stateRange.getMin();
                int max = stateRange.getMax();
                if (numNeighbors <= max && numNeighbors >= min) {
                    newState.setAlive(true);
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