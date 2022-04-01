package com.example.canoga.Model;

import android.util.Log;

import java.util.ArrayList;

public class Board {
    private int boardSize;
    private ArrayList<Boolean> humanBoard, computerBoard;
    private boolean firstPlay, humanTurn, humanGoesFirst;
    private ArrayList<Integer> diceCombinations;
    String savedGameName;

    public Board(int boardSize) {
        this.boardSize = boardSize;
        this.humanBoard = new ArrayList<>(boardSize);
        this.computerBoard = new ArrayList<>(boardSize);
        this.firstPlay = true;
        this.humanTurn = true;
        this.diceCombinations = new ArrayList<>();
        this.savedGameName = "Null00";

       refreshBoard(this.boardSize);

    }

    public Board(ArrayList<String> boardData, ArrayList<String> diceCombos, String gameName, boolean humanTurn, boolean humanGoesFirst, boolean firstPlay) {
        Misc misc = new Misc();
        this.humanTurn = humanTurn;
        this.humanGoesFirst = humanGoesFirst;
        this.savedGameName = gameName;
        this.firstPlay = firstPlay;

        this.boardSize = boardData.size() / 2;

        this.humanBoard = new ArrayList<>(boardSize);
        this.computerBoard = new ArrayList<>(boardSize);
        refreshBoard(this.boardSize);

        if(diceCombos.size() != 0) {
            this.diceCombinations = misc.getDiceComboInt(diceCombos);
        }

        for(int i = 0; i < this.boardSize; i++) {
            if(boardData.get(i).equals("*")) {
                this.computerBoard.set(i, true);
            }
            else {
                this.computerBoard.set(i, false);
            }
        }

        for(int i = this.boardSize; i < boardData.size(); i++) {
            if(boardData.get(i).equals("*")) {
                this.humanBoard.set(i - boardSize, true);
            }
            else {
                this.humanBoard.set(i - boardSize, false);
            }
        }
    }

    public void refreshBoard(int boardSize) {
        for(int i = 0; i < boardSize; i++) {
            this.humanBoard.add(false);
            this.computerBoard.add(false);
        }
    }

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

    public void setFirstPlay(boolean firstPlay) {
        this.firstPlay = firstPlay;
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

    public void changeTile(ArrayList<Boolean> currBoard, int square, boolean cover) {
        currBoard.set(square, cover);
    }

    public void resetBoardForHandicap(int boardSize, boolean forPlayer, int square) {
        this.boardSize = boardSize;
        this.firstPlay = true;

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

    public String getBoardForSave(boolean forPlayer) {
        ArrayList<Boolean> currBoard = (forPlayer) ? this.humanBoard : this.computerBoard;
        String boardInfo = "";

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

    public int getLoadedDice() {
        int firstNum = this.diceCombinations.get(0);
        int secondNum = this.diceCombinations.get(1);
        this.diceCombinations.remove(0);
        this.diceCombinations.remove(0);
        return firstNum + secondNum;
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

