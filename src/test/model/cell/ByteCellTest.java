package model.cell;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ByteCellTest {
    @Test
    void initializes_as_dead() {
        ByteCell result = new ByteCell();
        assertEquals("cell is dead", result.toString());
    }
    @Test
    void initializes_as_passed() {
        ByteCell result = new ByteCell(true);
        assertEquals("cell is alive", result.toString());
        result = new ByteCell(false);
        assertEquals("cell is dead", result.toString());
    }
    @Test
    void is_resurrected_on_resurrect() {
        ByteCell result = new ByteCell().resurrect();
        assertEquals("cell is alive", result.toString());
    }
    @Test
    void is_killed_on_kill() {
        ByteCell result = new ByteCell().resurrect();
        assertEquals("cell is alive", result.toString());
        result.kill();
        assertEquals("cell is dead", result.toString());
    }
}