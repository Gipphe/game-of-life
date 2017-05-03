package model.state;

/**
 * Implements the State interface as a boolean value, where {@code false} represents dead and {@code true} represents
 * alive.
 * Needs memory profiling on Turing Machine pattern.
 */
@Deprecated
public class BooleanState implements State {
    /**
     * The binary state of this state object. Is boolean {@code true} for alive, and boolean {@code false} for dead.
     */
    private boolean state = false;

    /**
     * Constructor.
     * Initializes as dead.
     */
    public BooleanState() {}

    /**
     * Constructor.
     * Initializes as passed state.
     * @param state Initializes as alive if {@code true}, initializes as dead otherwise.
     */
    public BooleanState(boolean state) {
        this.state = state;
    }

    /**
     * @return A dead cell.
     */
    public static BooleanState getDead() {
        return new BooleanState();
    }

    /**
     * @return An alive cell.
     */
    public static BooleanState getAlive() {
        return new BooleanState(true);
    }

    /**
     * @see State#isAlive()
     * @return {@code true} if this state is alive, {@code false} otherwise.
     */
    @Override
    public boolean isAlive() {
        return state;
    }

    /**
     * @see State#setAlive(boolean)
     * @param alive {@code true} for alive, {@code false} for dead.
     */
    @Override
    public void setAlive(boolean alive) {
        this.state = alive;
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
