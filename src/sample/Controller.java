package sample;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public int [][]board;
    public int [][] clickableBoard = new int[90][120];
    AnimationProcess animationProcess;
    @FXML
    Button startButton;
    @FXML
    Button stopButton;
    @FXML
    Button resumeButton;
    @FXML
    Button resetButton;
    @FXML
    CheckBox PBCcheckBox;
    @FXML
    Label structureChoiceLabel;
    @FXML
    ComboBox<String> structureComboBox;
    @FXML
    Label heightLabel;
    @FXML
    TextField heightTextField;
    @FXML
    Label widthLabel;
    @FXML
    TextField widthTextField;
    @FXML
    Label iterationLabel;
    @FXML
    TextField iterationsTextField;
    @FXML
    Canvas canvas;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PBCcheckBox.setSelected(true);
        structureComboBox.setItems(FXCollections.observableArrayList("Still","Glider","Manual","Oscillator","Random"));
        structureComboBox.setValue("Manual");
        heightTextField.setText("90");
        widthTextField.setText("120");
        iterationsTextField.setText("Unlimited");


        heightTextField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                heightTextField.clear();
            }
        });
        widthTextField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                widthTextField.clear();
            }
        });
        iterationsTextField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                iterationsTextField.clear();
            }
        });

        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.LIGHTGRAY);
        g.fillRect(0,0,970,730);

        drawGrid(90,120,this.canvas);
        
        this.canvas.setOnMouseClicked(this::handleDraw);
        this.canvas.setOnMouseDragged(this::handleDraw);

    }

    private void handleDraw(MouseEvent mouseEvent) {
        try{
            animationProcess.stop();
            clickableBoard = animationProcess.getBoard();
        }catch (Exception e){}
        double mouseX = mouseEvent.getX()/8.0 ;
        double mouseY = mouseEvent.getY()/8.0 ;
        int x_coord = (int) mouseX;
        int y_coord = (int) mouseY;
        //System.out.println(x_coord + " " + y_coord);
        try{
            changeState(this.clickableBoard[y_coord][x_coord],y_coord,x_coord);
        }catch (IndexOutOfBoundsException e){}
        draw(clickableBoard,90,120,canvas);
        drawGrid(90,120,canvas);
    }


    public void startButtonClicked()
    {
        //drawGrid();
        try{
            animationProcess.stop();
        }catch (Exception e){
            System.out.println("No animation running!");
        }
        // get all values from gui

        String structure = structureComboBox.getValue();
        boolean isPBC = PBCcheckBox.isSelected();
        int height = Integer.parseInt(heightTextField.getText());
        if(height > 90) height = 90;
        int width = Integer.parseInt(widthTextField.getText());
        if(width > 120) width = 120;
        boolean isUnlimited = iterationsTextField.getText().equals("Unlimited");
        int iterationsNo = 0;
        if(!isUnlimited) iterationsNo = Integer.parseInt(iterationsTextField.getText());
        //values were taken
//        System.out.println(structure);
//        System.out.println(isPBC);
//        System.out.println(height);
//        System.out.println(width);
//        System.out.println(isUnlimited);
//        System.out.println(iterationsNo);
        board = initializeBoard(structure,height,width);
        animationProcess = new AnimationProcess(this.canvas,board,height,width,iterationsNo,isUnlimited,isPBC);
        animationProcess.start();
    }
    public void stopButtonClicked()
    {
        clickableBoard = animationProcess.getBoard();
        try{
            animationProcess.stop();
        }catch(NullPointerException e){
            System.out.println("No animation running!");
        }
    }
    public void resumeButtonClicked()
    {
        animationProcess.setBoard(clickableBoard);
        try {
            animationProcess.start();
        } catch (NullPointerException nullException) {
            System.out.println("No animation running!");
        }
    }
    public void resetButtonClicked()
    {
        for (int i = 0; i < clickableBoard.length; i++) {
            for (int j = 0; j < clickableBoard[0].length ; j++) {
                clickableBoard[i][j] = 0;
            }
        }
        try{
            animationProcess.stop();
            animationProcess.setBoard(clickableBoard);
        }catch (Exception e)
        {
            System.out.println("No animation running!");
        }
        draw(clickableBoard,clickableBoard.length,clickableBoard[0].length,canvas);
    }


    public static void draw(int [][]currentGeneration, int height, int width, Canvas canvas)
    {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.LIGHTGRAY);
        g.fillRect(0,0,970,730);
        g.setFill(Color.BLACK);
        int cell_size = 8;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if(currentGeneration[i][j] == 1)
                {
                    double x_coord = cell_size*j;
                    double y_coord = cell_size*i;
                    g.fillRect(x_coord,y_coord,cell_size,cell_size);
                }
            }
        }
        drawGrid(height,width,canvas);
    }
    public static int countNeighbours(int [][] board,int x, int y)
    {
        int counter = 0;

        counter+= board[x-1][y-1];
        counter+= board[x-1][y];
        counter+= board[x-1][y+1];

        counter+= board[x][y-1];
        counter+= board[x][y+1];

        counter+= board[x+1][y-1];
        counter+= board[x+1][y];
        counter+= board[x+1][y+1];

        return counter;
    }

    public int [][] initializeBoard(String structure,int height,int width)
    {
        int [][] newBoard = new int[height][width];
        int x = height/2 ;
        int y = width/2 ;

        switch (structure){
            case "Still":
                newBoard[x - 1][y - 1] = 1;
                newBoard[x - 1][y] = 1;
                newBoard[x][y-2] = 1;
                newBoard[x][y+1] = 1;
                newBoard[x + 1][y - 1] = 1;
                newBoard[x + 1][y] = 1;
                break;
            case "Glider":
                newBoard[x - 1][y] = 1;
                newBoard[x - 1][y + 1] = 1;
                newBoard[x][y - 1] = 1;
                newBoard[x][y] = 1;
                newBoard[x + 1][y + 1] = 1;
                break;
            case "Manual":
                for (int i = 0; i < newBoard.length; i++) {
                    for (int j = 0; j < newBoard[0].length; j++) {
                        newBoard[i][j] = clickableBoard[i][j];
                    }
                }
                break;
            case"Oscillator":
                newBoard[x - 1][y] = 1;
                newBoard[x][y] = 1;
                newBoard[x + 1][y] = 1;
                break;
            case"Random":
                Random generator = new Random();
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        if (generator.nextInt() % 3 == 0) {
                            newBoard[i][j] = 1;
                        }
                    }
                }
                break;
            default:
                System.out.println("yo mama obama");
        }

        return newBoard;
    }

    public static int [][] generateNextGen(int [][] currentGeneration,int height, int width, boolean PBC)
    {
        int [][]newGeneration = new int[height][width];

        // check PBC

        if(PBC)
        {
            int neighboursCounter = 0;
            // code here
            // top left corner
            neighboursCounter += currentGeneration[height - 1][width -1];
            neighboursCounter += currentGeneration[height - 1][0];
            neighboursCounter += currentGeneration[height - 1][1];
            neighboursCounter += currentGeneration[0][width - 1];
            neighboursCounter += currentGeneration[0][1];
            neighboursCounter += currentGeneration[1][width - 1];
            neighboursCounter += currentGeneration[1][0];
            neighboursCounter += currentGeneration[1][1];
            newGeneration[0][0] = getNewState(newGeneration[0][0],neighboursCounter);
            neighboursCounter = 0;
            // top
            for (int i = 1; i < width - 1 ; i++) {
                neighboursCounter += currentGeneration[height - 1][i - 1];
                neighboursCounter += currentGeneration[height - 1][i];
                neighboursCounter += currentGeneration[height - 1][i + 1];
                neighboursCounter += currentGeneration[0][i - 1];
                neighboursCounter += currentGeneration[0][i + 1];
                neighboursCounter += currentGeneration[1][i - 1];
                neighboursCounter += currentGeneration[1][i];
                neighboursCounter += currentGeneration[1][i + 1];
                newGeneration[0][i] = getNewState(newGeneration[0][i],neighboursCounter);
                neighboursCounter = 0;
            }
            // top right corner
            neighboursCounter += currentGeneration[height - 1][width - 2];
            neighboursCounter += currentGeneration[height - 1][width - 1];
            neighboursCounter += currentGeneration[height - 1][0];
            neighboursCounter += currentGeneration[0][width - 2];
            neighboursCounter += currentGeneration[0][0];
            neighboursCounter += currentGeneration[1][width - 2];
            neighboursCounter += currentGeneration[1][width - 1];
            neighboursCounter += currentGeneration[1][0];
            newGeneration[0][width - 1] = getNewState(newGeneration[0][width - 1],neighboursCounter);
            neighboursCounter = 0;
            // left
            for (int i = 1; i < height - 1; i++) {
                neighboursCounter += currentGeneration[i - 1][width -1];
                neighboursCounter += currentGeneration[i - 1][0];
                neighboursCounter += currentGeneration[i - 1][1];
                neighboursCounter += currentGeneration[i][width - 1];
                neighboursCounter += currentGeneration[i][1];
                neighboursCounter += currentGeneration[i + 1][width - 1];
                neighboursCounter += currentGeneration[i + 1][0];
                neighboursCounter += currentGeneration[i + 1][1];
                newGeneration[i][0] = getNewState(newGeneration[i][0],neighboursCounter);
                neighboursCounter = 0;
            }
            // right
            for (int i = 1; i < height - 1; i++) {
                neighboursCounter += currentGeneration[i - 1][width - 2];
                neighboursCounter += currentGeneration[i - 1][width - 1];
                neighboursCounter += currentGeneration[i - 1][0];
                neighboursCounter += currentGeneration[i][width - 2];
                neighboursCounter += currentGeneration[i][0];
                neighboursCounter += currentGeneration[i + 1][width - 2];
                neighboursCounter += currentGeneration[i + 1][width - 1];
                neighboursCounter += currentGeneration[i + 1][0];
                newGeneration[i][width - 1] = getNewState(newGeneration[i][width - 1],neighboursCounter);
                neighboursCounter = 0;
            }
            // down left corner
            neighboursCounter += currentGeneration[height - 2][width - 1];
            neighboursCounter += currentGeneration[height - 2][0];
            neighboursCounter += currentGeneration[height - 2][1];
            neighboursCounter += currentGeneration[height - 1][width - 1];
            neighboursCounter += currentGeneration[height - 1][1];
            neighboursCounter += currentGeneration[0][width - 1];
            neighboursCounter += currentGeneration[0][0];
            neighboursCounter += currentGeneration[0][1];
            newGeneration[height - 1][0] = getNewState(newGeneration[height - 1][0],neighboursCounter);
            neighboursCounter = 0;
            // down
            for (int i = 1; i < width - 1 ; i++) {
                neighboursCounter += currentGeneration[height - 2][i - 1];
                neighboursCounter += currentGeneration[height - 2][i];
                neighboursCounter += currentGeneration[height - 2][i + 1];
                neighboursCounter += currentGeneration[height - 1][i - 1];
                neighboursCounter += currentGeneration[height - 1][i + 1];
                neighboursCounter += currentGeneration[0][i - 1];
                neighboursCounter += currentGeneration[0][i];
                neighboursCounter += currentGeneration[0][i + 1];
                newGeneration[height - 1][i] = getNewState(newGeneration[height - 1][i],neighboursCounter);
                neighboursCounter = 0;
            }
            // down right corner
            neighboursCounter += currentGeneration[height - 2][width - 2];
            neighboursCounter += currentGeneration[height - 2][width - 1];
            neighboursCounter += currentGeneration[height - 2][0];
            neighboursCounter += currentGeneration[height - 1][width - 2];
            neighboursCounter += currentGeneration[height - 1][0];
            neighboursCounter += currentGeneration[0][width - 2];
            neighboursCounter += currentGeneration[0][width - 1];
            neighboursCounter += currentGeneration[0][0];
            newGeneration[height - 1][width - 1] = getNewState(newGeneration[height - 1][width - 1],neighboursCounter);
            neighboursCounter = 0;
        }
        else
        {
            int neighboursCounter = 0;
            // code here
            // top left corner
            neighboursCounter += currentGeneration[0][1];
            neighboursCounter += currentGeneration[1][0];
            neighboursCounter += currentGeneration[1][1];
            newGeneration[0][0] = getNewState(newGeneration[0][0],neighboursCounter);
            neighboursCounter = 0;
            // top
            for (int i = 1; i < width - 1 ; i++) {
                neighboursCounter += currentGeneration[0][i - 1];
                neighboursCounter += currentGeneration[0][i + 1];
                neighboursCounter += currentGeneration[1][i - 1];
                neighboursCounter += currentGeneration[1][i];
                neighboursCounter += currentGeneration[1][i + 1];
                newGeneration[0][i] = getNewState(newGeneration[0][i],neighboursCounter);
                neighboursCounter = 0;
            }
            // top right corner
            neighboursCounter += currentGeneration[0][width - 2];
            neighboursCounter += currentGeneration[1][width - 2];
            neighboursCounter += currentGeneration[1][width - 1];
            newGeneration[0][width - 1] = getNewState(newGeneration[0][width - 1],neighboursCounter);
            neighboursCounter = 0;
            // left
            for (int i = 1; i < height - 1; i++) {
                neighboursCounter += currentGeneration[i - 1][0];
                neighboursCounter += currentGeneration[i - 1][1];
                neighboursCounter += currentGeneration[i][1];
                neighboursCounter += currentGeneration[i + 1][0];
                neighboursCounter += currentGeneration[i + 1][1];
                newGeneration[i][0] = getNewState(newGeneration[i][0],neighboursCounter);
                neighboursCounter = 0;
            }
            // right
            for (int i = 1; i < height - 1; i++) {
                neighboursCounter += currentGeneration[i - 1][width - 2];
                neighboursCounter += currentGeneration[i - 1][width - 1];
                neighboursCounter += currentGeneration[i][width - 2];
                neighboursCounter += currentGeneration[i + 1][width - 2];
                neighboursCounter += currentGeneration[i + 1][width - 1];
                newGeneration[i][width - 1] = getNewState(newGeneration[i][width - 1],neighboursCounter);
                neighboursCounter = 0;
            }
            // down left corner
            neighboursCounter += currentGeneration[height - 2][0];
            neighboursCounter += currentGeneration[height - 2][1];
            neighboursCounter += currentGeneration[height - 1][1];
            newGeneration[height - 1][0] = getNewState(newGeneration[height - 1][0],neighboursCounter);
            neighboursCounter = 0;
            // down
            for (int i = 1; i < width - 1 ; i++) {
                neighboursCounter += currentGeneration[height - 2][i - 1];
                neighboursCounter += currentGeneration[height - 2][i];
                neighboursCounter += currentGeneration[height - 2][i + 1];
                neighboursCounter += currentGeneration[height - 1][i - 1];
                neighboursCounter += currentGeneration[height - 1][i + 1];
                newGeneration[height - 1][i] = getNewState(newGeneration[height - 1][i],neighboursCounter);
                neighboursCounter = 0;
            }
            // down right corner
            neighboursCounter += currentGeneration[height - 2][width - 2];
            neighboursCounter += currentGeneration[height - 2][width - 1];
            neighboursCounter += currentGeneration[height - 1][width - 2];
            newGeneration[height - 1][width - 1] = getNewState(newGeneration[height - 1][width - 1],neighboursCounter);
            neighboursCounter = 0;
        }

        // generete next gen

        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                int numberOfNeighbours = countNeighbours(currentGeneration,i,j);
                newGeneration[i][j] = getNewState(currentGeneration[i][j],numberOfNeighbours);
            }
        }
        return newGeneration;
    }

    public static int getNewState(int state,int numberOfNeighbours)
    {
        int newState = 0;
        if(state == 1 && (numberOfNeighbours == 2 || numberOfNeighbours == 3)) newState = 1;
        //else if(state == 1 && (numberOfNeighbours < 2 || numberOfNeighbours > 3)) newState = 0;
        else if(state == 0 && numberOfNeighbours == 3) newState = 1;
        //else newState = 0;
        return newState;
    }

    public static void drawGrid(int height, int width,Canvas canvas)
    {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setStroke(Color.DARKMAGENTA);
        //g.setLineWidth(0.05f);
        for (int i = 0; i <= width; i++) {
            int coord = i*8;
            g.strokeLine(coord,0, coord, height*8);
        }
        for (int i = 0; i <= height; i++) {
            int coord = i*8;
            g.strokeLine(0, coord,width*8, coord);
        }
    }

    public void changeState(int state,int y_coord,int x_coord){
        try{
            if(y_coord >= 0 && y_coord <= 89 && x_coord >= 0 && x_coord <= 119)
            {
                if(state == 0)
                {
                    this.clickableBoard[y_coord][x_coord] = 1;
                }
                else this.clickableBoard[y_coord][x_coord] = 0;
            }
            if(y_coord < 0 || y_coord > 89) return;
            if(x_coord < 0 || x_coord > 119) return;
        }catch (IndexOutOfBoundsException e){}

    }
}