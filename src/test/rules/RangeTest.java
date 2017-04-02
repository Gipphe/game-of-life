package test.rules;

import org.junit.jupiter.api.Test;
import rules.Range;

import static org.junit.jupiter.api.Assertions.*;

class RangeTest {
    @Test
    void min_and_max_is_as_expected() {
        Range result = new Range(0, 1);
        assertEquals(0, result.getMin());
        assertEquals(1, result.getMax());
    }
}