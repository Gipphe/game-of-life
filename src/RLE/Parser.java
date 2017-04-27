package RLE;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    /**
     * Converts a passed Parser string to a byte[][] model for consumption by the Board class.
     * @param RLEString Source Parser string to convert to model.
     * @return Board representing the pattern described in the passed Parser string.
     */
    public static ParsedPattern toPattern(String RLEString) {
        FileContents contents = new FileContents(RLEString);

        int sizeX = contents.getX();
        int sizeY = contents.getY();
        String rule = contents.getRule();

        byte[][] pattern = new byte[sizeY][sizeX];

        int pX = 0;
        int pY = 0;

        StringBuilder digitAccumulator = new StringBuilder();
        for (String line : contents.getLines()) {
            for (char ch : line.toCharArray()) {
                if (ch == '!') {
                    break;
                }

                if (Character.isDigit(ch)) {
                    digitAccumulator.append(ch);
                    continue;
                }

                if (ch == '$') {
                    int count;
                    try {
                        count = Integer.parseInt(digitAccumulator.toString());
                    } catch (NumberFormatException e) {
                        count = 1;
                    }
                    digitAccumulator.setLength(0);

                    pY += count;
                    pX = 0;
                    continue;
                }

                int count;
                try {
                    count = Integer.parseInt(digitAccumulator.toString());
                } catch (NumberFormatException e) {
                    count = 1;
                }
                digitAccumulator.setLength(0);

                byte newState = ch == 'o' ? (byte) 1 : (byte) 0;

                for (int i = 0; i < count; i++) {
                    pattern[pY][pX] = newState;
                    pX++;
                }
            }
        }

        return new ParsedPattern(rule, pattern);
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
     * Parses passed model into Parser for consumption by a file writer.
     * @param board Board to convert to an Parser string.
     * @return Parser string representing the model.
     */
    public static String fromPattern(byte[][] board) {
        PatternContents rBoard = new PatternContents(board);

        StringBuilder result = new StringBuilder();
        StringBuilder line = new StringBuilder();
        line
                .append("x = ")
                .append(rBoard.getX())
                .append(", y = ")
                .append(rBoard.getY())
                .append(", rule = B3/S23");
        result
                .append(line.toString())
                .append("\n");
        line.setLength(0);

        // Accumulator for counting newline characters.
        StringBuilder newlineCharacters = new StringBuilder();

        Stack<String> rows = rBoard.getRows();
        int lastRowIndex = rows.size() - 1;
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            String row = rows.get(rowIndex);
            // If the row is empty, increment newline character count
            if (isEmptyRow(row)) {
                newlineCharacters.append("$");
                continue;
            }
            // If this is not the first row, append newline characters
            if (rowIndex != 0) {
                newlineCharacters.append("$");
                line.append(concentrate(newlineCharacters.toString()));
                newlineCharacters.setLength(0);
            }
            String[] statesInRow = row.split("");

            StringBuilder stateCharacters = new StringBuilder();
            int indexOfLastState = statesInRow.length - 1;

            int colsToRun = statesInRow.length;
            // If this is the last row, only include up to and including the last alive cell, omitting the last dead cells
            if (rowIndex == lastRowIndex) {
                int lastAlive = row.lastIndexOf('1');
                colsToRun = (lastAlive == -1) ? statesInRow.length : lastAlive + 1;
            }

            // Iterate through cells in the row
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
                String element = concentrate(stateCharacters.toString());
                if (line.length() + element.length() > 70) {
                    result.append(line.toString())
                            .append("\n");
                    line.setLength(0);
                }
                line.append(element);
                stateCharacters.setLength(0);
            }
        }
        result.append(line.toString());
        result.append("!");

        return result.toString();
    }
}
