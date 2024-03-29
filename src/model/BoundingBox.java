package model;


/**
 * A bounding box indicating an area of interest on a model.
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
    public int getFirstRow() {
        return firstRow;
    }

    /**
     * Getter for the first column.
     *
     * @return The first column.
     */
    public int getFirstCol() {
        return firstCol;
    }

    /**
     * Getter for the last row.
     *
     * @return The last row.
     */
    public int getLastRow() {
        return lastRow;
    }

    /**
     * Getter for the last column.
     *
     * @return The last column.
     */
    public int getLastCol() {
        return lastCol;
    }

    /**
     * Setter for the first row.
     *
     * @param firstRow The new first row.
     */
    public void setFirstRow(int firstRow) {
        this.firstRow = firstRow;
    }

    /**
     * Setter for the first column.
     *
     * @param firstCol The new first column.
     */
    public void setFirstCol(int firstCol) {
        this.firstCol = firstCol;
    }

    /**
     * Setter for the last row.
     *
     * @param lastRow The new last row.
     */
    public void setLastRow(int lastRow) {
        this.lastRow = lastRow;
    }

    /**
     * Setter for the last column.
     *
     * @param lastCol The new last column.
     */
    public void setLastCol(int lastCol) {
        this.lastCol = lastCol;
    }

    /**
     * Getter for the X size of the Bounding Box.
     *
     * @return The X size of the Bounding Box.
     */
    public int getSizeX() {
        int sizeX = (getLastCol() - getFirstCol()) + 1;
        return sizeX;
    }

    /**
     * Getter for the Y size of the Bounding Box.
     *
     * @return The Y size of the Bounding Box.
     */
    public int getSizeY() {
        int sizeY = (getLastRow() - getFirstRow()) + 1;
        return sizeY;
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