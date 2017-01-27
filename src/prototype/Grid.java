package prototype;

/**
 * Created by Jonas on 27.01.2017.
 */
public class Grid {
    public boolean[][] grid = new boolean[8][8];

    public void tick() {
        boolean[][] newGrid = new boolean[8][8];
        for (int y = 0; y < grid.length; y++) {
            boolean[] row = grid[y];
            for (int x = 0; x < row.length; x++) {
                boolean cell = row[x];

                int numNeighbours = neighbours(x, y); // international english ftw
                boolean state = rules(cell, numNeighbours);
                newGrid[y][x] = state;
            }
        }
        grid = newGrid;
    }

    public boolean rules(boolean cell, int num) {
        if (!cell) {
            if (num >= 3) {
                return true;
            }
        } else {
            if (num > 1 && num < 4) {
                return true;
            }
        }
        return false;
    }

    public int neighbours(int ox, int oy) {
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

                if (grid[ny][nx]) {
                    num++;
                }
            }
        }
        return num;
    }

    @Override
    public String toString() {
        String msg = "";
        for (int i = 0; i < grid.length; i++) {
            String s = "";
            for(int j = 0; j < grid[0].length; j++) {
                int val = grid[i][j] ? 1 : 0;
                s = s + val + " ";
            }
            msg = msg + "\n" + s;
        }
        return msg;
    }
}
