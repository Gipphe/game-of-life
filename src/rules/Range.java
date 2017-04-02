package rules;

/**
 * Represents a range between two number, inclusive.
 */
public class Range {
    /**
     * Minimum inclusive number in the range.
     */
    private final int min;
    /**
     * Maximum inclusive number in the range.
     */
    private final int max;

    /**
     * Constructor accepting the minimum and maximum numbers in the range.
     *
     * @param min Minimum number in the range.
     * @param max Maximum number in the range.
     */
    public Range(int min, int max) {
        this.min = min;
        this.max = max;
    }
    public Range(int minMax) {
        this.min = minMax;
        this.max = minMax;
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