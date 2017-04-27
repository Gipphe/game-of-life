package RLE;

public class Parser {
    /**
     * Converts a passed Parser string to a byte[][] model for consumption by the Board class.
     * @param RLEString Source Parser string to convert to model.
     * @return Board representing the pattern described in the passed Parser string.
     */
    public static ParsedPattern toPattern(String RLEString) {
        FileContents contents = new FileContents(RLEString);

        int sizeX = contents.getNumCols();
        int sizeY = contents.getNumRows();
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
     * Finds out whether the passed row is made up entirely of dead cells or not.
     * @param row Row to check.
     * @return True if the row is empty, false otherwise.
     */
    private static boolean isEmptyRow(byte[] row) {
        for (byte col : row) {
            if (col == 1) return false;
        }
        return true;
    }

    /**
     * Finds the index of the last alive cell in the passed row.
     * @param row Row to find the last alive cell in.
     * @return Index of the last alive cell.
     */
    private static int lastAliveInRow(byte[] row) {
        int index = 0;
        for (int i = 0; i < row.length; i++) {
            if (row[i] == 1) index = i;
        }
        return index;
    }

    /**
     * Parses passed model into RLE for consumption by a file writer.
     * @param pattern Pattern to convert to an RLE string.
     * @return Parser string representing the model.
     */
    public static String fromPattern(byte[][] pattern) {
        StringBuilder result = new StringBuilder();
        StringBuilder line = new StringBuilder();
        line
                .append("x = ")
                .append(pattern[0].length)
                .append(", y = ")
                .append(pattern.length)
                .append(", rule = B3/S23");
        result
                .append(line.toString())
                .append("\n");
        line.setLength(0);

        // Counter for newline characters.
        int newLineCount = 1;

        int lastRowIndex = pattern.length - 1;
        for (int rowIndex = 0; rowIndex < pattern.length; rowIndex++) {
            byte[] row = pattern[rowIndex];
            // If the row is empty, increment newline character count.
            if (isEmptyRow(row)) {
                newLineCount++;
                continue;
            }
            // If this is not the first row, append newline character count (if applicable) and newline character ($).
            if (rowIndex != 0) {
                if (newLineCount > 1) {
                    line.append(newLineCount);
                    newLineCount = 1;
                }
                line.append("$");
            }

            // Counter for a specific state.
            int stateCount = 1;
            int indexOfLastState = row.length - 1;

            // Number of iterations for this row.
            int colsToIterate = row.length;
            // If it is the last row, only include up to and including the last alive cell, omitting the remaining
            // dead cells.
            if (rowIndex == lastRowIndex) {
                int lastAliveIndex = lastAliveInRow(row);
                colsToIterate = (lastAliveIndex == -1) ? row.length : lastAliveIndex + 1;
            }

            // Iterate through cells in the row.
            for (int i = 0; i < colsToIterate; i++) {
                byte state = row[i];
                String tag = (state == 1 ? "o" : "b");

                byte nextState = -1;
                if (i < indexOfLastState) {
                    nextState = row[i + 1];
                }

                // If this state is the same as the next state, iterate state counter.
                if (state == nextState) {
                    stateCount++;
                    continue;
                }
                StringBuilder runCountWithTagBuilder = new StringBuilder();
                if (stateCount > 1) {
                    runCountWithTagBuilder.append(stateCount);
                    stateCount = 1;
                }
                runCountWithTagBuilder.append(tag);

                String runCountWithTag = runCountWithTagBuilder.toString();
                if (line.length() + runCountWithTag.length() > 70) {
                    result.append(line.toString())
                            .append("\n");
                    line.setLength(0);
                }
                line.append(runCountWithTag);
            }
        }
        result.append(line.toString());
        result.append("!");

        return result.toString();
    }
}
