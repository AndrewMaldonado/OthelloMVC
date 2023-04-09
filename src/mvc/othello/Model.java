package mvc.othello;

import com.mrjaffesclass.apcs.messenger.*;

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

  // Model's data vControllerariables

  /**
   * Model constructor: Create the data representation of the program
   * @param messages Messaging class instantiated by the Controller for 
   *   local messages between Model, View, and controller
   */
  public Model(Messenger messages) {
    mvcMessaging = messages;
    this.board = new int[8][8];
  }
  
  /**
   * Initialize the model here and subscribe to any required messages
   */
  public void init() {
    this.newGame();
    this.mvcMessaging.subscribe("playerMove", this);
    this.mvcMessaging.subscribe("newGame", this);
    this.mvcMessaging.subscribe("gameOver", this);
    this.mvcMessaging.subscribe("Tie", this);
    whoseMove = false;
    gameOver = false;

  }
  
    public void newGame() {
        this.board = new int[8][8];
        
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
      



     
        
        // Send the boardChange message along with the new board 
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
  
    
    
     public static int getSquare(int[][] board, Position position) {
        return board[position.getRow()][position.getCol()];
    }
     
     public static int[][] setSquare(int player, int[][] board, Position position) {
        board[position.getRow()][position.getCol()] = player;
        return board;
    }
     
     private static boolean makeStep(int player, int[][] board, Position position, Position direction, int count) {
        Position newPosition = position.translate(direction);
        int oplayer = ((player == 1) ? 2 : 1);
        if (newPosition.isOffBoard()) {
            return false;
        } else if (getSquare(board, newPosition) == oplayer) {
            boolean valid = makeStep(player, board, newPosition, direction, count+1);
            if (valid) {
                setSquare(player, board, newPosition);
            }
            return valid;
        } else if (getSquare(board, newPosition) == player) {
            return count > 0;
        } else {
            return false;
        }
    }
     
    public static int[][] makeMove(int playerToMove, int[][] board, Position positionToMove) {
        for (String direction : Directions.getDirections()) {
            Position directionVector = Directions.getVector(direction);
            if (makeStep(playerToMove, board, positionToMove, directionVector, 0)) {
                board = setSquare(playerToMove, board, positionToMove);
            }
        }
        return board;
    }

     private static boolean step(int player, int[][] board, Position position, Position direction, int count) {
         Position newPosition = position.translate(direction);
         int newplayer = ((player == 1) ? 2 : 1);
         if (newPosition.isOffBoard()) {
            // if off board then illegal
            return false;
         } else if ((getSquare(board, newPosition) == 0) && (count == 0)) {
            // if empty and adjacent to position then illegal
            return false;
         } else if (getSquare(board, newPosition) == newplayer && getSquare(board, newPosition) != 0) {
            // if space has opposing player then move to space in that direction
            return step(player, board, newPosition, direction, count+1);
         } else if (getSquare(board, newPosition) == player) {
            // if space has player and moved more than 1 space its legal
            return count > 0;
         } else {
            return false;
        }
     }
     
    
     public static boolean isLegalMove(int[][] board, int player, Position positionToCheck) {
        // check if empty, if not illegal
        if (getSquare(board, positionToCheck) != 0)
            return false;
        // check directions to see if its legal
        for (String direction : Directions.getDirections()) {
            Position directionVector = Directions.getVector(direction);
            if (step(player, board, positionToCheck, directionVector, 0)) {
                return true;
            }
        }
        return false;
    }
    
    
    
    
}