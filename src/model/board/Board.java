package model.board;

import model.Size;
import rules.RuleSet;

import java.util.List;
import java.util.function.Consumer;

public interface Board {
    void nextGenerationConcurrent();
    void nextGeneration();
    void insertPattern(byte[][] pattern);
    void addResizeListener(Consumer<Size> runner);
    RuleSet getRuleSet();
    void setRuleSet(RuleSet ruleSet);
    int getSizeX();
    int getSizeY();
    List<List<Boolean>> getEnumerable();
    Board patternToBoard();
    boolean getDynamic();
    void setDynamic(boolean dynamic);
    void setCellAlive(int y, int x, boolean alive);
    boolean getCellAlive(int y, int x);
    void addRowTop();
    void addRowBottom();
    void addColLeft();
    void addColRight();
    void clearBoard();
    int getGenCount();
}
