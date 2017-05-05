package view;

/**
 * Represents a coordinate pair of a Y-coordinate and an X-coordinate on the board.
 */
public class BoardCoordinate {
    /**
     * The Y-coordinate of this coordinate pair.
     */
    private int y;

    /**
     * The X-coordinate of this coordinate pair.
     */
    private int x;

    /**
     * Constructor.
     * @param y The Y-coordinate of this pair.
     * @param x The X-coordinate of this pair.
     */
    public BoardCoordinate(int y, int x) {
        this.y = y;
        this.x = x;
    }

    /**
     * @return The Y-coordinate of this pair.
     */
    public int getY() {
        return y;
    }

    /**
     * @return The X-coordinate of this pair.
     */
    public int getX() {
        return x;
    }

    /**
     * @return A verbose {@code String} describing this coordinate pair.
     */
    @Override
    public String toString() {
        return "Coordinate at " + y + ", " + x;
    }

    /**
     * Compares this BoardCoordinate instance with the passed object, determining whether they represent the same point
     * on the board, or whether they are the very same object.
     * @param o The object to compare this BoardCoordinate to.
     * @return {@code true} if the passed object is the same object as this one, or if they represent the same
     * coordinate pair. Returns {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        BoardCoordinate bc = (BoardCoordinate) o;

        return !(o == null || getClass() != o.getClass()) &&
                y == bc.getY() && x == bc.getX();

    }

    /**
     * Returns a hash code for this coordinate pair. The hash code for a {@code BoardCoordinate} object is computed as
     * <blockquote><pre>
     * (31 * y) + x
     * </pre></blockquote>
     * using {@code int} arithmetic, where {@code y} is the Y-coordinate of this coordinate pair and {@code x} is the
     * X-coordinate of this coordinate pair.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        int result = y;
        result = 31 * result + x;
        return result;
    }
}
