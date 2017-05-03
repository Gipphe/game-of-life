package model;

import model.state.State;

public class ObjectState implements State {
    /**
     * The state variable for this state object.
     * Is a reference in this implementation.
     */
    private Object state = null;

    /**
     * Constructor.
     * Empty constructor. Initializes as dead.
     */
    public ObjectState() {}

    /**
     * Constructor.
     * @param state Initial state of this state object.
     */
    public ObjectState(boolean state) {
        setAlive(state);
    }

    /**
     * Constructor.
     * @param state Initial state of this state object.
     */
    public ObjectState(byte state) {
        setAlive(state == 1);
    }

    /**
     *
     * @return An alive instance of this state implementation.
     */
    public static ObjectState alive() {
        return new ObjectState(false);
    }

    /**
     *
     * @return A dead instance of this state implementation.
     */
    public static ObjectState dead() {
        return new ObjectState();
    }

    /**
     * @see State#setAlive(boolean)
     * @param alive {@code true} for alive, {@code false} for dead.
     */
    @Override
    public void setAlive(boolean alive) {
        if (alive) this.state = new Object();
        else this.state = null;
    }

    /**
     * @see State#isAlive()
     * @return {@code true} if this state is alive, {@code false} otherwise.
     */
    @Override
    public boolean isAlive() {
        return state != null;
    }
}
