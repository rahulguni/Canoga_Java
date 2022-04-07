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

    /*
    Function Name: getDiceRollSum
    Purpose: Returns the sum of first two integers of arraylist
    Parameters:
            diceRolls:- arraylist of integers containing the dice roll integers
    Return Value: int
    Algorithm: None
    Assistance Received: None
    */

    public int getDiceRollSum(ArrayList<Integer> diceRolls) {
        int sum = 0;
        for(int i = 0; i < 2; i++) {
            sum += diceRolls.get(i);
        }
        return sum;
    }

    /*
    Function Name: checkDuplicateCombinations
    Purpose: Checks if a given vector has duplicates
    Parameters:
            currMoves:- arraylist of integers containing all possible moves
    Return Value: bool
    Algorithm:
            1. Check if every element in the vector is unique and return.
    Assistance Received: None
    */
    public boolean checkDuplicateCombinations(ArrayList<Integer> currMoves) {
        Set<Integer> set = new HashSet<>(currMoves);
        if(set.size() < currMoves.size()) {
            return false;
        }
        return true;
    }

    /*
    Function Name: calculateScore
    Purpose: Calculate score of given board
    Parameters:
            board:- boolean arraylist to check
            cover:- boolean value to indicate whether to check for cover or uncover for a given board.
    Return Value: int, score of the board
    Algorithm: None
    Assistance Received: None
    */

    public int calculateScore(ArrayList<Boolean> board, boolean cover) {
        int boardScore = 0;

        for(int i = 0; i < board.size(); i++) {
            if(board.get(i) == !cover) {
                boardScore += 1;
            }
        }

        Log.d("TAG", "calculateScore: " + cover + " :" + boardScore);

        return boardScore;
    }

    public int getPlayerScore(String score) {
        int index = score.indexOf(':');
        String scoreStr = score.substring(index + 2);
        return Integer.parseInt(scoreStr);
    }


    public String getGameNameFromPath(String path) {
        File file = new File(path);
        return file.getName().substring(0, file.getName().length() - 4);
    }

    /*
    Function Name: getOpponent
    Purpose: Return name of opponent
    Parameters:
            game:- current game
            player:- player object to check for opponent
    Return Value: String
    Algorithm: None
    Assistance Received: None
    */

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

    /*
    Function Name: switchPlayer
    Purpose: Return name of opponent
    Parameters:
            game:- current game
            player:- player object to check for opponent
    Return Value: String
    Algorithm: None
    Assistance Received: None
    */

    public Player switchPlayer(Game game, Player player) {
        if(player.equals(game.getPlayer1())) {
            return game.getPlayer2();
        }
        else {
            return game.getPlayer1();
        }
    }

    /*
    Function Name: getCurrentPlayer
    Purpose: Return name of opponent
    Parameters:
            game:- current game object
            board:- board object to check turn
    Return Value: Player object
    Algorithm: None
    Assistance Received: None
    */

    public Player getCurrentPlayer(Game game, Board board) {
        if(board.isHumanTurn()) {
            return game.getPlayer1();
        }
        else {
            return game.getPlayer2();
        }
    }

    /*
    Function Name: getCurrentPlayer
    Purpose: Print all the available moves for a turn
    Parameters:
            allMoves:- Arraylist of all moves available
    Return Value: String
    Algorithm: Go over all moves and give it a nice format to print from boardView.
    Assistance Received: None
    */

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

    public boolean compareTurns(String playerName, String turnName) {
        String player1Name, nextTurn;
        player1Name = playerName.substring(0, playerName.length() - 1);
        nextTurn = turnName.substring(turnName.indexOf(":") + 2);
        if(player1Name.equals(nextTurn)) {
            return true;
        }
        return false;
    }

    /*
    Function Name: getHandicapSquare
    Purpose: Get the square to cover for handicap after a round is over
    Parameters:
            boardSize:- current board object's boardSize
            number:- Score that adds up to handicap tile number.
    Return Value: int, square to be covered for handicap
    Algorithm:
            1. Add the numbers in number parameter (18 = 1 + 8 = 9).
            2. If the number is more than boardSize, repeat the process again.
    Assistance Received: None
    */

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

    /*
    Function Name: getBestCombination
    Purpose: Get the best combination of moves
    Parameters:
            allMoves:- arraylist of arraylist of integers that contains all the possible moves
    Return Value: arraylist of integers, best possible moves
    Algorithm: None.
    Assistance Received: None
    */

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

    /*
    Function Name: getDiceComboInt
    Purpose: Convert all dice loads from text file into integers
    Parameters:
            diceComboString:- vector of strings that contains dice combinations loaded from file
    Return Value: vector of integers, same dice rolls but as integers
    Algorithm:
            1. Convert each string into an integer and push them to a vector to return.
    Assistance Received: None
    */

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

    public boolean checkSaveGameName(String gameName) {
        if(gameName.length() >= 1 && !gameName.contains(" ")) {
            return true;
        }
        return false;
    }

    /*
    Function Name: gameNames
    Purpose: get all game names in the app's save file directory
    Parameters:
            Context:- BoardView activity's context to find file location
    Return Value: Arraylist of Strings that contains all file names.
    Algorithm: None
    Assistance Received: None
    */

    public ArrayList<String> gameNames(Context context) {
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

    /*
    Function Name: checkPlayerName
    Purpose: Check if player's name is valid
    Parameters:
            name:- string, name to check
    Return Value: bool
    Algorithm:
            1. Check if user entered name is "Computer" or "computer". If not, return true.
    Assistance Received: None
    */


    public boolean checkPlayerName(String name) {
        if(name.equals("Computer") || name.equals("computer") || name.equals("")) {
            return false;
        }
        return true;
    }

    /*
    Function Name: removeEmptyLines
    Purpose: Remove vector elements containing empty lines or empty spaces
    Parameters:
            gameDataVec:- Arraylist of strings
    Return Value: arraylist of strings, after removing empty lines
    Algorithm:
            1. Check if an element in the arraylist is an empty line or empty space.
            2. Remove them from the arraylist if true and return the arraylist.
    Assistance Received: None
    */

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


}
