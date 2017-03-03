package Test;

import app.Grid;
import org.junit.jupiter.api.Test;

class GridTest {
    @Test
    void nextGeneration() {
        byte[][] testBoard = {
                { 0, 0, 0, 0 },
                { 0, 1, 1, 0 },
                { 0, 1, 1, 0 },
                { 0, 0, 0, 0 }
        };
        Grid testGrid = new Grid(4,4);
        testGrid.setBoard(testBoard);
        testGrid.nextGeneration();

        org.junit.jupiter.api.Assertions.assertEquals(testGrid.toString(),"0000011001100000");
    }

}