package RLE;

/**
 * Container for an RLE to ArrayListBoard parsing's output, containing the parsed pattern and the rule the pattern is designed
 * for. Can be expanded to include metadata such as author, creation date etc.
 */
public class ParsedPattern {
    private String name;
    private String author;
    private String description;

    /**
     * The rule designation that this pattern is designed for.
     */
    private String rule;
    /**
     * The pattern itself, represented as a two-state two-dimensional byte array for simplicity.
     */
    private byte[][] pattern;
    public ParsedPattern(String name, String author, String description, String rule, byte[][] pattern) {
        this.name = name;
        this.author = author;
        this.description = description;
        this.rule = rule;
        this.pattern = pattern;
    }

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

    /**
     *
     * @return The name of the pattern.
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return The name of the author.
     */
    public String getAuthor() {
        return author;
    }

    /**
     *
     * @return The description of the pattern.
     */
    public String getDescription() {
        return description;
    }
}
