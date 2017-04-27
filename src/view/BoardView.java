package view;

/**
 * A boundary box representing a "camera view" of within a certain grid.
 */
public class BoardView implements Boundary {
    private int centerRow;
    private int centerCol;
    private int top;
    private int right;
    private int bottom;
    private int left;
    private int rowsToDisplay;
    private int colsToDisplay;

    /**
     * Constructor
     *
     * Initializes with top, right, bottom and left at 0.
     */
    public BoardView() {
        top = 0;
        right = 0;
        bottom = 0;
        left = 0;
        centerRow = 0;
        centerCol = 0;
    }

    private void setCenterRow() {
        centerRow = (bottom - top) / 2;
    }

    private void setCenterCol() {
        centerCol = (right - left) / 2;
    }

    /**
     *
     * @see Boundary#setTop(int)
     * @param top Top edge index value.
     */
    @Override
    public void setTop(int top) {
        this.top = top;
        setCenterRow();
    }

    /**
     *
     * @see Boundary#setLeft(int)
     * @param left Left edge index value.
     */
    @Override
    public void setLeft(int left) {
        this.left = left;
        setCenterCol();
    }

    /**
     *
     * @see Boundary#setBottom(int)
     * @param bottom Bottom edge index value.
     */
    @Override
    public void setBottom(int bottom) {
        this.bottom = bottom;
        setCenterRow();
    }

    /**
     *
     * @see Boundary#setRight(int)
     * @param right Right edge index value.
     */
    @Override
    public void setRight(int right) {
        this.right = right;
        setCenterCol();
    }

    /**
     *
     * @see Boundary#getTop()
     * @return Top edge index value.
     */
    @Override
    public int getTop() {
        return top;
    }

    /**
     *
     * @see Boundary#getRight()
     * @return Right edge index value.
     */
    @Override
    public int getRight() {
        return right;
    }

    /**
     *
     * @see Boundary#getBottom()
     * @return Bottom edge index value.
     */
    @Override
    public int getBottom() {
        return bottom;
    }

    /**
     *
     * @see Boundary#getLeft()
     * @return Left edge index value.
     */
    @Override
    public int getLeft() {
        return left;
    }

    /**
     *
     * @see Boundary#getCenterRow()
     * @return Center row index value.
     */
    @Override
    public int getCenterRow() {
        return centerRow;
    }

    /**
     *
     * @see Boundary#getCenterCol()
     * @return Center col index value.
     */
    @Override
    public int getCenterCol() {
        return centerCol;
    }

}
