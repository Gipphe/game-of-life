package model.cell;

import model.state.ReferenceState;
import model.state.State;

/**
 * A cell object representing a single point on the simulation model.
 * Benched at 351385KB on Turing Machine.
 */
@Deprecated
public class ReferenceCell implements Cell {

    private State state;

    /**
     * Constructor.
     * Initializes as a dead cell.
     */
    public ReferenceCell() {
        this.state = new ReferenceState();
    }

    /**
     * Constructor.
     * Initializes to the passed state.
     * @param state State to initialize to.
     */
    public ReferenceCell(boolean state) {
        this.state = new ReferenceState(state);
    }

    /**
     * Explicitly sets the state of this cell to 0.
     * @see Cell#kill()
     * @return Itself, for chaining.
     */
    @Override
    public ReferenceCell kill() {
        setState(false);
        return this;
    }

    /**
     * Explicitly sets the state of this cell to 1.
     * @see Cell#resurrect()
     * @return Itself, for chaining.
     */
    @Override
    public ReferenceCell resurrect() {
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
