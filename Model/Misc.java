package com.example.canoga.Model;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Misc {

    public int getDiceRollSum(ArrayList<Integer> diceRolls) {
        int sum = 0;
        for(int i = 0; i < 2; i++) {
            sum += diceRolls.get(i);
        }
        return sum;
    }

    public boolean checkDuplicateCombinations(ArrayList<Integer> currMoves) {
        Set<Integer> set = new HashSet<>(currMoves);
        if(set.size() < currMoves.size()) {
            return false;
        }
        return true;
    }

    public boolean playerIsComputer(Player player) {
        if(player.getName().equals("Computer")) {
            return true;
        }
        return false;
    }

    public String getOpponent(Game game, Player player) {
        String opponent;
        if(game.getPlayer1().getName().equals(player.getName())) {
            opponent = game.getPlayer2().getName();
        }
        else {
            opponent = game.getPlayer1().getName();
        }
        return opponent;
    }

    public Player switchPlayer(Game game, Player player) {
        if(player.equals(game.getPlayer1())) {
            return game.getPlayer2();
        }
        else {
            return game.getPlayer1();
        }
    }

}
