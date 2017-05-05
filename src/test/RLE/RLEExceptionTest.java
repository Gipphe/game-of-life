package RLE;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RLEExceptionTest {
    @Test
    void blank_constructor() {
        assertEquals(null, new RLEException().getMessage());
    }
    @Test
    void descriptive_constructor() {
        assertEquals("foo", new RLEException("foo").getMessage());
    }
}