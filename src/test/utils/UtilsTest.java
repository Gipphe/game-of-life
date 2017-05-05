package utils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.Utils.limit;
import static utils.Utils.wrap;

class UtilsTest {
    @Test
    void limit_should_not_change_value_that_is_within_boundary() {
        assertEquals(1, limit(2, 0, 1));
    }
    @Test
    void limit_should_limit_value_to_min_boundary() {
        assertEquals(0, limit(2, 0, -1));
    }
    @Test
    void limit_should_limit_value_to_max_boundary() {
        assertEquals(2, limit(2, 0, 3));
    }
    @Test
    void limit_should_throw_if_max_is_less_than_min() {
        boolean thrown = false;
        try {
            limit(1, 2, 1);
        } catch(RuntimeException e) {
            assertEquals("Max is less than min", e.getMessage());
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    void limit_should_not_change_double_value() {
        assertEquals(1, limit(2.0, 0.1, 1.0));
    }
    @Test
    void limit_should_limit_double_value_to_min_boundary() {
        assertEquals(0.2, limit(2.2, 0.2, 0.0));
    }
    @Test
    void limit_should_limit_double_value_to_max_boundary() {
        assertEquals(2.3, limit(2.3, 0.1, 3.4));
    }
    @Test
    void limit_should_throw_if_double_max_is_less_than_min() {
        boolean thrown = false;
        try {
            limit(0.9, 1, 0.95);
        } catch(RuntimeException e) {
            assertEquals("Max is less than min", e.getMessage());
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    void wrap_should_not_wrap_if_within_boundaries() {
        assertEquals(1, wrap(2, 0, 1));
    }
    @Test
    void wrap_should_wrap_around_upper_boundary() {
        assertEquals(1, wrap(2, 0, 3));
    }
    @Test
    void wrap_should_wrap_around_lower_boundary() {
        assertEquals(1, wrap(2, 0, -1));
    }

    @Test
    void constructor_is_private() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<Utils> constructor = Utils.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}