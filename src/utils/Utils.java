package utils;

public class Utils {
    public static int limit(int max, int min, int value) {
        return (int) Math.round(limit((double) max, (double) min, (double) value));
    }
    public static double limit(double max, double min, double value) {
        return Math.max(Math.min(value, max), min);
    }
}
