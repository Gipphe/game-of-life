package app;

import java.util.Arrays;

public class Grid {
    int xaxis;
    int yaxis;
    private byte[][] grid;

    public Grid(int xaxis,int yaxis){
        this.xaxis=xaxis;
        this.yaxis=yaxis;
        grid = new byte[yaxis][xaxis];
        pattern();
    }
    void pattern() {
        grid[5][5]=1;
        grid[5][6]=1;
        grid[5][7]=1;
        grid[6][6]=1;
        for(int i = 0; i<grid.length; i++){
            grid[4][i]=1;
        }
    }

    void tick() {
        byte[][] newGrid = new byte[grid.length][grid[0].length];
        for (int y = 0; y < grid.length; y++) {
            byte[] row = grid[y];
            for (int x = 0; x < row.length; x++) {
                int cell = row[x];

                int numNeighbours = neighbours(x, y);
                byte state = rules(cell, numNeighbours);
                newGrid[y][x] = state;
            }
        }
        grid = newGrid;
    }

    private byte rules(int cell, int num) {
        if (cell == 0) {
            if (num == 3) {
                return 1;
            }
        } else {
            if (num > 1 && num < 4) {
                return 1;
            }
        }
        return 0;
    }

    private int neighbours(int ox, int oy) {
        int num = 0;
        for (int y = -1; y < 2; y++) {
            for (int x = -1; x < 2; x++) {
                int nx = ox + x;
                int ny = oy + y;
                int lenx = grid[oy].length;
                int leny = grid.length;

                ny = limit(leny, ny);
                nx = limit(lenx, nx);

                if (x == 0 && y == 0) {
                    continue;
                }
                if (grid[ny][nx] == 1) {
                    num++;
                }
            }
        }
        return num;
    }

    /**
     * Makes the coordinate wrap around to the other side of the grid if it is out of bounds
     * @param lim int
     * @param val int
     * @return int
     */
    private static int limit(int lim, int val) {
        if (val >= lim) {
            return val - lim;
        } else if (val < 0) {
            return val + lim;
        }

        return val;
    }

    @Override
    public String toString() {
        String msg = "";
        for (byte[] row : grid) {
            String s = "";
            for (int cell : row) {
                String val = cell == 1 ? "0" : ".";
                s = s + val + " ";
            }
            msg = msg + "\n" + s;
        }
        return msg;
    }

    public byte[][] getGrid() {
        return grid;
    }
}
