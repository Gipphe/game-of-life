package test;

import app.Grid;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GridTest {
    @Test
    void squares_remain_squares() {
        byte[][] testBoard = {
                { 0, 0, 0, 0 },
                { 0, 1, 1, 0 },
                { 0, 1, 1, 0 },
                { 0, 0, 0, 0 }
        };
        Grid testGrid = new Grid(4, 4);
        testGrid.setBoard(testBoard);
        testGrid.nextGeneration();

        assertEquals("0000011001100000", testGrid.toString());
    }
    void single_dot_dies() {
        byte[][] testBoard = {
                {0,0,0},
                {0,1,0},
                {0,0,0}
        };
        Grid testGrid = new Grid(3, 3);
        testGrid.setBoard(testBoard);
        testGrid.nextGeneration();

        assertEquals("000000000", testGrid.toString());
    }

}