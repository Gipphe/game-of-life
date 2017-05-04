package RLE;

/**
 * Container for an RLE to Board parsing's output, containing the parsed pattern and the rule the pattern is designed
 * for. Can be expanded to include metadata such as author, creation date etc.
 */
public class ParsedPattern {
    /**
     * The rule designation that this pattern is designed for.
     */
    private String rule;
    /**
     * The pattern itself, represented as a two-state two-dimensional byte array for simplicity.
     */
    private byte[][] pattern;

    /**
     * Constructor
     * @param rule The rule this pattern is designed for.
     * @param pattern The pattern itself.
     */
    ParsedPattern(String rule, byte[][] pattern) {
        this.rule = rule;
        this.pattern = pattern;
    }

    /**
     *
     * @return The rule this pattern is designed for.
     */
    public String getRule() {
        return rule;
    }

    /**
     *
     * @return The pattern itself.
     */
    public byte[][] getPattern() {
        return pattern;
    }
}
