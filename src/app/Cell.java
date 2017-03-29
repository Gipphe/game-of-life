package app;

public class Cell {
    private byte state;
    private byte neighbors = 0;
    Cell() {
        state = 0;
    }
    Cell(byte val) {
        state = val;
    }
    byte getState() {
        return state;
    }
    void setState(byte newState) {
        state = newState;
    }
    void setNeighbors(byte neighbors) {
        this.neighbors = neighbors;
    }
    byte getNeighbors() {
        return neighbors;
    }
    void incrementNeighbors() {
        neighbors++;
    }
}
