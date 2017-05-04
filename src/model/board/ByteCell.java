package model.board;

import model.state.ByteState;
import model.state.State;

/**
 * A cell object representing a single point on the simulation model.
 * Benched at 350856KB on Turing Machine.
 */
public class ByteCell implements Cell {

    private State state;

    /**
     * Constructor.
     * Initializes as a dead cell.
     */
    public ByteCell() {
        this.state = new ByteState();
    }

    /**
     * Constructor.
     * Initializes as the passed cell's state.
     * @param cell The cell to mirror the state of.
     */
    public ByteCell(Cell cell) {
        this.state = new ByteState(cell.getState().isAlive());
    }

    /**
     * Constructor.
     * Initializes to the passed state.
     * @param state State to initialize to.
     */
    public ByteCell(boolean state) {
        this.state = new ByteState(state);
    }

    /**
     * Explicitly sets the state of this cell to 0.
     * @see Cell#kill()
     * @return Itself, for chaining.
     */
    @Override
    public ByteCell kill() {
        setState(false);
        return this;
    }

    /**
     * Explicitly sets the state of this cell to 1.
     * @see Cell#resurrect()
     * @return Itself, for chaining.
     */
    @Override
    public ByteCell resurrect() {
        setState(true);
        return this;
    }

    /**
     * @see Cell#getState()
     * @return The state object representing this cell's state.
     */
    @Override
    public State getState() {
        return state;
    }

    /**
     *
     * @param state The binary state to set this object's state to.
     */
    private void setState(boolean state) {
        this.state.setAlive(state);
    }

    /**
     *
     * @return A verbose string about the aliveness of this cell.
     */
    @Override
    public String toString() {
        return "cell is " + (getState().isAlive() ? "alive" : "dead");
    }
}
