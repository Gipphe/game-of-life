package model.state;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a binary state, 1 and 0.
 * Attempt to represent the internal state as a reference in and of itself. Memory usage in focus, objects that have no
 * references pointing to them should be garbage collected, reducing memory footprint of each cell individually.
 *
 * In short: empty objects represent 1, null reference represents 0. Computation time might suffer.
 */
@Deprecated
public class ReferenceState implements State {
    /**
     * The binary state of this object. Can be 1 or 0. Language limitations prevent us from using an actual bit value.
     * byte and boolean values are practically the same in memory usage. Actual usage depends on the virtual machine,
     * and varies.
     */
    private Object state = null;

    /**
     * Constructor. Initializes as passed value.
     * @param state The initial state of this binary state object.
     */
    public ReferenceState(boolean state) {
        setState(state);
    }

    /**
     * Constructor. Initializes as dead.
     */
    public ReferenceState() {}

    /**
     * @return An alive cell.
     */
    @NotNull
    public static ReferenceState getAlive() {
        return new ReferenceState(true);
    }

    /**
     * @return A dead cell.
     */
    @NotNull
    public static ReferenceState getDead() {
        return new ReferenceState();
    }

    /**
     *
     * @param state The binary state to set this object's state to.
     */
    public void setState(boolean state) {
        this.state = state ? new Object() : null;
    }

    /**
     * @see State#isAlive()
     * @return {@code true} if this state is alive, {@code false} otherwise.
     */
    @Override
    public boolean isAlive() {
        return state != null;
    }

    /**
     * Sets the aliveness of this state.
     * @see State#setAlive(boolean)
     * @param alive {@code true} for alive, {@code false} for dead.
     */
    @Override
    public void setAlive(boolean alive) {
        this.state = alive ? new Object() : null;
    }

    /**
     * Compares passed object with this state for aliveness.
     * @see State#equals(Object)
     * @param otherState Other object to compare against.
     * @return {@code true} if passed state has same aliveness as this state, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object otherState) {
        return otherState instanceof State && this.isAlive() == ((State) otherState).isAlive();
    }

    /**
     * @see State#toString()
     * @return A string representing all applicable information about this binary state object.
     */
    @Override
    public String toString() {
        return "State: " + (isAlive() ? "alive" : "dead");
    }
}
