package RLE;

import java.util.Stack;

class PatternContents {
    private int y;
    private int x;

    private Stack<String> rows = new Stack<>();

    /**
     * Parses the passed model into a Stack of string lines.
     * @param board Board to parse into stack.
     */
    private void setPattern(byte[][] board) {
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
     * @param pattern pattern to be parsed as a PatternContents.
     */
    PatternContents(byte[][] pattern) {
        setPattern(pattern);
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
