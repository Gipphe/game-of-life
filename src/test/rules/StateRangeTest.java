package rules;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StateRangeTest {
    @Test
    void min_and_max_is_as_expected() {
        StateRange result = new StateRange(0, 1);
        assertEquals(0, result.getMin());
        assertEquals(1, result.getMax());
    }
}