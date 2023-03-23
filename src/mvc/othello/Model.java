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
  private String[][] board;

  // Model's data variables

  /**
   * Model constructor: Create the data representation of the program
   * @param messages Messaging class instantiated by the Controller for 
   *   local messages between Model, View, and controller
   */
  public Model(Messenger messages) {
    mvcMessaging = messages;
    this.board = new String[8][8];
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
    for(int row=0; row<this.board.length; row++) {
      for (int col=0; col<this.board[0].length; col++) {
        this.board[row][col] = "";
      }
    }
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
      



      // If square is blank...
        //String winner = this.isWinner();
        if(!this.gameOver) {
            if (this.board[row][col].equals("")) {
            // ... then set X or O depending on whose move it is
                if (this.whoseMove) {
                    this.board[row][col] = "X";
                    this.whoseMove = false;
                    this.mvcMessaging.notify("Black");
                } else {
                    this.board[row][col] = "O";
                    this.whoseMove = true;
                    this.mvcMessaging.notify("White");
                }
            }
        }
        // Send the boardChange message along with the new board 
        this.mvcMessaging.notify("boardChange", this.board);
        String winner = this.isWinner(); 
    
    // newGame message handler
        
    

        
    } else if (messageName.equals("newGame")) {
      // Reset the app state
      this.newGame();
      // Send the boardChange message along with the new board 
      this.mvcMessaging.notify("boardChange", this.board);
      this.mvcMessaging.notify("newGame");
    }

  }
  
   private String isWinner() {
    for (int i = 0; i < 3; i++) {
        if (board[i][0].equals(board[i][1]) && board[i][0].equals(board[i][2]) && !board[i][0].equals("")) {
            this.mvcMessaging.notify("gameOver");
            gameOver = true;
            return board[i][0];
            }
        }
         for (int i = 0; i < 3; i++) {
            if (board[0][i].equals(board[1][i]) && board[0][i].equals(board[2][i]) && !board[0][i].equals("")) {
                this.mvcMessaging.notify("gameOver");
                gameOver = true;
                return board[0][i];
            }
        }

        if (board[0][0].equals(board[1][1]) && board[0][0].equals(board[2][2]) && !board[0][0].equals("")) {
            this.mvcMessaging.notify("gameOver");
            gameOver = true;
            return board[0][0];
        }
        if (board[0][2].equals(board[1][1]) && board[0][2].equals(board[2][0]) && !board[0][2].equals("")) {
            this.mvcMessaging.notify("gameOver");
            gameOver = true;
            return board[0][2];
        }

        if (!board[0][0].equals("") && !board[0][1].equals("") && !board[0][2].equals("") && !board[1][0].equals("") && !board[1][1].equals("")
                    && !board[1][2].equals("") && !board[2][0].equals("") && !board[2][1].equals("") && !board[2][2].equals("")) {
            this.mvcMessaging.notify("Tie");
            gameOver = true;
        }
      return "";  
    }
}
