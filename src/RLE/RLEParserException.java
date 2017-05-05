package RLE;

/**
 * A runtime exception for handling cases where the RLE parser encounters an unrecoverable error.
 */
public class RLEParserException extends RuntimeException {
    /**
     * Constructor.
     */
    public RLEParserException() {
        super();
    }

    /**
     * Constructor.
     * @param s Description of this exception, and why it happened.
     */
    public RLEParserException(String s) {
        super(s);
    }
}
