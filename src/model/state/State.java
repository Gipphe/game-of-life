package model.state;

/**
 * Represents a binary life state, alive or dead.
 */
public interface State {
    /**
     * @return {@code true} if this state is alive, {@code false} otherwise.
     */
    boolean isAlive();

    /**
     * Sets the aliveness of this state.
     * @param alive {@code true} for alive, {@code false} for dead.
     */
    void setAlive(boolean alive);

    /**
     * Compares passed object with this state for aliveness.
     * @param otherState Other object to compare against.
     * @return {@code true} if passed state has same aliveness as this state, {@code false} otherwise.
     */
    @Override
    boolean equals(Object otherState);

    /**
     * @return A string representing all applicable information about this binary state object.
     */
    @Override
    String toString();
}
