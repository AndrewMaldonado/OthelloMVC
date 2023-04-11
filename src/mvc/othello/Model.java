package mvc.othello;

import com.mrjaffesclass.apcs.messenger.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The model represents the data that the app uses.
 * @author Roger Jaffe
 * @version 1.0
 */
public class Model implements MessageHandler {

  // Messaging system for the MVC
  private final Messenger mvcMessaging;
  private boolean whoseMove;
  private boolean gameOver;
  int[][] board;
  int[][] legalMoves;

  // Model's data vControllerariables

  /**
   * Model constructor: Create the data representation of the program
   * @param messages Messaging class instantiated by the Controller for 
   *   local messages between Model, View, and controller
   */
  public Model(Messenger messages) {
    mvcMessaging = messages;
    this.board = new int[8][8];
    this.legalMoves = new int[8][8];
  }
  
  /**
   * Initialize the model here and subscribe to any required messages
   */
  public void init() {
    this.mvcMessaging.subscribe("playerMove", this);
    this.mvcMessaging.subscribe("newGame", this);
    this.mvcMessaging.subscribe("gameOver", this);
    this.mvcMessaging.subscribe("Tie", this);
    whoseMove = false;
    gameOver = false;

  }
  
    public void newGame() {
       
        
        this.whoseMove = false;
        this.gameOver = false;
  }
    
     
  
  @Override
  public void messageHandler(String messageName, Object messagePayload) {
       // Display the message to the console for debugging
    if (messagePayload != null) {
      System.out.println("MSG: received by model: "+messageName+" | "+messagePayload.toString());
    } else {
      System.out.println("MSG: received by model: "+messageName+" | No data sent");
    }
   
    // playerMove message handler
    if (messageName.equals("playerMove")) {
      // Get the position string and convert to row and col
      String position = (String)messagePayload;
      Integer row = Integer.valueOf(position.substring(0,1));
      Integer col = Integer.valueOf(position.substring(1,2));
      
      //0 means empty, 1 is black, and -1 is white



     
        
        // Send the boardChange message along with the new board 
        this.mvcMessaging.notify("boardChange", this.board);
        this.mvcMessaging.notify("pieces", pieces());
        this.mvcMessaging.notify("boardChange", this.board);
        //String winner = this.isWinner(); 
    
    // newGame message handler
        
    

        
    } else if (messageName.equals("newGame")) {
      // Reset the app state
      this.newGame();
      // Send the boardChange message along with the new board 
      this.mvcMessaging.notify("boardChange", this.board);
      this.mvcMessaging.notify("newGame");
    }
  }
  
    public boolean isLegalMove(int row, int col) {
        int[] position = new int[2];
        HashMap<Integer, int[]> place  = new HashMap<>();
        if(this.board[row][col] != 0) {
            return false;
        }
        int turn = this.whoseMove ? -1 : 1;
        for(int[] directions : Directions.directions) {
            position[0] = row;
            position[1] = col;
            int i = 0;
            vector(directions, position);
            while(inBound(position) && getSquare(position) == turn * -1) {
                place.put(getSquare(position), directions);
                vector(directions, position);
                i ++;
            }
            if(inBound(position)) {
                place.put(getSquare(position), directions);
            }
        }
        return false;
    }
  
    private void updateBoard(int row, int col) {
        int[] position = new int[2];
        HashMap<Integer, int[]> place  = new HashMap<>();
        int square = this.board[row][col];
        for(int[] direction : Directions.directions) {
            position[0] = row;
            position[1] = col;
            vector(direction, position);
            while(inBound(position) && getSquare(position) == square * -1) {
                int[] pos = {position[0], position[1]};
                place.put(getSquare(position), pos);
                vector(direction, position);
            }
            if(inBound(position) && getSquare(position) != 0) {
                updateSquare(position, new int[] {row, col}, direction);
            }
        }
    }
    
    private void updateSquare(int[] end, int[] start, int[] directions ) {
        vector(directions, start);
        while(start[0] != end[0] || start[1] != end[1]) {
            this.board[start[0]][start[1]] = this.board[start[0]][start[1]] * -1;
            vector(directions, start);
        }
    }
    
    private int[] pieces() {
        int black = 0;
        int white = 0;
        for(int[] i : this.board) {
            for(int num : i) {
                if(num == 1) {
                    black ++;
                }
                if(num == -1) {
                    white ++;
                }
           }
       }
        return new int[] {black, white};
    }
    
    private void makeMove(int row, int col) {
        this.board[row][col] = (this.whoseMove) ? 1 : -1;
        updateBoard(row, col);
        //go through whole board and check using legalmove, if legal add to legalMoves[][]
        
        
        
       
        

    }
    
    
    public void vector(int[] vector, int[] position) {
        position[0] += vector[0];
        position[1] += vector[1];
    }
    
    public boolean inBound(int[] position) {
        return (position[0] >= 0 && position[0] < 8) && (position[1] >= 0 && position[1] < 8);
    }
     
    private int getSquare(int[] position) {
        return this.board[position[0]][position[1]];
    }
  
 
    
    
    
}
    
    
