package RLE;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileContentsTest {
    @Test
    void should_default_rule_to_conway() {
        String base = "x = 1, y = 1\no!";
        FileContents fc = new FileContents(base);
        assertEquals("B3/S23", fc.getRule());
    }
    @Test
    void should_fetch_rule_from_input() {
        String base = "x = 1, y = 1, rule = B36/S23\no!";
        FileContents fc = new FileContents(base);
        assertEquals("B36/S23", fc.getRule());
    }
    @Test
    void should_split_pattern_information_by_lines() {
        String base = "x = 1, y = 3\no$\nb$\no!";
        FileContents fc = new FileContents(base);
        assertEquals(3, fc.getLines().size());
        assertEquals("o$", fc.getLines().get(0));
        assertEquals("b$", fc.getLines().get(1));
        assertEquals("o!", fc.getLines().get(2));
    }
    @Test
    void should_get_number_of_rows() {
        String base = "x = 1, y = 2\no$o!";
        FileContents fc = new FileContents(base);
        assertEquals(2, fc.getNumRows());
    }
    @Test
    void should_get_number_of_columns() {
        String base = "x = 2, y = 1\n2o!";
        FileContents fc = new FileContents(base);
        assertEquals(2, fc.getNumCols());
    }
    @Test
    void should_strip_metadata() {
        String base = "#C foo\nx = 2, y = 1, rule = B36/S23\n2o!";
        FileContents fc = new FileContents(base);
        assertEquals(2, fc.getNumCols());
        assertEquals(1, fc.getNumRows());
        assertEquals("B36/S23", fc.getRule());
        assertEquals(1, fc.getLines().size());
        assertEquals("2o!", fc.getLines().get(0));
    }
    @Test
    void should_throw_if_axis_is_not_defined() {
        String base = "x = 2, rule = B3/S23\n2o!";
        boolean thrown = false;
        try {
            new FileContents(base);
        } catch(RLEException e) {
            thrown = true;
            assertEquals("Missing axis information: y", e.getMessage());
        }
        assertTrue(thrown);
    }
}