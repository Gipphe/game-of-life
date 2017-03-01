package app

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

public class GridTest {

    void testNextGeneration() {
        byte[][] array = new byte[4][4];
        array[1][0] = 1;
        array[1][1] = 1;
        array[1][2] = 1;

        byte[][] array2 = new byte[4][4];
        array2[0][1] = 1;
        array2[1][1] = 1;
        array2[2][1] = 1;

        Grid grid = new Grid(3, 3)

        grid.setGrid(array);
        grid.nextGeneration();


        assertArrayEquals(grid.getGrid(),array2);
    }

    @Test
    void testPattern() {

    }
}
