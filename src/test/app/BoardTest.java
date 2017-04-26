package app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoardTest {
    @Test
    void should_insert_byte_board() {
        byte[][] testBoard = {
                {1,0},
                {0,1}
        };
        Board board = new Board(2, 2);
        assertEquals("0000", board.toString());
        board.insertPattern(testBoard);
        assertEquals("1001", board.toString());
    }
    @Test
    void should_fill_all_cells_with_passed_state() {
        Board board = new Board(4, 4);
        assertEquals("0000000000000000", board.toString());
        board.fill((byte) 1);
        assertEquals("1111111111111111", board.toString());
        board.fill((byte) 0);
        assertEquals("0000000000000000", board.toString());
    }
    @Test
    void should_expand_when_inserting_too_large_pattern() {
        Board board = new Board(2, 2);
        byte[][] pattern = new byte[][] {
                {1,1,1,0},
                {1,1,0,1},
                {1,0,1,1},
                {0,1,1,1}
        };
        board.insertPattern(pattern);
        assertEquals(4, board.getSizeX());
        assertEquals(4, board.getSizeY());
        assertEquals("1110110110110111", board.toString());
    }
    @Test
    void squares_remain_squares() {
        Board testBoard = new Board(4, 4);

        testBoard.insertPattern(new byte[][]{
                {0,0,0,0},
                {0,1,1,0},
                {0,1,1,0},
                {0,0,0,0}
        });

        testBoard.nextGeneration();

        assertEquals("0000011001100000", testBoard.toString());
    }

    @Test
    void single_dot_dies() {
        byte[][] testBoard = {
                {0,0,0},
                {0,1,0},
                {0,0,0}
        };
        Board testGrid = new Board(3, 3);

        testGrid.insertPattern(testBoard);
        testGrid.nextGeneration();
        assertEquals("000000000", testGrid.toString());
    }

    @Test
    void should_step_glider() {
        byte[][] glider = {
                {0,1,0},
                {0,0,1},
                {1,1,1}
        };
        Board board = new Board(5, 5);
        board.insertPattern(glider);
        assertEquals("0100000100111000000000000", board.toString());
        board.nextGeneration();
        assertEquals("0000010100011000100000000", board.toString());
    }

}