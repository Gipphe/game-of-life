package prototype;

public class Grid {
    private int[][] grid = new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 1, 1, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
    };

    void tick() {
        int[][] newGrid = new int[grid.length][grid[0].length];
        for (int y = 0; y < grid.length; y++) {
            int[] row = grid[y];
            for (int x = 0; x < row.length; x++) {
                int cell = row[x];

                int numNeighbours = neighbours(x, y);
                int state = rules(cell, numNeighbours);
                newGrid[y][x] = state;
            }
        }
        grid = newGrid;
    }

    private int rules(int cell, int num) {
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
                int len = grid.length;

                if (nx < 0 || ny < 0 || nx >= len || ny >= len) {
                    continue;
                }

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

    @Override
    public String toString() {
        String msg = "";
        for (int[] row : grid) {
            String s = "";
            for (int cell : row) {
                String val = cell == 1 ? "0" : ".";
                s = s + val + " ";
            }
            msg = msg + "\n" + s;
        }
        return msg;
    }
}
