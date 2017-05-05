package model.board;

import model.Size;
import rules.RuleSet;

import java.util.List;
import java.util.function.Consumer;

public interface Board {
    void nextGenerationConcurrent();
    void nextGeneration();
    void insertPattern(byte[][] pattern);
    void addPostResizeListener(Consumer<Size> runner);
    void addPreResizeListener(Consumer<Size> runner);
    RuleSet getRuleSet();
    void setRuleSet(RuleSet ruleSet);
    int getSizeX();
    int getSizeY();
    List<List<Boolean>> getEnumerable();
    List<List<Cell>> getThisGen();
    Board patternToBoard();
    boolean getDynamic();
    void setDynamic(boolean dynamic);
    boolean getMultithreading();
    void setMultithreading(boolean multithreadingEnabled);
    void setCellAlive(int y, int x, boolean alive);
    boolean getCellAlive(int y, int x);
    void addRowTop();
    void addRowBottom();
    void addColLeft();
    void addColRight();
    void clearBoard();
    int getGenCount();

    int getAliveCount();
    long getDeadCount();
}
