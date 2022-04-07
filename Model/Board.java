package com.example.canoga.Model;

import android.util.Log;

import java.util.ArrayList;

public class Board {
    //Size for board
    private int boardSize;
    //Boolean ArrayLists for both player board, true if tile covered, else false.
    private ArrayList<Boolean> humanBoard, computerBoard;
    //Boolean value that determines if it is the first play of the game.
    private boolean firstPlay;
    //Boolean value that determines if it is player1's turn to throw the next dice.
    boolean humanTurn;
    //Boolean value that determines if it was player1 who went first in a round.
    boolean humanGoesFirst;
    //ArrayList of integers to store all loaded dice combinations from serializer.
    private ArrayList<Integer> diceCombinations = new ArrayList<>();
    //String to give the game a name to save so that the same game cannot be saved with two different names.
    String savedGameName;


    /*
    Function Name: Board
    Purpose: Constructor of Board class called for new game
    Parameters:
            boardSize:- integer to assign a size to the board
    Return Value: None
    Algorithm:
        1. Set the board's size as value passed.
        2. Dynamically allocate an ArrayList of booleans using boardSize.
        3. Set firstPlay to true as this constructor is only called when a round ends.
        4. Set the game's name to default "Null00" and make all values of boolean arrays allocated before to false.
    Assistance Received: None
    */

    public Board(int boardSize) {
        this.boardSize = boardSize;
        this.humanBoard = new ArrayList<>(boardSize);
        this.computerBoard = new ArrayList<>(boardSize);
        this.firstPlay = true;
        this.humanTurn = true;
        this.savedGameName = "Null00";

       refreshBoard(this.boardSize);

    }
    /*
    Function Name: Board
    Purpose: Constructor of Board class called for load game.
    Parameters:
            boardData:- a vector of string that contains the board's state for both players loaded from file
            diceCombos:- a vector of string that contains all dice rolls loaded from the file
            humanTurn:- bool value that determines if nextTurn is for player1.
            humanGoesFirst:- bool value that determines if player1 went first.
            firstPlay:- bool value that determines if this is the first play of the round.
    Return Value: None
    Algorithm:
            1. Set board's gameName, humanTurn, humanGoesFirst and firstPlay member variables to the value passed.
            2. Copy values from diceCombos vector to Board's diceCombinations after converting strings to integer values.
            3. Dynamically allocate two arrays and fill their state according to boardData vector passed.
    Assistance Received: None
     */

    public Board(ArrayList<String> boardData, ArrayList<String> diceCombos, String gameName, boolean humanTurn, boolean humanGoesFirst, boolean firstPlay) {
        Misc misc = new Misc();
        this.humanTurn = humanTurn;
        this.humanGoesFirst = humanGoesFirst;
        this.savedGameName = gameName;
        this.firstPlay = firstPlay;

        //Board Size will be half of string vector size.
        this.boardSize = boardData.size() / 2;

        //allocate new boards according to size
        this.humanBoard = new ArrayList<>(boardSize);
        this.computerBoard = new ArrayList<>(boardSize);
        refreshBoard(this.boardSize);

        if(diceCombos.size() != 0) {
            this.diceCombinations = misc.getDiceComboInt(diceCombos);
        }

        //Fill computer board, starts from 0 to board's size - 1
        for(int i = 0; i < this.boardSize; i++) {
            if(boardData.get(i).equals("*")) {
                this.computerBoard.set(i, true);
            }
            else {
                this.computerBoard.set(i, false);
            }
        }

        //Fill human board, starts from boardSize to end of string vector
        for(int i = this.boardSize; i < boardData.size(); i++) {
            if(boardData.get(i).equals("*")) {
                this.humanBoard.set(i - boardSize, true);
            }
            else {
                this.humanBoard.set(i - boardSize, false);
            }
        }

    }

    /*
    Function Name: refreshBoard
    Purpose: Sets all tiles to false for new game
    Parameters:
        boardSize:- size of new board
    Return Value: None
    Algorithm:
        1. Loop over the ArrayLists and set all elements to false.
    Assistance Received: None
 */

    public void refreshBoard(int boardSize) {
        for(int i = 0; i < boardSize; i++) {
            this.humanBoard.add(false);
            this.computerBoard.add(false);
        }
    }

    /***************************************GETTERS AND SETTERS*****************************************/

    public ArrayList<Integer> getDiceCombinations() {
        return diceCombinations;
    }

    public String getSavedGameName() {
        return savedGameName;
    }

    public void setSavedGameName(String savedGameName) {
        this.savedGameName = savedGameName;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public ArrayList<Boolean> getHumanBoard() {
        return humanBoard;
    }

    public ArrayList<Boolean> getComputerBoard() {
        return computerBoard;
    }

    public boolean isFirstPlay() {
        return firstPlay;
    }

    public boolean isHumanTurn() {
        return humanTurn;
    }

    public void setHumanTurn(boolean humanTurn) {
        this.humanTurn = humanTurn;
    }

    public boolean isHumanGoesFirst() {
        return humanGoesFirst;
    }

    public void setHumanGoesFirst(boolean humanGoesFirst) {
        this.humanGoesFirst = humanGoesFirst;
    }

    /*******************************************************************************************************/

    /*
    Function Name: changeTurn
    Purpose: To change playerTurn variable
    Parameters: None
    Return Value: None
    Algorithm:
            1. If playerTurn is true, make it false and vice versa.
            2. If firstPlay is true, make it false as there is only one first turn in a round.
    Assistance Received: None
    */

    public void changeTurn() {
        if(this.humanTurn) {
            this.humanTurn = false;
        }
        else {
            this.humanTurn = true;
        }

        // Change firstPlay to false after the first move
        if(this.firstPlay) {
            this.firstPlay = false;
        }
    }

    /*
    Function Name: changeTile
    Purpose: Change a board's tile
    Parameters:
            1. currBoard:- a boolean arraylist that gets specified boolean arraylist of board (playerBoard or computerBoard).
            2. square:- an integer that determines which tile to change value in the array.
            3. cover:- a boolean value that determines whether to cover(true) or uncover(false) the tiles in boolean array.
    Return Value: None
    Algorithm: None
    Assistance Received: None
    */

    public void changeTile(ArrayList<Boolean> currBoard, int square, boolean cover) {
        currBoard.set(square, cover);
    }

    /*
    Function Name: resetBoardForHandicap
    Purpose: Re-loads the member variables for a new round with handicap
    Parameters:
            boardSize:- a vector of string that contains the board's state for both players loaded from file
            forPlayer:- boolean value true if player1 is handicap, else false
            square:- int value that determines which square to cover
    Return Value: None
    Algorithm:
            1. Delete previous dynamic boolean arrays to prevent the case of lost objects.
            2. Set the board's size as value passed.
            3. Allocate new arrays according to boardSize.
            4. If a handicap square exists (square > 0), cover the square in player1/player2 board according to forPlayer
    Assistance Received: None
    */

    public void resetBoardForHandicap(int boardSize, boolean forPlayer, int square) {
        this.boardSize = boardSize;
        this.firstPlay = true;

        //Add new tiles according to board size
        for(int i = 0; i < boardSize; i++) {
            if(i >= this.humanBoard.size()) {
                this.humanBoard.add(false);
                this.computerBoard.add(false);
            }
        }

        //Remove tiles according to board size
        if(boardSize < this.humanBoard.size()) {
            for(int i = boardSize; i < this.humanBoard.size(); i++) {
                this.humanBoard.remove(i);
                this.computerBoard.remove(i);
            }
        }

        for(int i = 0; i < boardSize; i++) {
            this.humanBoard.set(i, false);
            this.computerBoard.set(i, false);
        }

        if(square > 0) {
            if(forPlayer) {
                this.humanBoard.set(square - 1, true);
            }
            else {
                this.computerBoard.set(square - 1, true);
            }
        }
    }

    /*
    Function Name: getBoardForSave
    Purpose: Get the state of board for save in the form of string
    Parameters:
            forPlayer:- boolean value true if board is for player1, else false
    Return Value: String that contains the board's state for either player.
    Algorithm:
            1. If the value is true at an index in the boolean array (playerBoard and computerBoard), string is * else number.
            2. Append to a local string variable.
    Assistance Received: None
    */

    public String getBoardForSave(boolean forPlayer) {
        //An arraylist of booleans, according to forPlayer parameter passed.
        ArrayList<Boolean> currBoard = (forPlayer) ? this.humanBoard : this.computerBoard;

        //Append all board data to this string and return later
        String boardInfo = "";

        //Append to the string looping through the boolean array
        for(int i = 0; i < this.boardSize; i++) {
            if(currBoard.get(i)) {
                boardInfo += "* ";
            }
            else {
                boardInfo += (i + 1) + " ";
            }
        }

        return boardInfo;
    }

    /*
    Function Name: getLoadedDice
    Purpose: Get first integer dice from diceCombinations
    Parameters: None
    Return Value: Integer value of dice
    Algorithm:
            1. Record the first element of diceCombinations, delete it from the arrayList and return it.
    Assistance Received: None
    */

    public int getLoadedDice() {
        int firstNum = this.diceCombinations.get(0);
        this.diceCombinations.remove(0);
        return firstNum;
    }

    @Override
    public String toString() {
        String boardData = "Player Board: ";
        for(int i = 0; i < this.boardSize; i++) {
            boardData += String.valueOf(i) + String.valueOf(this.humanBoard.get(i));
        }
        boardData += "  Computer Board: ";
        for(int i = 0; i < this.boardSize; i++) {
            boardData += String.valueOf(i) + String.valueOf(this.computerBoard.get(i));
        }
        return boardData;
    }
}

