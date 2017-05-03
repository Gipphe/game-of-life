package model.cell;

import model.state.State;

/**
 * Represents a living object that can have states alive and dead.
 */
public interface Cell {
    /**
     * Explicitly sets the state of this cell object to dead.
     * @return Itself, for chaining.
     */
    Cell kill();

    /**
     * Explicitly sets the state of this cell object to alive.
     * @return Itself, for chaining.
     */
    Cell resurrect();

    /**
     * @return The state object holding the state information for this cell.
     */
    State getState();
}
