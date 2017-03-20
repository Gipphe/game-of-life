package app;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ReadableBoard {
    private byte[][] board;
    private int y;
    private int x;

    Stack<String> rows = new Stack<String>();

    public void setBoard(byte[][] board) {
        this.board = board;

        this.y = board.length;
        this.x = board[0].length;

        StringBuilder sb = new StringBuilder();
        for (byte[] row : board) {
            for (byte cell : row) {
                sb.append(cell);
            }
            rows.add(sb.toString());
            sb.setLength(0);
        }
    }
    ReadableBoard(byte[][] board) {
        setBoard(board);
    }

    int getX() {
        return x;
    }
    int getY() {
        return y;
    }
    Stack<String> getRows() {
        return rows;
    }
}
class RLEContents implements Iterable<String> {
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
    @NotNull
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

    @Override
    public Iterator<String> iterator() {
        return commands.iterator();
    }

    RLEContents(String contents) {
        String bareData = stripMeta(contents);

        String header = firstLine(bareData);
        this.x = getX(header);
        this.y = getY(header);
        this.rule = getRule(header);

        String justCommands = removeRuleLine(bareData);

        this.commands = extractCommands(justCommands);
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

        for (String command : contents) {
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

    private static String concentrate(String source) {
        int length = source.length();
        String lengthString = length == 1 ? "" : "" + length;
        return lengthString + source.charAt(0);
    }
    private static boolean isEmptyRow(String row) {
        Pattern pattern = Pattern.compile("[^0,\\]\\[ ]");
        Matcher match = pattern.matcher(row);
        boolean isEmpty = true;
        if (match.find()) {
            isEmpty = false;
        }
        return isEmpty;
    }
    public static String fromBoard(byte[][] board) {
        ReadableBoard rboard = new ReadableBoard(board);

        StringBuilder result = new StringBuilder();
        result
            .append("x = ")
            .append(rboard.getX())
            .append(", y = ")
            .append(rboard.getY())
            .append(", rule = B3/S23")
            .append("\n");

        StringBuilder newlineCharacters = new StringBuilder();
        Stack<String> rows = rboard.getRows();
        int lastRowIndex = rows.size() - 1;
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            String row = rows.get(rowIndex);
            if (isEmptyRow(row)) {
                newlineCharacters.append("$");
                continue;
            }
            if (rowIndex != 0) {
                newlineCharacters.append("$");
                result.append(concentrate(newlineCharacters.toString()));
                newlineCharacters.setLength(0);
            }
            String[] statesInRow = row.split("");

            StringBuilder stateCharacters = new StringBuilder();
            int indexOfLastState = statesInRow.length - 1;

            int colsToRun = statesInRow.length;
            if (rowIndex == lastRowIndex) {
                System.out.println(row);
                int lastAlive = row.lastIndexOf('1');
                colsToRun = (lastAlive == -1) ? statesInRow.length : lastAlive + 1;
                System.out.println(colsToRun);
            }

            for (int i = 0; i < colsToRun; i++) {
                String state = statesInRow[i];
                String value = (state.equals("1") ? "o" : "b");

                String nextState = "";
                if (i < indexOfLastState) {
                    nextState = statesInRow[i + 1];
                }

                if (state.equals(nextState)) {
                    stateCharacters.append(value);
                    continue;
                }
                stateCharacters.append(value);
                result.append(concentrate(stateCharacters.toString()));
                stateCharacters.setLength(0);
            }
        }
        result.append("!");

        return result.toString();
    }
}
