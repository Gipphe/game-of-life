package model.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Deprecated
class BooleanStateTest {
    @Test
    void should_init_as_dead() {
        State s = new BooleanState();
        assertEquals(false, s.isAlive());
    }
    @Test
    void should_init_as_passed_value() {
        State s = new BooleanState(true);
        assertEquals(true, s.isAlive());
    }
    @Test
    void should_set_state() {
        State s = new BooleanState();
        assertEquals(false, s.isAlive());
        s.setAlive(true);
        assertEquals(true, s.isAlive());
    }
    @Test
    void should_supply_dead_cells() {
        State s = BooleanState.getDead();
        assertEquals(false, s.isAlive());
    }
    @Test
    void should_supply_alive_cells() {
        State s = BooleanState.getAlive();
        assertEquals(true, s.isAlive());
    }
    @Test
    void should_return_false_when_passing_non_state_to_equals() {
        State s = BooleanState.getAlive();
        Object foo = new Object();
        assertEquals(false, s.equals(foo));
    }
    @Test
    void should_return_false_when_aliveness_does_not_match() {
        State s = BooleanState.getAlive();
        State s2 = BooleanState.getDead();
        assertEquals(false, s.equals(s2));
    }
    @Test
    void should_return_true_when_aliveness_matches() {
        State s = BooleanState.getAlive();
        State s2 = BooleanState.getAlive();
        assertEquals(true, s.equals(s2));
    }
    @Test
    void should_return_verbose_string() {
        State s = BooleanState.getAlive();
        assertEquals("State: alive", s.toString());
        s = BooleanState.getDead();
        assertEquals("State: dead", s.toString());
    }

}