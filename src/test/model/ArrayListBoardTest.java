package model;

import model.board.ArrayListBoard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArrayListBoardTest {
    @Test
    void should_insert_byte_board() {
        byte[][] testBoard = {
                {1,0},
                {0,1}
        };
        ArrayListBoard board = new ArrayListBoard(2, 2);
        assertEquals("0000", board.toString());
        board.insertPattern(testBoard);
        assertEquals("1001", board.toString());
    }

    @Test
    void should_expand_when_inserting_too_large_pattern() {
        ArrayListBoard board = new ArrayListBoard(2, 2);
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
        ArrayListBoard testBoard = new ArrayListBoard(4, 4);

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
        ArrayListBoard testGrid = new ArrayListBoard(3, 3);

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
        ArrayListBoard board = new ArrayListBoard(5, 5);
        board.insertPattern(glider);
        assertEquals("0000000100000100111000000", board.toString());
        board.nextGeneration();
        assertEquals("0000000000010100011000100", board.toString());
    }

}