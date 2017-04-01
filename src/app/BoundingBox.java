package app;


/**
 * A bounding box indicating an area of interest on a board.
 */
public class BoundingBox {
    /**
     * First row of the BoundingBox.
     */
    private int firstRow;
    /**
     * First column of the BoundingBox.
     */
    private int firstCol;

    /**
     * Last row of the BoundingBox.
     */
    private int lastRow;

    /**
     * Last column of the BoundingBox.
     */
    private int lastCol;

    /**
     * Getter for the first row.
     *
     * @return The first row.
     */
    int getFirstRow() {
        return firstRow;
    }

    /**
     * Getter for the first column.
     *
     * @return The first column.
     */
    int getFirstCol() {
        return firstCol;
    }

    /**
     * Getter for the last row.
     *
     * @return The last row.
     */
    int getLastRow() {
        return lastRow;
    }

    /**
     * Getter for the last column.
     *
     * @return The last column.
     */
    int getLastCol() {
        return lastCol;
    }

    /**
     * Setter for the first row.
     *
     * @param firstRow The new first row.
     */
    void setFirstRow(int firstRow) {
        this.firstRow = firstRow;
    }

    /**
     * Setter for the first column.
     *
     * @param firstCol The new first column.
     */
    void setFirstCol(int firstCol) {
        this.firstCol = firstCol;
    }

    /**
     * Setter for the last row.
     *
     * @param lastRow The new last row.
     */
    void setLastRow(int lastRow) {
        this.lastRow = lastRow;
    }

    /**
     * Setter for the last column.
     *
     * @param lastCol The new last column.
     */
    void setLastCol(int lastCol) {
        this.lastCol = lastCol;
    }

    /**
     * Constructor accepting all 4 coordinates of the bounding box to be made.
     * @param firstRow First row.
     * @param firstCol First column.
     * @param lastRow Last row.
     * @param lastCol Last column.
     */
    public BoundingBox(int firstRow, int firstCol, int lastRow, int lastCol) {
        this.firstRow = firstRow;
        this.firstCol = firstCol;
        this.lastRow = lastRow;
        this.lastCol = lastCol;
    }

    /**
     * Returns a string representing the BoundingBox.
     * @return A descriptive string of the BoundingBox.
     */
    @Override
    public String toString() {
        return "First row: " + firstRow + "\nLast row: " + lastRow + "\nFirst column: " + firstCol + "\nLast column: " + lastCol;
    }
}