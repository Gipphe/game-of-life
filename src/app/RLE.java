package app;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ReadableBoard {
    private int y;
    private int x;

    private Stack<String> rows = new Stack<String>();

    /**
     * Parses the passed board into a Stack of string lines.
     * @param board Board to parse into stack.
     */
    private void setBoard(byte[][] board) {
        this.y = board.length;
        this.x = board[0].length;

        StringBuilder sb = new StringBuilder();
        for (byte[] row : board) {
            for (byte cell : row) {
                sb.append(cell);
            }
            rows.add(sb.toString());
            sb.setLength(0);
        }
    }

    /**
     * Constructor
     * @param board board to be parsed as a ReadableBoard.
     */
    ReadableBoard(byte[][] board) {
        setBoard(board);
    }

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
     * Getter for rows.
     * @return The stack of String lines.
     */
    Stack<String> getRows() {
        return rows;
    }
}

class RLEContents {
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
     * Finds the passed RLE string's size along the axis.
     * @param source RLE string to find size of.
     * @param axis Axis to get size of.
     * @return The size of the RLE pattern along the passed axis. Returns 0 if no such size was found.
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
        Stack<String> result = new Stack<String>();
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }

    /**
     * Constructor
     * @param contents full string contents of a potential RLE file.
     */
    RLEContents(String contents) {
        String bareData = stripMeta(contents);

        String header = splitByNewline(bareData).get(0);
        this.x = getAxisSize(header, "x");
        this.y = getAxisSize(header, "y");
        this.rule = getRule(header);

        String justCommands = removeFirstLine(bareData);

        this.commands = splitToCommands(justCommands);
    }
}
public class RLE {
    /**
     * Converts a passed RLE string to a byte[][] board for consumption by the Board class.
     * @param RLEString Source RLE string to convert to board.
     * @return Board representing the pattern described in the passed RLE string.
     */
    public static byte[][] toBoard(String RLEString) {
        RLEContents contents = new RLEContents(RLEString);

        int sizeX = contents.getX();
        int sizeY = contents.getY();
        String rule = contents.getRule();
        if (!rule.equals("B3/S23")) {
            throw new RuntimeException("Incorrect rule");
        }

        byte[][] board = new byte[sizeY][sizeX];

        int pX = 0;
        int pY = 0;

        int count = 1;

        for (String command : contents.getCommands()) {
            Stack<String> commandBits = new Stack<String>();
            commandBits.addAll(Arrays.asList(command.split("")));

            String state = commandBits.pop();

            if (state.equals("!")) {
                break;
            }
            if (state.equals("$")) {
                pY++;
                pX = 0;
                continue;
            }

            if (commandBits.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (String number : commandBits) {
                    sb.append(number);
                }
                count = Integer.parseInt(sb.toString());
            }

            for (int i = 0; i < count; i++) {
                try {
                    byte newState = state.equals("o") ? (byte) 1 : (byte) 0;
                    board[pY][pX] = newState;
                    pX++;
                } catch (ArrayIndexOutOfBoundsException e) {
                    pX = 0;
                    pY++;
                    break;
                }
            }
            count = 1;
        }
        return board;
    }

    /**
     * Counts number of characters in string, and reduces them to a number, representing the number of occurrences of
     * that string in the source string, and a single instance of the character in question.
     *
     * @param source Source string to reduce to number + character.
     * @return Reduced string with the number of occurrences of the character in the source string.
     */
    private static String concentrate(String source) {
        int length = source.length();
        String lengthString = length == 1 ? "" : "" + length;
        return lengthString + source.charAt(0);
    }

    /**
     * Finds out whether the passed row is made up entirely of dead cells or not.
     * @param row Row to check.
     * @return True if the row is empty, false otherwise.
     */
    private static boolean isEmptyRow(String row) {
        Pattern pattern = Pattern.compile("[^0,\\]\\[ ]");
        Matcher match = pattern.matcher(row);
        boolean isEmpty = true;
        if (match.find()) {
            isEmpty = false;
        }
        return isEmpty;
    }

    /**
     * Parses passed board into RLE for consumption by a file writer.
     * @param board Board to convert to an RLE string.
     * @return RLE string representing the board.
     */
    public static String fromBoard(byte[][] board) {
        ReadableBoard rBoard = new ReadableBoard(board);

        StringBuilder result = new StringBuilder();
        result
                .append("x = ")
                .append(rBoard.getX())
                .append(", y = ")
                .append(rBoard.getY())
                .append(", rule = B3/S23")
                .append("\n");

        StringBuilder newlineCharacters = new StringBuilder();
        Stack<String> rows = rBoard.getRows();
        int lastRowIndex = rows.size() - 1;
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            String row = rows.get(rowIndex);
            if (isEmptyRow(row)) {
                newlineCharacters.append("$");
                continue;
            }
            if (rowIndex != 0) {
                newlineCharacters.append("$");
                result.append(concentrate(newlineCharacters.toString()));
                newlineCharacters.setLength(0);
            }
            String[] statesInRow = row.split("");

            StringBuilder stateCharacters = new StringBuilder();
            int indexOfLastState = statesInRow.length - 1;

            int colsToRun = statesInRow.length;
            if (rowIndex == lastRowIndex) {
                int lastAlive = row.lastIndexOf('1');
                colsToRun = (lastAlive == -1) ? statesInRow.length : lastAlive + 1;
            }

            for (int i = 0; i < colsToRun; i++) {
                String state = statesInRow[i];
                String value = (state.equals("1") ? "o" : "b");

                String nextState = "";
                if (i < indexOfLastState) {
                    nextState = statesInRow[i + 1];
                }

                if (state.equals(nextState)) {
                    stateCharacters.append(value);
                    continue;
                }
                stateCharacters.append(value);
                result.append(concentrate(stateCharacters.toString()));
                stateCharacters.setLength(0);
            }
        }
        result.append("!");

        return result.toString();
    }
}
