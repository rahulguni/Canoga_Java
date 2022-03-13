package com.example.canoga.Model;

import java.util.ArrayList;

public class Board {
    private int boardSize;
    private ArrayList<Boolean> humanBoard, computerBoard;
    private boolean firstPlay, humanTurn, humanGoesFirst;

    public Board(int boardSize) {
        this.boardSize = boardSize;
        this.humanBoard = new ArrayList<>(boardSize);
        this.computerBoard = new ArrayList<>(boardSize);
        this.firstPlay = true;

        for(int i = 0; i < boardSize; i++) {
            this.humanBoard.add(false);
            this.computerBoard.add(false);
        }
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

    public void changeTile(boolean humanBoard, int square, boolean cover) {
        if(humanBoard) {
            this.humanBoard.set(square, cover);
        }
        else {
            this.computerBoard.set(square, cover);
        }
    }
}

