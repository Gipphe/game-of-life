package rules;

/**
 * Represents a range between two number, inclusive.
 */
public class StateRange {
    /**
     * Minimum inclusive number in the range.
     */
    private int min;
    /**
     * Maximum inclusive number in the range.
     */
    private int max;

    private void initStateRange(int min, int max) {
        if (min < 0) {
            throw new StateRangeException("Minimum value is less than 0: " + min);
        }
        if (max > 8) {
            throw new StateRangeException("Maximum value is more than 8: " + max);
        }
        this.min = min;
        this.max = max;
    }

    /**
     * Constructor accepting the minimum and maximum numbers in the range.
     *
     * @param min Minimum number in the range.
     * @param max Maximum number in the range.
     */
    public StateRange(int min, int max) {
        initStateRange(min, max);
    }
    public StateRange(int minMax) {
        initStateRange(minMax, minMax);
    }

    /**
     * Getter for the minimum number in the range.
     *
     * @return The minimum number in the range.
     */
    public int getMin() {
        return min;
    }

    /**
     * Getter for the maximum number in the range.
     *
     * @return The maximum number in the range.
     */
    public int getMax() {
        return max;
    }
}