package RLE;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParsedPatternTest {
    @Test
    void should_accept_metadata_for_pattern() {
        byte[][] pattern = {{1}};
        ParsedPattern pp = new ParsedPattern(
                "Name",
                "Author",
                "Description",
                "Date",
                "B36/S23",
                pattern
        );
        assertEquals("Name", pp.getName());
        assertEquals("Author", pp.getAuthor());
        assertEquals("Description", pp.getDescription());
        assertEquals("Date", pp.getDate());
        assertEquals("B36/S23", pp.getRule());
        assertEquals(1, pp.getPattern().length);
        assertEquals(1, pp.getPattern()[0].length);
        assertEquals(1, pp.getPattern()[0][0]);
    }
    @Test
    void should_allow_simple_construction() {
        byte[][] pattern = {{0}};
        ParsedPattern pp = new ParsedPattern("B36/S23", pattern);
        assertEquals("", pp.getName());
        assertEquals("", pp.getAuthor());
        assertEquals("", pp.getDescription());
        assertEquals("", pp.getDate());
        assertEquals("B36/S23", pp.getRule());
        assertEquals(1, pp.getPattern().length);
        assertEquals(1, pp.getPattern()[0].length);
        assertEquals(0, pp.getPattern()[0][0]);
    }
    @Test
    void should_throw_if_rule_is_an_empty_string_on_simple_constructor() {
        byte[][] pattern = {{0}};
        boolean thrown = false;
        try {
            new ParsedPattern("", pattern);
        } catch (RLEException e) {
            assertEquals("Rule must be defined", e.getMessage());
            thrown = true;
        }
        assertTrue(thrown);
    }
    @Test
    void should_throw_if_rule_is_an_empty_string_on_rich_constructor() {
        byte[][] pattern = {{0}};
        boolean thrown = false;
        try {
            new ParsedPattern("", "", "", "", "", pattern);
        } catch (RLEException e) {
            assertEquals("Rule must be defined", e.getMessage());
            thrown = true;
        }
        assertTrue(thrown);
    }
}