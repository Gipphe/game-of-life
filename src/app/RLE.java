package app;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RLEContents {
    private int x;
    private int y;
    private String rule;
    private Stack<String> commands;

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public String getRule() {
        return rule;
    }
    public Stack<String> getCommands() {
        return commands;
    }

    private static int getDimension(String source, String dim) {
        String result = getHeaderProperty(source, dim + " = (\\d+),");
        if (result == null) {
            return 0;
        }
        return Integer.parseInt(result);
    }
    private static String getHeaderProperty(String sourceString, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sourceString);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
    private static List<String> splitByNewline(String source) {
        return Arrays.asList(source.split("\\r?\\n"));
    }
    private static String firstLine(String source) {
        return splitByNewline(source).get(0);
    }
    private static String getRule(String rle) {
        return getHeaderProperty(rle, "rule = (.+)");
    }
    private static int getX(String rle) {
        return getDimension(rle, "x");
    }
    private static int getY(String rle) {
        return getDimension(rle, "y");
    }
    @NotNull
    private static String stripMeta(String rle) {
        String[] lines = rle.split("\\r?\\n");
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            if (line.startsWith("#")) {
                continue;
            }
            result.append(line);
            result.append("\n");
        }

        return result.toString();
    }
    private static String removeRuleLine(String source) {
        if (!source.startsWith("x = ")) {
            return source;
        }

        List<String> lines = splitByNewline(source);
        Stack<String> st = new Stack<String>();
        st.addAll(lines);
        st.remove(0);
        StringBuilder sb = new StringBuilder();
        for (String line : st) {
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }
    private static List<String> splitToCommands(String str) {
        Pattern pattern = Pattern.compile("\\d*\\w|\\$|!");
        Matcher matcher = pattern.matcher(str);
        List<String> result = new ArrayList<String>();
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }

    private static Stack<String> extractCommands(String commands) {
        Stack<String> stack = new Stack<String>();
        List<String> list = splitToCommands(commands);
        stack.addAll(list);
        return stack;
    }

    RLEContents(String contents) {
        String bareData = stripMeta(contents);

        String header = firstLine(bareData);
        this.x = getX(header);
        this.y = getY(header);
        this.rule = getRule(header);

        String justCommands = removeRuleLine(bareData);
        System.out.println("justCommands: " + justCommands);

        this.commands = extractCommands(justCommands);
        System.out.println("commands: " + commands.toString());
    }
}
public class RLE {
    public static byte[][] toBoard(String rle) {
        RLEContents contents = new RLEContents(rle);

        int limx = contents.getX();
        int limy = contents.getY();
        String rule = contents.getRule();
        if (!rule.equals("B3/S23")) {
            throw new RuntimeException("Incorrect rule");
        }

        byte[][] board = new byte[limy][limx];

        int pX = 0;
        int pY = 0;

        int count = 1;

        for (String command : contents.getCommands()) {
            System.out.println("command: " + command);
            Stack<String> commandBits = new Stack<String>();
            commandBits.addAll(Arrays.asList(command.split("")));

            String state = commandBits.pop();

            if (state.equals("!")) {
                break;
            }
            if (state.equals("$")) {
                pY++;
                pX = 0;
                continue;
            }

            if (commandBits.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (String number : commandBits) {
                    sb.append(number);
                }
                count = Integer.parseInt(sb.toString());
            }

            for (int i = 0; i < count; i++) {
                try {
                    byte newState = state.equals("o") ? (byte) 1 : (byte) 0;
                    System.out.println("pX: " + pX);
                    System.out.println("pY: " + pY);
                    System.out.println("newState: " + newState);
                    board[pY][pX] = newState;
                    pX++;
                } catch (ArrayIndexOutOfBoundsException e) {
                    pX = 0;
                    pY++;
                    break;
                }
            }
            count = 1;
        }
        return board;
    }

    private static String accumulate(String source) {
        int length = source.length();
        String lengthString = length == 1 ? "" : "" + length;
        return lengthString + source.charAt(0);
    }
    public static String fromBoard(byte[][] board) {
        StringBuilder result = new StringBuilder();
        int dimy = board.length;
        int dimx = board[0].length;
        String rule = "B3/S23";
        result.append("x = ");
        result.append(dimx);
        result.append(", y = ");
        result.append(dimy);
        result.append(", rule = ");
        result.append(rule);
        result.append("\n");
        String accumulator = "";

        for (int y = 0; y < board.length; y++) {
            byte[] row = board[y];
            for (int x = 0; x < row.length; x++) {
                byte cell = row[x];
                String value = cell == 1 ? "o" : "b";
                accumulator += value;
                if (x + 1 < board[y].length) {
                    if (cell != board[y][x + 1]) {
                        result.append(accumulate(accumulator));
                        accumulator = "";
                    }
                } else {
                    result.append(accumulate(accumulator));
                    accumulator = "";
                }
            }
            if (y < board.length - 1) {
                accumulator += "$";
                int lastChar = result.length() - 1;
                if (result.charAt(lastChar) != "$") {
                    
                }
                result.append("$");
            }
        }

        result.append("!");
        return result.toString();
    }
}
