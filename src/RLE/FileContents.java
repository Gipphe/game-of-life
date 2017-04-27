package RLE;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FileContents {
    /**
     * Number of columns in the pattern.
     */
    private int numCols;
    /**
     * Number of rows in the pattern.
     */
    private int numRows;
    private String rule;
    private Stack<String> lines;

    /**
     * Getter for numCols.
     * @return The value of numCols.
     */
    int getNumCols() {
        return numCols;
    }

    /**
     * Getter for numRows.
     * @return The value of numRows.
     */
    int getNumRows() {
        return numRows;
    }

    /**
     * Getter for rule.
     * @return The full rule line.
     */
    String getRule() {
        return rule;
    }

    /**
     * Getter for lines.
     * @return The stack of lines, with their corresponding run count.
     */
    Stack<String> getLines() {
        return lines;
    }

    /**
     * Finds the passed Parser string's size along the axis.
     * @param source Parser string to find size of.
     * @param axis Axis to get size of.
     * @return The size of the Parser pattern along the passed axis. Returns 0 if no such size was found.
     */
    private static int getAxisSize(String source, String axis) {
        String result = getFirstMatch(source, axis + " = (\\d+),");
        if (result == null) {
            return 0;
        }

        return Integer.parseInt(result);
    }

    /**
     * Returns the first occurrence in sourceString matching the passed regex.
     * @param sourceString String to search through.
     * @param regex Pattern to match against.
     * @return The first occurrence found. If no occurrence is found, returns empty string.
     */
    private static String getFirstMatch(String sourceString, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sourceString);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    /**
     * Splits a string into a List by newlines.
     * @param source String to split by newlines.
     * @return Source string, now split by newline characters into an iterable List.
     */
    @NotNull
    private static Stack<String> splitByNewline(String source) {
        String[] arr = source.split("\\r?\\n");
        Stack<String> result = new Stack<>();
        result.addAll(Arrays.asList(arr));
        return result;
    }

    /**
     * Returns the rule string.
     * @param rle Source string to find the rule of.
     * @return The rule line. Usually "rule = B3/S23", for Conway's rule.
     */
    private static String getRule(String rle) {
        return getFirstMatch(rle, "rule = (.+)");
    }

    /**
     * Strips metadata off of the supplied source string.
     * @param source String to strip of metadata.
     * @return New string without any lines beginning with #.
     */
    @NotNull
    private static String stripMeta(String source) {
        String[] lines = source.split("\\r?\\n");
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            if (line.matches(".*#.*")) {
                continue;
            }
            result.append(line);
            result.append("\n");
        }

        return result.toString();
    }

    /**
     * Constructor
     * @param contents full string contents of a potential Parser file.
     */
    FileContents(String contents) {
        String bareData = stripMeta(contents);

        Stack<String> lines = splitByNewline(bareData);
        String header = lines.get(0);
        this.numCols = getAxisSize(header, "x");
        this.numRows = getAxisSize(header, "y");
        this.rule = getRule(header);
        lines.remove(0);
        this.lines = lines;
    }
}