package view;

/**
 * Interface for representing an enclosed area.
 */
public interface Boundary {
    /**
     * Setter for the top edge of the Boundary object.
     * @param top Top edge index value.
     */
    void setTop(int top);

    /**
     * Setter for the right edge of the Boundary object.
     * @param right Right edge index value.
     */
    void setRight(int right);

    /**
     * Setter for the bottom edge of the Boundary object.
     * @param bottom Bottom edge index value.
     */
    void setBottom(int bottom);

    /**
     * Setter for the left edge of the Boundary object.
     * @param left Left edge index value.
     */
    void setLeft(int left);

    /**
     *
     * @return Top edge index value.
     */
    int getTop();

    /**
     *
     * @return Right edge index value.
     */
    int getRight();

    /**
     *
     * @return Bottom edge index value.
     */
    int getBottom();

    /**
     *
     * @return Left edge index value.
     */
    int getLeft();

    /**
     *
     * @return Center row index value.
     */
    int getCenterRow();

    /**
     *
     * @return Center column index value.
     */
    int getCenterCol();
}
