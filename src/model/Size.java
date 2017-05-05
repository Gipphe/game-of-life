package model;

public class Size {
    private final int cols;

    private final int rows;
    private final int deltaTop;
    private final int deltaRight;
    private final int deltaBottom;
    private final int deltaLeft;
    public Size(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        deltaTop = 0;
        deltaRight = 0;
        deltaBottom = 0;
        deltaLeft = 0;
    }

    public Size(int rows, int cols, int deltaTop, int deltaRight, int deltaBottom, int deltaLeft) {
        this.rows = rows;
        this.cols = cols;
        this.deltaTop = deltaTop;
        this.deltaRight = deltaRight;
        this.deltaBottom = deltaBottom;
        this.deltaLeft = deltaLeft;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public int getDeltaTop() {
        return deltaTop;
    }

    public int getDeltaRight() {
        return deltaRight;
    }

    public int getDeltaBottom() {
        return deltaBottom;
    }

    public int getDeltaLeft() {
        return deltaLeft;
    }
}
