package utils;

public final class Utils {
    /**
     * Hog the constructor to prevent instantiation.
     */
    private Utils() {}

    /**
     * Limits the value to the passed max and min values.
     * @see Utils#limit(double, double, double) 
     * @param max Maximum value, inclusive, for {@code value}.
     * @param min Minimum value, inclusive, for {@code value}.
     * @param value Value to limit.
     * @return The value if it is within the boundaries, or the boundary it exceeds itself.
     */
    public static int limit(int max, int min, int value) {
        return (int) Math.round(limit((double) max, (double) min, (double) value));
    }

    /**
     * Limits the value to the passed max and min values.
     * @param max Maximum value, inclusive, for {@code value}.
     * @param min Minimum value, inclusive, for {@code value}.
     * @param value Value to limit.
     * @return The value if it is within the boundaries, or the boundary it exceeds itself.
     */
    public static double limit(double max, double min, double value) {
        if (max < min) throw new RuntimeException("Max is less than min");
        return Math.max(Math.min(value, max), min);
    }

    /**
     * Returns a corresponding value within {@code [min, max]} for {@code val}, wrapping it around the boundaries.
     *
     * @param max Upper wrapping point for val.
     * @param min Lower wrapping point for val.
     * @param val Value to wrap.
     * @return Value within [0, lim].
     */
    public static int wrap(int max, int min, int val) {
        if (max < min) throw new RuntimeException("Max is less than min");

        if (val >= max) {
            return val - max;
        } else if (val < min) {
            return val + max;
        }
        return val;
    }
}
