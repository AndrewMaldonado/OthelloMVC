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
  private int gameOver;
  int[][] board;
  boolean[][] legalMoves;

  // Model's data vControllerariables

  /**
   * Model constructor: Create the data representation of the program
   * @param messages Messaging class instantiated by the Controller for 
   *   local messages between Model, View, and controller
   */
  public Model(Messenger messages) {
    mvcMessaging = messages;
    this.board = new int[8][8];
    this.legalMoves = new boolean[8][8];
  }
  
  /**
   * Initialize the model here and subscribe to any required messages
   */
  public void init() {
    this.newGame();
    this.mvcMessaging.subscribe("playerMove", this);
    this.mvcMessaging.subscribe("newGame", this);
  }
  
    //reset board and for new game
    public void newGame() {
        //clear board
        for(int[] board : this.board) {
            for(int i = 0; i < board.length; i++) {
                board[i] = 0;
            }
        }
        //clear legal moves
        for(boolean[] board : this.legalMoves) {
            for(int i = 0; i < board.length; i++) {
                board[i] = false;
            }
        }
        //set up board
        this.whoseMove = true;
        this.board[3][3] = -1;
        this.board[4][4] = -1;
        this.board[3][4] = 1;
        this.board[4][3] = 1;
        this.legalMoves[2][3] = true;
        this.legalMoves[3][2] = true;
        this.legalMoves[4][5] = true;
        this.legalMoves[5][4] = true;
        this.mvcMessaging.notify("boardChange", this.board);
        this.mvcMessaging.notify("pieces", pieces());
        this.mvcMessaging.notify("legalMoves", this.legalMoves);
        this.mvcMessaging.notify("whoseMove", this.whoseMove);
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
      
        //make chosen move
        if(this.board[row][col] == 0 && legalMoves[row][col]) {
            this.makeMove(row, col);
        }
        //check if game is over
        if(this.gameOver() == true) {
            this.mvcMessaging.notify("gameOver", this.gameOver);
        }

        // Send the boardChange message along with the new board 
        this.mvcMessaging.notify("boardChange", this.board);
        this.mvcMessaging.notify("legalMoves", this.legalMoves);
        this.mvcMessaging.notify("pieces", this.pieces());
        
    
    // newGame message handler
    } else if (messageName.equals("newGame")) {
      //reset board
      this.newGame();
    }
  }
    //check if the square is a legal move
    public boolean isLegalMove(int row, int col) {
        //create an array to hold the position
        int[] position = new int[2];
        //create a hashmap to hold the possible moves
        HashMap<Integer, int[]> place = new HashMap<>();
        //if the square is not empty return false
        if(this.board[row][col] != 0) {
            return false;
        }
        //get whoseMove
        int turn = this.whoseMove ? -1 : 1;
        //go through all directions and check if the move is legal
        for(int[] directions : Directions.directions) {
            position[0] = row;
            position[1] = col;
            place = new HashMap<>();
            vector(directions, position);
            //check if any pieces to flip with a move and store them
            while(inBound(position) && getSquare(position) == turn * -1) {
                place.put(getSquare(position), directions);
                vector(directions, position);
            }
            //check if the move is in bounds
            if(inBound(position)) {
                place.put(getSquare(position), directions);
            }
            //check if the move is legal
            if(place.containsKey(turn) && place.containsKey(turn * -1)) {
                return true;
            }
        }
        return false;
    }
  
    private void updateBoard(int row, int col) {
        //create an array to hold the position
        int[] position = new int[2];
        HashMap<Integer, int[]> place = new HashMap<>();
        int square = this.board[row][col];
        //go through all directions and check if the move is legal
        for(int[] direction : Directions.directions) {
            position[0] = row;
            position[1] = col;
            place = new HashMap<>();
            vector(direction, position);
            //check for the opposite color
            while(inBound(position) && getSquare(position) == square * -1) {
                //int[] pos = {position[0], position[1]};
                place.put(getSquare(position), new int[] {position[0], position[1]});
                vector(direction, position);
            }
            //update squares
            if(inBound(position) && getSquare(position) != 0) {
                //int[] newPosition = {position[row], position[col]};
                updateSquare(position, new int[] {row, col}, direction);
            }
        }
    }
    
    private void updateSquare(int[] end, int[] start, int[] directions ) {
        vector(directions, start);
        //flip game pieces
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
        //get whoseMove
        this.board[row][col] = (this.whoseMove) ? 1 : -1;
        //update the board
        updateBoard(row, col);
        boolean isLegal = false;
        //set legal moves board to have all legal moves
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                legalMoves[i][j] = isLegalMove(i, j);
            }
        }
        //check the move is a legal move
        for(boolean[] legal : this.legalMoves) {
            for(boolean position : legal) {
                if(position) {
                    isLegal = true;
                }
            }
        }
        //if there are no legal moves change turn
        if(!isLegal) {
            whoseMove = !whoseMove;
            for(int i = 0; i < 8; i++) {
                for(int j = 0; j < 8; j++) {
                    legalMoves[i][j] = isLegalMove(i, j);
                }
            }
        }

        gameOver();
        this.whoseMove = !this.whoseMove;
        this.mvcMessaging.notify("pieces", pieces());
        this.mvcMessaging.notify("boardChange", this.board);
        this.mvcMessaging.notify("legalMoves", this.legalMoves);
        this.mvcMessaging.notify("pieces", pieces());
        this.mvcMessaging.notify("whoseMove", this.whoseMove);
    }

    private boolean gameOver() {
        //check if any squares are empty
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(this.board[i][j] == 0) {
                    return false;
                }
            }
        }
        //handle who wins by comparing the number of pieces
        int[] pieces = pieces();
        if(pieces[0] > pieces[1]) {
            this.gameOver = 1;
        } else if(pieces[0] < pieces[1]) {
            this.gameOver = -1;
        } else {
            this.gameOver = 0;
        }
        return true;
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
    
    
