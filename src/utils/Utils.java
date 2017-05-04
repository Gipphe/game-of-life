package utils;

public class Utils {
    public static int limit(int max, int min, int value) {
        return (int) Math.round(limit((double) max, (double) min, (double) value));
    }
    public static double limit(double max, double min, double value) {
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
        if (val >= max) {
            return val - max;
        } else if (val < min) {
            return val + max;
        }
        return val;
    }
}
