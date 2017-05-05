package RLE;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    private String boardToString(byte[][] board) {
        StringBuilder sb = new StringBuilder();
        for (byte[] row : board) {
            for (byte cell : row) {
                sb.append(cell);
            }
        }
        return sb.toString();
    }

    @Test
    void converts_glider_from_rle_to_proper_board() {
        String base =
                "#N Glider\n" +
                        "#O Richard K. Guy\n" +
                        "#C The smallest, most common, and first discovered spaceship. Diagonal, has period 4 and speed c/4.\n" +
                        "#C www.conwaylife.com/wiki/index.php?title=Glider\n" +
                        "x = 3, y = 3, rule = B3/S23\n"+
                        "bob$2bo$3o!";
        byte[][] result = Parser.toPattern(base).getPattern();

        byte[][] expected = {
            {0,1,0},
            {0,0,1},
            {1,1,1}
        };

        assertEquals(boardToString(expected), boardToString(result));
    }

    @Test
    void converts_block_from_RLE_to_board() {
        String base = "x = 2, y = 2, rule = B3/S23\n2o$2o!";

        byte[][] result = Parser.toPattern(base).getPattern();

        byte[][] expected = {
                {1,1},
                {1,1}
        };

        assertEquals(boardToString(expected), boardToString(result));
    }

    @Test
    void handles_lower_case_and_upper_case_rules() {
        String base = "x = 1, y = 1, rule = b3/s23\no!";
        byte[][] result = Parser.toPattern(base).getPattern();
        byte[][] expected = {{1}};
        assertEquals(boardToString(expected), boardToString(result));

        base = "x = 1, y = 1, rule = B3/s23\no!";
        result = Parser.toPattern(base).getPattern();
        expected = new byte[][]{{1}};
        assertEquals(boardToString(expected), boardToString(result));

        base = "x = 1, y = 1, rule = B3/S23\no!";
        result = Parser.toPattern(base).getPattern();
        expected = new byte[][]{{1}};
        assertEquals(boardToString(expected), boardToString(result));
    }

    @Test
    void converts_from_RLE_even_with_double_digit_length_encoding() {
        String base = "x = 18, y = 1, rule = B3/S23\n18o!";
        byte[][] result = Parser.toPattern(base).getPattern();

        byte[][] expected = {
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        };

        assertEquals(boardToString(expected), boardToString(result));
    }

    @Test
    void converts_pulsar_from_RLE_to_board() {
        String base = "x = 13, y = 13, rule = B3/S23\n" +
                "2b3o3b3o2b2$o4bobo4bo$o4bobo4bo$o4bobo4bo$2b3o3b3o2b2$2b3o3b3o2b$o4bobo4bo$o4bobo4bo$o4bobo4bo2$2b3o3b3o!";

        byte[][] result = Parser.toPattern(base).getPattern();

        byte[][] expected = {
                {0,0,1,1,1,0,0,0,1,1,1,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0},
                {1,0,0,0,0,1,0,1,0,0,0,0,1},
                {1,0,0,0,0,1,0,1,0,0,0,0,1},
                {1,0,0,0,0,1,0,1,0,0,0,0,1},
                {0,0,1,1,1,0,0,0,1,1,1,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,1,1,1,0,0,0,1,1,1,0,0},
                {1,0,0,0,0,1,0,1,0,0,0,0,1},
                {1,0,0,0,0,1,0,1,0,0,0,0,1},
                {1,0,0,0,0,1,0,1,0,0,0,0,1},
                {0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,1,1,1,0,0,0,1,1,1,0,0},
        };

        assertEquals(boardToString(expected), boardToString(result));
    }

    @Test
    void converts_box_from_board_to_RLE() {
        byte[][] base = {
                {1,1},
                {1,1}
        };
        String expected = "x = 2, y = 2, rule = B3/S23\n2o$2o!";

        ParsedPattern pp = new ParsedPattern("B3/S23", base);
        String result = Parser.fromPattern(pp);

        assertEquals(expected, result);
    }

    @Test
    void converts_glider_from_board_to_RLE() {
        byte[][] base = {
                {0,1,0},
                {1,0,0},
                {1,1,1}
        };
        String expected = "x = 3, y = 3, rule = B3/S23\nbob$o2b$3o!";

        ParsedPattern pp = new ParsedPattern("B3/S23", base);
        String result = Parser.fromPattern(pp);

        assertEquals(expected, result);
    }

    @Test
    void converts_pulsar_from_board_to_RLE() {
        byte[][] base = {
                {0,0,1,1,1,0,0,0,1,1,1,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0},
                {1,0,0,0,0,1,0,1,0,0,0,0,1},
                {1,0,0,0,0,1,0,1,0,0,0,0,1},
                {1,0,0,0,0,1,0,1,0,0,0,0,1},
                {0,0,1,1,1,0,0,0,1,1,1,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,1,1,1,0,0,0,1,1,1,0,0},
                {1,0,0,0,0,1,0,1,0,0,0,0,1},
                {1,0,0,0,0,1,0,1,0,0,0,0,1},
                {1,0,0,0,0,1,0,1,0,0,0,0,1},
                {0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,1,1,1,0,0,0,1,1,1,0,0},
        };

        String expected = "x = 13, y = 13, rule = B3/S23\n" +
                "2b3o3b3o2b2$o4bobo4bo$o4bobo4bo$o4bobo4bo$2b3o3b3o2b2$2b3o3b3o2b$o4bob\n" +
                "o4bo$o4bobo4bo$o4bobo4bo2$2b3o3b3o!";

        ParsedPattern pp = new ParsedPattern("B3/S23", base);
        String result = Parser.fromPattern(pp);

        assertEquals(expected, result);
    }

    @Test
    void converts_from_pattern_even_when_there_are_double_digits_length_encoding() {
        byte[][] base = {
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        };

        String expected = "x = 18, y = 1, rule = B3/S23\n" +
                "18o!";
        ParsedPattern pp = new ParsedPattern("B3/S23", base);
        String result = Parser.fromPattern(pp);

        assertEquals(expected, result);
    }

    @Test
    void includes_name_when_converting_from_pattern() {
        byte[][] base = {{1}};

        String expected  = "#N Name\nx = 1, y = 1, rule = B3/S23\no!";
        ParsedPattern pp = new ParsedPattern("Name", "", "", "", "B3/S23", base);
        String result = Parser.fromPattern(pp);

        assertEquals(expected, result);
    }

    @Test
    void includes_author_when_converting_from_pattern() {
        byte[][] base = {{1}};
        String expected = "#O Author\nx = 1, y = 1, rule = B3/S23\no!";
        ParsedPattern pp = new ParsedPattern("", "Author", "", "", "B3/S23", base);
        String result = Parser.fromPattern(pp);

        assertEquals(expected, result);
    }

    @Test
    void inclues_date_when_converting_from_pattern() {
        byte[][] base = {{1}};
        String expected = "#O Date\nx = 1, y = 1, rule = B3/S23\no!";
        ParsedPattern pp = new ParsedPattern("", "", "", "Date", "B3/S23", base);
        String result = Parser.fromPattern(pp);

        assertEquals(expected, result);
    }
    @Test
    void includes_author_and_date_when_converting_from_pattern() {
        byte[][] base = {{1}};
        String expected = "#O Author, Date\nx = 1, y = 1, rule = B3/S23\no!";
        ParsedPattern pp = new ParsedPattern("", "Author", "", "Date", "B3/S23", base);
        String result = Parser.fromPattern(pp);

        assertEquals(expected, result);
    }

    @Test
    void includes_description_when_converting_from_pattern() {
        byte[][] base = {{1}};
        String expected = "#C Description\nx = 1, y = 1, rule = B3/S23\no!";
        ParsedPattern pp = new ParsedPattern("", "", "Description", "", "B3/S23", base);
        String result = Parser.fromPattern(pp);

        assertEquals(expected, result);
    }

    @Test
    void constructor_is_private() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<Parser> constructor = Parser.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}