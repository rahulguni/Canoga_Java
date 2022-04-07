package com.example.canoga.Model;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Game {
    //Player objects to get two players
    private Player player1, player2;
    //a new board object
    private Board newBoard;
    //Round object
    private Round newRound = new Round();
    //Helper object
    private Misc misc = new Misc();

    /*
    Function Name: Game
    Purpose: Constructor of Game class
    Parameters:
            player1:- Player 1 name
            player2:- Player 2 name
            boardSize:- newBoard's board size
    Return Value: None
    Algorithm:
            1. Initialize member variables player1 and player2 according to user choice.
            2. Initialize member variable newBoard after asking the user for board size.
    Assistance Received: None
    */

    public Game(String player1, String player2, int boardSize) {
        this.player1 = new Human(player1);
        if(!player2.equals("Computer")) {
            this.player2 = new Human(player2);
        }
        else {
            this.player2 = new Computer();
        }
        this.newBoard = new Board(boardSize);
    }

    /*
    Function Name: Game
    Purpose: Constructor for Game class, data loaded from text file
    Parameters:
            gameFile: string, name of text file to load game from
    Return Value: None
    Algorithm:
            1. Load all data as string from text file.
            2. Keep track of game data with next line's position in string and put them in a vector.
            3. Remove all empty strings from the vector.
            4. Read remaining board data from text file as strings and vectors to construct a new board object.
    Assistance Received: None
    */

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Game(String filePath) {
        //Get all data from text file as string
        String gameData = "";
        try {
            gameData = new String(Files.readAllBytes(Paths.get(filePath)));

            //Keep track of game data with next line's position in string
            int index = gameData.indexOf("\n");
            ArrayList<String> gameDataArrList = new ArrayList<>();
            while(!gameData.isEmpty()) {
                String currData = gameData.substring(0, index);
                gameData = gameData.substring(index + 1);
                index = gameData.indexOf("\n");
                gameDataArrList.add(currData.trim());
                if(index == -1) {
                    break;
                }
            }

            //Remove all empty lines from arraylist.
            gameDataArrList = misc.removeEmptyLines(gameDataArrList);

            //get first player
            this.player1 = new Human(misc.getPlayerName(gameDataArrList.get(3)));
            this.player1.updateScore(misc.getPlayerScore(gameDataArrList.get(5)));

            //check if second player is human or computer
            if(gameDataArrList.get(0).equals("Computer:")) {
                this.player2 = new Computer();
            }
            else {
                this.player2 = new Human(misc.getPlayerName(gameDataArrList.get(0)));
            }
            this.player2.updateScore(misc.getPlayerScore(gameDataArrList.get(2)));

            boolean humanTurn = misc.compareTurns(gameDataArrList.get(3), gameDataArrList.get(7));
            boolean humanGoesFirst = misc.compareTurns(gameDataArrList.get(3), gameDataArrList.get(6));

            //Get Board Data
            ArrayList<String> boardData = new ArrayList<>();
            for(int i = 1; i <= 4; i += 3) {
                String numbers = misc.fixStringForRead(gameDataArrList.get(i).substring(gameDataArrList.get(i).indexOf(":") + 2));
                int newIndex = numbers.indexOf(" ");
                while(!numbers.isEmpty()) {
                    String currNum = numbers.substring(0, newIndex);
                    numbers = numbers.substring(newIndex + 1);
                    newIndex = numbers.indexOf(" ");
                    boardData.add(currNum);
                    if(newIndex == -1) {
                        break;
                    }
                }
            }

            //Get Dice Rolls
            ArrayList<String> diceInfo = new ArrayList<>();
            for(int i = 9; i < gameDataArrList.size(); i++) {
                String digits = "";
                for(int j = 0; j < gameDataArrList.get(i).length(); j++) {
                    if(gameDataArrList.get(i).charAt(j) != ' ') {
                        digits += String.valueOf(gameDataArrList.get(i).charAt(j));
                    }
                }
                diceInfo.add(digits);
            }

            //Get first Play
            boolean firstPlay = false;
            if(gameDataArrList.get(6).charAt(0) == 'f') {
                firstPlay = true;
            }

            this.newBoard = new Board(boardData, diceInfo, misc.getGameNameFromPath(filePath), humanTurn, humanGoesFirst, firstPlay);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean gameOver() {
        boolean gameOver = false;
        if(newRound.roundWon(newBoard)) {
            gameOver = true;
        }
        return gameOver;
    }

    /*
    Function Name: continueGame
    Purpose: Start another round and return the handicap information.
    Parameters:
            currScore:- Int value of current round score, needed for next round's handicap.
            boardSize:- new board size for new round
    Return Value: String
    Algorithm:
            1. Ask if user wants to play another round.
            2. If yes, reset the board with new board size and a handicap square.
    Assistance Received: None
    */


    public String continueGame(int currScore, int boardSize) {
        //Bool variable to declare whether if player or computer is the handicap
        String handicapInfo = "";
        boolean forPlayer = false;

        //Start another Round
        int squareToCover = misc.getHandicapSquare(boardSize, currScore);

        //Check handicap First
        if(squareToCover > 0) {
            String firstTurn, handicap;

            //If human went first this round, computer is the handicap and vice versa.
            if(this.newBoard.isHumanGoesFirst()) {
                forPlayer = false;
                firstTurn = player1.getName();
                handicap = player2.getName();
            }
            else {
                forPlayer = true;
                firstTurn = player2.getName();
                handicap = player1.getName();
            }

            handicapInfo += "Since " + firstTurn + " went first this round, " + handicap + " has an advantage for next round.\n";
            handicapInfo += handicap + "'s tile " + squareToCover + " will be automatically covered from the beginning of next round.\n";
        }
        else {
            handicapInfo += "Since last round was a draw, there is no handicap for this round.\n";
        }

        //Reset board for new round.
        this.newBoard.resetBoardForHandicap(boardSize, forPlayer, squareToCover);
        handicapInfo += "Press Start to continue Game.\n";
        return handicapInfo;
    }

    /***************************************GETTERS AND SETTERS*****************************************/

    public Board getNewBoard() {
        return newBoard;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Round getNewRound() {
        return newRound;
    }

    public Player getWinner() {
        Player winner = null;
        if(gameOver()) {
            if(player1.getScore() > player2.getScore()) {
                winner = player1;
            }
            else if(player2.getScore() > player1.getScore()) {
                winner = player2;
            }
        }
        return winner;
    }

    /*******************************************************************************************************/
}
