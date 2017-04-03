package RLE;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    /**
     * Converts a passed Parser string to a byte[][] board for consumption by the Board class.
     * @param RLEString Source Parser string to convert to board.
     * @return Board representing the pattern described in the passed Parser string.
     */
    public static byte[][] toBoard(String RLEString) {
        FileContents contents = new FileContents(RLEString);

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
            Stack<String> commandBits = new Stack<>();
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
     * Parses passed board into Parser for consumption by a file writer.
     * @param board Board to convert to an Parser string.
     * @return Parser string representing the board.
     */
    public static String fromBoard(byte[][] board) {
        BoardContents rBoard = new BoardContents(board);

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
