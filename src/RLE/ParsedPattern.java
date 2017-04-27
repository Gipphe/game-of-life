package RLE;

public class ParsedPattern {
    private String rule;
    private byte[][] pattern;

    ParsedPattern(String rule, byte[][] pattern) {
        this.rule = rule;
        this.pattern = pattern;
    }
    public String getRule() {
        return rule;
    }
    public byte[][] getPattern() {
        return pattern;
    }
}
