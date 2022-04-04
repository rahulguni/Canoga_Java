package com.example.canoga.Model;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import java.io.File;
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

    public int calculateScore(ArrayList<Boolean> board, boolean cover) {
        int boardScore = 0;

        for(int i = 0; i < board.size(); i++) {
            if(board.get(i) == !cover) {
                boardScore += 1;
            }
        }

        return boardScore;
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

    public Player getCurrentPlayer(Game game, Board board) {
        if(board.isHumanTurn()) {
            return game.getPlayer1();
        }
        else {
            return game.getPlayer2();
        }
    }

    public String getMoves(ArrayList<ArrayList<Integer>> allMoves) {
        String allMovesStr = "";
        for(int i = 0; i < allMoves.size(); i++) {
            allMovesStr += "(";
            for(int j = 0; j < allMoves.get(i).size(); j++) {
                if(j  == (allMoves.get(i).size() - 1)) {
                    allMovesStr += String.valueOf(allMoves.get(i).get(j));
                }
                else {
                    allMovesStr += String.valueOf(allMoves.get(i).get(j)) + ", ";
                }
            }
            allMovesStr += ")\n";
        }
        return allMovesStr;
    }

    public int getHandicapSquare(int boardSize, int number) {
        int square = 0;

        int currNum = Math.abs(number);

        int temp;
        while(currNum > 0) {
            temp = currNum % 10;
            square += temp;
            currNum /= 10;
        }

        if(square > boardSize) {
            currNum = Math.abs(square);
            square = 0;
            int tem;
            while(currNum > 0) {
                tem = currNum % 10;
                square += tem;
                currNum /= 10;
            }
        }

        return square;
    }

    public ArrayList<Integer> getBestCombination(ArrayList<ArrayList<Integer>> allMoves) {
        ArrayList<Integer> bestCombination = new ArrayList<>();
        int smallestVecSize = 5;
        int largestTile = 0;

        for(int i = 0; i < allMoves.size(); i++) {
            if(allMoves.get(i).size() <= smallestVecSize) {
                smallestVecSize = allMoves.get(i).size();
                if(allMoves.get(i).get(smallestVecSize - 1) > largestTile) {
                    largestTile = allMoves.get(i).get(smallestVecSize - 1);
                    bestCombination.clear();
                    bestCombination = allMoves.get(i);
                }
            }
        }
        return bestCombination;
    }

    public ArrayList<Integer> getDiceComboInt(ArrayList<String> diceComboString) {
        ArrayList<Integer> dice = new ArrayList<>();

        for(int i = 0; i < diceComboString.size(); i++) {
            for(int j = 0; j < diceComboString.get(i).length(); j++) {
                dice.add(Integer.parseInt(String.valueOf(diceComboString.get(i).charAt(j))));
            }
            if(diceComboString.get(i).length() < 2) {
                dice.add(0);
            }
        }

        return dice;
    }

    public String extractGameName(String gameName) {
        return gameName.substring(0, gameName.length() - 4);
    }

    public ArrayList<String> gameNames(Context context) {
        Log.d("TAG", context.getFilesDir().toString());
        ArrayList<String> allFiles = new ArrayList<>();
        File file = new File(String.valueOf(context.getFilesDir()) + "/savedGames");
        String[] savedFiles;
        savedFiles = file.list();

        if(savedFiles != null) {
            for(int i = 0; i < savedFiles.length; i++) {
                allFiles.add(this.extractGameName(savedFiles[i]));
            }
        }

        return allFiles;
    }

    public boolean checkPlayerName(String name) {
        if(name.equals("Computer") || name.equals("computer")) {
            return false;
        }
        return true;
    }

    public ArrayList<String> removeEmptyLines(ArrayList<String> gameDataVec) {
        for(int i = gameDataVec.size() - 1; i >=0; i--) {
            if(gameDataVec.get(i).equals(" ") || gameDataVec.get(i).equals("\n") || gameDataVec.get(i).length() < 1) {
                gameDataVec.remove(i);
            }
        }
        return gameDataVec;
    }

    public String fixStringForRead(String data) {
        return data + " ";
    }

    public String getPlayerName(String name) {
        return name.substring(0, name.length() - 1);
    }

    public int getPlayerScore(String score) {
        int index = score.indexOf(':');
        String scoreStr = score.substring(index + 2);
        return Integer.parseInt(scoreStr);
    }

    public boolean compareTurns(String playerName, String turnName) {
        String player1Name, nextTurn;
        player1Name = playerName.substring(0, playerName.length() - 1);
        nextTurn = turnName.substring(turnName.indexOf(":") + 2);
        if(player1Name.equals(nextTurn)) {
            return true;
        }
        return false;
    }

    public String getGameNameFromPath(String path) {
        File file = new File(path);
        return file.getName().substring(0, file.getName().length() - 4);
    }

}
