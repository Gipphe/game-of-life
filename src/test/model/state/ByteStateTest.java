package model.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ByteStateTest {
    @Test
    void should_init_as_dead() {
        State s = new ByteState();
        assertEquals(false, s.isAlive());
    }
    @Test
    void should_init_as_passed_value() {
        State s = new ByteState(true);
        assertEquals(true, s.isAlive());
    }
    @Test
    void should_set_state() {
        State s = new ByteState();
        assertEquals(false, s.isAlive());
        s.setAlive(true);
        assertEquals(true, s.isAlive());
    }
    @Test
    void should_supply_dead_cells() {
        State s = ByteState.getDead();
        assertEquals(false, s.isAlive());
    }
    @Test
    void should_supply_alive_cells() {
        State s = ByteState.getAlive();
        assertEquals(true, s.isAlive());
    }
    @Test
    void should_return_false_when_passing_non_state_to_equals() {
        State s = ByteState.getAlive();
        Object foo = new Object();
        assertEquals(false, s.equals(foo));
    }
    @Test
    void should_return_false_when_aliveness_does_not_match() {
        State s = ByteState.getAlive();
        State s2 = ByteState.getDead();
        assertEquals(false, s.equals(s2));
    }
    @Test
    void should_return_true_when_aliveness_matches() {
        State s = ByteState.getAlive();
        State s2 = ByteState.getAlive();
        assertEquals(true, s.equals(s2));
    }
    @Test
    void should_return_verbose_string() {
        State s = ByteState.getAlive();
        assertEquals("State: alive", s.toString());
        s = ByteState.getDead();
        assertEquals("State: dead", s.toString());
    }

}