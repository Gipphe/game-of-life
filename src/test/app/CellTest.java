package app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {
    @Test
    void state_gets_set_as_expected_on_passing_initial_state() {
        Cell result = new Cell(1);
        assertEquals("State: 1", result.toString());
    }
    @Test
    void state_gets_set_to_default_if_no_initial_state_is_passed() {
        Cell result = new Cell();
        assertEquals("State: 0", result.toString());
    }
}