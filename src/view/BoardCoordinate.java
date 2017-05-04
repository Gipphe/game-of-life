package view;

import java.util.Objects;

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
     *
     * @return The Y-coordinate of this pair.
     */
    public int getY() {
        return y;
    }

    /**
     *
     * @return The X-coordinate of this pair.
     */
    public int getX() {
        return x;
    }

    @Override
    public String toString() {
        return "Coordinate at " + y + ", " + x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        return y == ((BoardCoordinate) o).getY() && x == ((BoardCoordinate) o).getX();
    }

    @Override
    public int hashCode() {
        int result = y;
        result = 31 * result + x;
        return result;
    }
}
