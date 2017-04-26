package model;

/**
 * A cell object representing a single point on the simulation model.
 */
public class Cell {
    /**
     * The state of the cell. For Conway's rules, either 0 or 1.
     */
    private byte state;

    /**
     * Empty constructor initializing the cell with state 0 (dead by Conway's rules).
     */
    public Cell() {
        state = 0;
    }

    /**
     * Constructor accepting the initial state of the cell.
     * @param val Initial state of the cell.
     */
    public Cell(int val) {
        state = (byte) val;
    }

    /**
     * Getter for the current state of the cell.
     * @return The current state of the cell.
     */
    public byte getState() {
        return state;
    }

    /**
     * Setter for the current state of the cell.
     * @param newState New state of the cell.
     */
    public void setState(byte newState) {
        state = newState;
    }

    /**
     * Returns the state of the cell as a String.
     * @return A string representing the cell.
     */
    @Override
    public String toString() {
        return "State: " + state;
    }
}
