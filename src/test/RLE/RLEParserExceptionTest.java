package RLE;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RLEParserExceptionTest {
    @Test
    void blank_constructor() {
        assertEquals(null, new RLEParserException().getMessage());
    }
    @Test
    void descriptive_constructor() {
        assertEquals("foo", new RLEParserException("foo").getMessage());
    }
}