package com.example.canoga.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Round {
    //Winner of current round
    String winner;

    Round() {

    }

    public String getWinner() {
        return winner;
    }

    /*
    Function Name: roundWon
    Purpose: Check if either board satisfies the condition for the round to be over
    Parameters:
           board:- Board object passed by value to check the two boolean arrays.
    Return Value: Boolean value, true if round is over
    Algorithm:
            1. Check if it is the first play of the round. If true, end round by only covering player's own tiles.
            2. Else, check for both covering and uncovering.
    Assistance Received: None
    */

    public boolean roundWon(Board board) {
        boolean won;

        if(board.isFirstPlay()) {
            //check for human turn
            if(board.isHumanTurn()) {
                won = checkBoard(board.getHumanBoard(), true);
            }
            //check for computer turn
            else {
                won = checkBoard(board.getComputerBoard(), true);
            }
        }
        else {
            //check for human turn
            if(board.isHumanTurn()) {
                won = checkBoard(board.getHumanBoard(), true) || checkBoard(board.getComputerBoard(), false);
            }
            //check for computer turn
            else {
                won = checkBoard(board.getComputerBoard(), true) || checkBoard(board.getHumanBoard(), false);
            }
        }

        return won;
    }

    /*
    Function Name: updateScore
    Purpose: update score for player that won the round.
    Parameters:
            board:- board object passed by value to access its variables and properties.
            player1:- player object (Human)
            player2:- player object (Human/Computer)
    Return Value: score of current round.
    Algorithm:
            1. If player1's turn, get current score and add to player1's score, else same step for player2.
    Assistance Received: None
    */

    public int updateScore(Board board, Player player1, Player player2) {
        //Integer to record current game's score
        int currScore;
        //String to record winner of current round's winner.
        String winner;

        if(board.isHumanTurn()) {
            //human won, check if won by covering or uncovering
            currScore = getScore(board.getBoardSize(), board.getHumanBoard(), board.getComputerBoard());
            player1.updateScore(currScore);
            winner = player1.getName();
        }
        else {
            //computer won, check if won by covering or uncovering
            currScore = getScore(board.getBoardSize(), board.getComputerBoard(), board.getHumanBoard());
            player2.updateScore(currScore);
            //Make currScore negative to know who won the match
            currScore *= -1;
            winner = player2.getName();
        }
        this.winner = winner;
        return currScore;
    }

    /*
    Function Name: getScore
    Purpose: Get score for current round
    Parameters:
            boardSize:- board object's boardSize variable
            myBoard:- boolean arraylist
            oppBoard:- boolean arraylist
    Return Value: None
    Algorithm:
            1. Find out if the round was won by covering own board's squares or by uncovering opponent's squares.
            2. Return score according to the rules.
    Assistance Received: None
    */

    public int getScore(int boardSize, ArrayList<Boolean> myBoard, ArrayList<Boolean> oppBoard) {
        //Integer value to return later
        int currScore = 0;
        //To record whether the game was won by covering own board or by uncovering opponent's board
        boolean didCover = true;

        for(int i = 0; i < boardSize; i++) {
            if(myBoard.get(i) == false) {
                didCover = false;
                break;
            }
        }

        //Find out scores according to rules.
        if(didCover) {
            for(int i = 0; i < boardSize; i++) {
                if(oppBoard.get(i) == false) {
                    currScore += i + 1;
                }
            }
        }
        else {
            for(int i = 0; i < boardSize; i++) {
                if(myBoard.get(i) == true) {
                    currScore += i + 1;
                }
            }
        }

        return currScore;
    }

    /*
    Function Name: checkBoard
    Purpose: Check if an array of boolean values (humanBoard/computerBoard) in Board object qualifies for the round to be over.
    Parameters:
            board:- boolean arraylist
            boardSize:- int value, board's boardSize
            cover:- boolean, checks board for cover(true) or uncover(false).
    Return Value: Boolean value, true if board qualifies for round to be over.
    Algorithm:
            1. Loop over passed array and return false if any one of the elements are not equal to value of cover.
            2. If the loop exits, return true.
    Assistance Received: None
    */

    private boolean checkBoard(ArrayList<Boolean> board, boolean cover) {
        for(int i = 0; i < board.size(); i++) {
            if(board.get(i) != cover) {
                return false;
            }
        }
        return true;
    }

}
