package model.state;

/**
 * Implements the State interface as a byte value, where {@code 0} represents dead and {@code 1} represents alive.
 * Needs memory profiling on Turing Machine pattern.
 */
public class ByteState implements State {
    /**
     * The binary state of this state object. Is byte value 1 for alive, and any other byte value for dead.
     */
    private byte state = 0;

    /**
     * Constructor.
     * Initializes as dead.
     */
    public ByteState() {}

    /**
     * Constructor.
     * Initializes as passed state.
     * @param state Initializes as alive if {@code true}, initializes as dead otherwise.
     */
    public ByteState(boolean state) {
        if (state) this.state = 1;
    }

    /**
     * @return An alive cell.
     */
    public static ByteState getAlive() {
        return new ByteState(true);
    }

    /**
     * @return A dead cell.
     */
    public static ByteState getDead() {
        return new ByteState();
    }

    /**
     * @see State#isAlive()
     * @return {@code true} if this state is alive, {@code false} otherwise.
     */
    @Override
    public boolean isAlive() {
        return state == 1;
    }

    /**
     * @see State#setAlive(boolean)
     * @param alive {@code true} for alive, {@code false} for dead.
     */
    @Override
    public void setAlive(boolean alive) {
        state = alive ? (byte) 1 : 0;
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
