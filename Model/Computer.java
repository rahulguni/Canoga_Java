package com.example.canoga.Model;

public class Computer extends Player{
    public Computer() {

    }

    @Override
    public boolean oneDicePossible(Board board) {
        return false;
    }

    @Override
    public String getName() {
        return "Computer";
    }
}
