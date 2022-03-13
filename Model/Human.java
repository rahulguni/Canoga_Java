package com.example.canoga.Model;

public class Human extends Player{
    private String humanName;

    public Human(String humanName) {
        this.humanName = humanName;
    }

    @Override
    public boolean oneDicePossible(Board board) {
        return false;
    }

    @Override
    public String getName() {
        return this.humanName;
    }
}
