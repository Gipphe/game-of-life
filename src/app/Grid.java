package app;

public class Grid {
    public int yaxis;
    public int xaxis;
    private byte[][] grid;

    public Grid(int xaxis, int yaxis){
        this.yaxis =xaxis;
        this.xaxis =yaxis;
        grid = new byte[yaxis][xaxis];
    }

    public void setGrid(byte[][] newGrid) {
        grid = newGrid;
    }

    public void nextGeneration() {
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

                ny = wrap(leny, ny);
                nx = wrap(lenx, nx);

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
    public static int wrap(int lim, int val) {
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
                String val = cell == 1 ? "1" : "0";
                s = s + val;
            }
            msg = msg + s;
        }
        return msg;
    }

    public void setBoard(byte[][] newBoard){
        grid = newBoard;
    }

    public byte[][] getGrid() {
        return grid;
    }

}
