package com.example.canoga.Model;

import java.util.ArrayList;

public class Computer extends Player{
    public Computer() {

    }

    @Override
    public boolean oneDicePossible(Board board) {
        return false;
    }

    @Override
    public boolean checkForMove(Board board, int diceSum) {
        return false;
    }

    @Override
    public int makeMove(Board board, boolean coverSelf, int diceSum, int tile) {
        return 1;
    }

    @Override
    public int coverSelf(Board board, int diceSum) {
        return 0;
    }

    @Override
    public int uncoverOpp(Board board, int diceSum) {
        return 0;
    }

    @Override
    public String getName() {
        return "Computer";
    }
}
