package sample;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;

public class AnimationProcess extends AnimationTimer {
    private Canvas canvas;
    private int [][] board;
    private int numberOfIterations;
    private boolean isUnlimited;
    private boolean PBC;
    private int height;
    private int width;

    public AnimationProcess(Canvas canvas, int[][] board,int height,int width, int numberOfIterations, boolean isUnlimited, boolean PBC) {
        this.canvas = canvas;
        this.board = board;
        this.height = height;
        this.width = width;
        this.numberOfIterations = numberOfIterations;
        this.isUnlimited = isUnlimited;
        this.PBC = PBC;
    }
    int iterator = 0;
    long last_action = 0;
    @Override
    public void handle(long now) {
        if(now - last_action >= 100_000_000)
        {
            Controller.draw(board,height,width,canvas);
            board = Controller.generateNextGen(board,height,width,PBC);
            last_action = now;
            if(!this.isUnlimited)
            {
                iterator++;
                if(iterator > numberOfIterations) {
                    Controller.draw(board,height,width,canvas);
                    this.stop();
                }
            }
            //insert code here
        }
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }
}
