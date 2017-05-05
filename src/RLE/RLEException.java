package RLE;

/**
 * A normal exception in RLE handling, not strictly related to RLE parsing itself, but may be.
 */
public class RLEException extends RuntimeException {
    /**
     * Constructor.
     */
    public RLEException() {
        super();
    }

    /**
     * Constructor.
     * @param s The description of the exception, and why it happened.
     */
    public RLEException(String s) {
        super(s);
    }
}
