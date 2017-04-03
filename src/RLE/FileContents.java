package RLE;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FileContents {
    private int x;
    private int y;
    private String rule;
    private Stack<String> commands;

    /**
     * Getter for x.
     * @return The value of x.
     */
    int getX() {
        return x;
    }

    /**
     * Getter for y.
     * @return The value of y.
     */
    int getY() {
        return y;
    }

    /**
     * Getter for rule.
     * @return The full rule line.
     */
    String getRule() {
        return rule;
    }

    /**
     * Getter for commands.
     * @return The stack of commands, with their corresponding run count.
     */
    Stack<String> getCommands() {
        return commands;
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
    private static List<String> splitByNewline(String source) {
        return Arrays.asList(source.split("\\r?\\n"));
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
     * Removes the first line of the supplied string.
     * @param source String to remove first line of.
     * @return Source string, sans first line.
     */
    private static String removeFirstLine(String source) {
        int indexOfFirstNewLine = source.indexOf("\n");
        return source.substring(indexOfFirstNewLine);
    }

    /**
     * Split supplied string into a stack of commands, keeping the preceding number with the command itself.
     * @param source Source string to split into commands.
     * @return List of commands.
     */
    private static Stack<String> splitToCommands(String source) {
        Pattern pattern = Pattern.compile("\\d*\\w|\\$|!");
        Matcher matcher = pattern.matcher(source);
        Stack<String> result = new Stack<>();
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }

    /**
     * Constructor
     * @param contents full string contents of a potential Parser file.
     */
    FileContents(String contents) {
        String bareData = stripMeta(contents);

        String header = splitByNewline(bareData).get(0);
        this.x = getAxisSize(header, "x");
        this.y = getAxisSize(header, "y");
        this.rule = getRule(header);

        String justCommands = removeFirstLine(bareData);

        this.commands = splitToCommands(justCommands);
    }
}