package com.example.canoga.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Round {
    String winner;

    Round() {

    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }


    public boolean roundWon(Board board) {
        boolean won;

        if(board.isFirstPlay()) {
            if(board.isHumanTurn()) {
                won = checkBoard(board.getHumanBoard(), true);
            }
            else {
                won = checkBoard(board.getComputerBoard(), true);
            }
        }
        else {
            if(board.isHumanTurn()) {
                won = checkBoard(board.getHumanBoard(), true) || checkBoard(board.getComputerBoard(), false);
            }
            else {
                won = checkBoard(board.getComputerBoard(), true) || checkBoard(board.getHumanBoard(), false);
            }
        }

        return won;
    }

    public int updateScore(Board board, Player player1, Player player2) {
        int currScore = 0;
        String winner;
        if(board.isHumanTurn()) {
            currScore = getScore(board.getBoardSize(), board.getHumanBoard(), board.getComputerBoard());
            player1.updateScore(currScore);
            winner = player1.getName();
        }
        else {
            currScore = getScore(board.getBoardSize(), board.getComputerBoard(), board.getHumanBoard());
            player2.updateScore(currScore);
            currScore *= -1;
            winner = player2.getName();
        }
        this.winner = winner;
        return currScore;
    }

    public int getScore(int boardSize, ArrayList<Boolean> myBoard, ArrayList<Boolean> oppBoard) {
        int currScore = 0;
        boolean didCover = true;

        for(int i = 0; i < boardSize; i++) {
            if(myBoard.get(i) == false) {
                didCover = false;
                break;
            }
        }

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

    private boolean checkBoard(ArrayList<Boolean> board, boolean cover) {
        for(int i = 0; i < board.size(); i++) {
            if(board.get(i) != cover) {
                return false;
            }
        }
        return true;
    }

}
