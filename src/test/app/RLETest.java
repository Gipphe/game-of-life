package test.app;

import app.RLE;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RLETest {
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
        byte[][] result = RLE.toBoard(base);

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

        byte[][] result = RLE.toBoard(base);

        byte[][] expected = {
                {1,1},
                {1,1}
        };

        assertEquals(boardToString(expected), boardToString(result));
    }
    @Test
    void converts_from_RLE_even_with_double_digit_length_encoding() {
        String base = "x = 18, y = 1, rule = B3/S23\n18o!";
        byte[][] result = RLE.toBoard(base);

        byte[][] expected = {
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        };

        assertEquals(boardToString(expected), boardToString(result));
    }

    @Test
    void converts_pulsar_from_RLE_to_board() {
        String base = "x = 13, y = 13, rule = B3/S23\n" +
                "2b3o3b3o2b2$o4bobo4bo$o4bobo4bo$o4bobo4bo$2b3o3b3o2b2$2b3o3b3o2b$o4bobo4bo$o4bobo4bo$o4bobo4bo2$2b3o3b3o!";

        byte[][] result = RLE.toBoard(base);

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

        String result = RLE.fromBoard(base);

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

        String result = RLE.fromBoard(base);

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
                "2b3o3b3o2b2$o4bobo4bo$o4bobo4bo$o4bobo4bo$2b3o3b3o2b2$2b3o3b3o2b$o4bobo4bo$o4bobo4bo$o4bobo4bo2$2b3o3b3o!";

        String result = RLE.fromBoard(base);

        assertEquals(expected, result);
    }

    @Test
    void converts_from_board_even_when_there_are_double_digits_length_encoding() {
        byte[][] base = {
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        };

        String expected = "x = 18, y = 1, rule = B3/S23\n" +
                "18o!";
        String result = RLE.fromBoard(base);

        assertEquals(expected, result);
    }

}