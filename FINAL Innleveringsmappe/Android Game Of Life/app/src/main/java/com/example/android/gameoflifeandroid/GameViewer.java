package com.example.android.gameoflifeandroid;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.constraint.solver.widgets.Rectangle;
import android.util.AttributeSet;
import android.view.View;

class GameViewer extends View {
    public Paint aliveCellPaint = new Paint();
    public Paint deadCellPaint = new Paint();
    public float cellSize;
    public float topRect;
    public float bottomRect;
    public float leftRect;
    public float rightRect;
    public Board board = new Board(100, 100);
    byte[][] gameBoard = board.getBoard();
    boolean isAnimating = false;


    /**
     * Constructor for the game viewer.
     *
     * @param context
     * @param attrs
     */
    public GameViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        startUpProcedures();
    }

    /**
     * Method that sets aliveCellPaint to black and deadCellPaint to white.
     */
    public void startUpProcedures() {
        aliveCellPaint.setColor(Color.BLACK);
        deadCellPaint.setColor(Color.WHITE);

        // The added +1 is to account for the decimals lost when widthPixels/xaxis gets made into an int.
        cellSize = (Resources.getSystem().getDisplayMetrics().widthPixels / board.xaxis) + 1;
    }

    /**
     * Overrides onDraw method. Draws the board.
     *
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas) {
        gameBoard = board.getBoard();
        topRect = 0;
        bottomRect = cellSize;
        leftRect = 0;
        rightRect = cellSize;
        for(int i = 0; i<board.yaxis; i++) {
            for(int j = 0; j<board.xaxis; j++) {
                if(gameBoard[i][j] == 0){
                    canvas.drawRect(leftRect,topRect,rightRect-1,bottomRect-1,deadCellPaint);
                } else {
                    canvas.drawRect(leftRect,topRect,rightRect-1,bottomRect-1,aliveCellPaint);
                }
                leftRect += cellSize;
                rightRect += cellSize;
            }
            leftRect = 0;
            rightRect = cellSize;
            topRect += cellSize;
            bottomRect += cellSize;
        }

        if (isAnimating) {
            board.nextGeneration();
            try {
                Thread.sleep(300);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            invalidate();
        }
    }
}