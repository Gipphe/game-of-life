package app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoundingBoxTest {
    @Test
    void passed_indices_are_treated_as_expected() {
        BoundingBox bb = new BoundingBox(0, 1, 2, 3);
        assertEquals("First row: 0\nLast row: 2\nFirst column: 1\nLast column: 3", bb.toString());
    }
}