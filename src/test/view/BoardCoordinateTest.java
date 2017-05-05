package view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardCoordinateTest {
    @Test
    void should_accept_passed_coordinates() {
        BoardCoordinate bc = new BoardCoordinate(2, 3);
        assertEquals(2, bc.getY());
        assertEquals(3, bc.getX());
    }

    @Test
    void should_return_same_hashcode_for_similar_objects() {
        BoardCoordinate bc1 = new BoardCoordinate(2, 3);
        BoardCoordinate bc2 = new BoardCoordinate(2, 3);
        assertEquals(bc1.hashCode(), bc2.hashCode());
    }

    /**
     * This test is mostly bogus because of how hashCode works. Two identical objects have the same hashCode, this
     * is a requirement from the hashCode spec itself, but two objects that return false for obj1.equals(obj2) can
     * also have the same hashCode.
     */
    @Test
    void should_return_different_hashcode_for_dissimilar_objects() {
        BoardCoordinate bc1 = new BoardCoordinate(2, 3);
        BoardCoordinate bc2 = new BoardCoordinate(24, 199);
        assertNotEquals(bc1.hashCode(), bc2.hashCode());
    }

    @Test
    void should_return_false_for_equality_between_dissimilar_objects() {
        assertFalse(new BoardCoordinate(1, 1).equals(new BoardCoordinate(1, 2)));
    }

    @Test
    void should_return_true_for_equality_between_similar_objects() {
        assertTrue(new BoardCoordinate(1, 1).equals(new BoardCoordinate(1, 1)));
    }

    @Test
    void should_return_a_verbose_string_representing_the_object() {
        assertEquals("Coordinate at 1, 2", new BoardCoordinate(1, 2).toString());
    }

    @Test
    void should_not_equal_when_coordinates_are_very_similar_but_not_equal() {
        assertFalse(new BoardCoordinate(34, 0).equals(new BoardCoordinate(33, 0)));
        assertFalse(new BoardCoordinate(22, 1).equals(new BoardCoordinate(22, 2)));
        assertFalse(new BoardCoordinate(33, 1).equals(new BoardCoordinate(32, 0)));
    }
}