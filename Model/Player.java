package com.example.canoga.Model;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public abstract class Player {
    //Keep track of player's score in Game
    private int score = 0;
    //Boolean value to record player's chosen move for board, true if choose to cover else false
    private boolean coverChoice;
    //An arraylist that contains arraylist of integers that are next possible tiles that a player can choose from.
    ArrayList<ArrayList<Integer>> possibleTiles = new ArrayList<>();
    //Misc object to access helper functions
    public Misc misc = new Misc();

    public Player() {
    }

    /***************************************GETTERS AND SETTERS*****************************************/

    @Override
    public String toString() {
        return this.getName() + ": " + String.valueOf(this.score);
    }

    public void updateScore(int score) {
        this.score += score;
    }

    public void setCoverChoice(boolean coverChoice) {
        this.coverChoice = coverChoice;
    }

    public int getScore() {
        return score;
    }

    public boolean getCoverChoice() {
        return coverChoice;
    }

    public ArrayList<ArrayList<Integer>> getPossibleTiles() {
        return possibleTiles;
    }

    public void setPossibleTiles(ArrayList<ArrayList<Integer>> possibleTiles) {
        this.possibleTiles = possibleTiles;
    }

    /*******************************************************************************************************/

    /*
    Function Name: rollDice
    Purpose: To get random generated dice numbers
    Parameters:
        board:- board object to know whether or not rolling two dice is possible
        twoDice:- boolean variable to determine whether one or two dice is rolled
    Return Value: The arraylist of integer dice values
    Algorithm:
            1. Check if a dice roll can be loaded from saved file. If true, return using board's getLoadedDice().
            2. Generate random value and add to diceSum depending on whether one or two dice rolled.
    Assistance Received: None
    */

    public ArrayList<Integer> rollDice(Board board, boolean twoDice) {
        ArrayList<Integer> diceSum = new ArrayList<>();
        int diceOne, diceTwo;

        //Check if there is dice combination left in board loaded from file
        if(board.getDiceCombinations().size() >= 2) {
            diceOne = board.getLoadedDice();
            diceTwo = board.getLoadedDice();
        }

        else {
            Random random = new Random();
            diceOne = random.nextInt(6) + 1;
            if(twoDice) {
                diceTwo = random.nextInt(6) + 1;
            }
            else {
                diceTwo = 0;
            }
        }

        diceSum.add(diceOne);
        diceSum.add(diceTwo);

        return diceSum;
    }

    /*
    Function Name: getAllCombinations
    Purpose: Save the board state and Player info in a text file.
    Parameters:
            arr[]:- integer array to
            rolledDice:- int sum of dice rolls to get combinations
            index:- int, given number
            reducedSum:- int, reduced number
    Return Value: None
    Algorithm:
            Explained in website.
    Assistance Received: https://www.geeksforgeeks.org/find-all-combinations-that-adds-upto-given-number-2/
    */

    public void getAllCombinations(int arr[], int rolledDice, int index, int reducedSum) {
        //Arraylist to store all moves available temporarily before filtering for duplicates.
        ArrayList<Integer> currMoves = new ArrayList<>();
        //Base Condition
        if(reducedSum < 0) return;

        //If combination is found, store it in currMove
        if(reducedSum == 0) {
            for(int i = 0; i < index; i++) {
                currMoves.add(arr[i]);
            }
            //Filter the combinations by checking for duplicates, only push back combinations with unique elements.
            if(misc.checkDuplicateCombinations(currMoves)) {
                this.possibleTiles.add(currMoves);
            }
            return;
        }
        // Find the previous number stored in arr[]. It helps in maintaining increasing order
        int prev = (index == 0) ? 1: arr[index - 1];

        //note loop starts from previous number i.e. at array location index - 1
        for(int k = prev; k <= rolledDice; k++) {
            // next element of array is k
            arr[index] = k;
            // call recursively with reduced number
            getAllCombinations(arr, rolledDice, index + 1, reducedSum - k);
        }
    }

    /*
    Function Name: checkForCoverUncover
    Purpose: Check if moves are available
    Parameters:
            board:- boolean arraylist to check moves on
            boardSize:- int, array size
            rolledDice: int, sum of dice roll
            coverSelf:- bool, to check board for cover or uncover
    Return Value: None
    Algorithm: None
    Assistance Received: None
    */

    public boolean checkForCoverUncover(ArrayList<Boolean> board, int boardSize, int rolledDice, boolean coverSelf) {
        if(getAllMoves(board, boardSize, rolledDice, coverSelf).size() >= 1) {
            return true;
        }
        else {
            return false;
        }
    }

    /*
    Function Name: getAllMoves
    Purpose: Return all moves available for current board with specific dice roll and option to cover/uncover
    Parameters:
            currBoard:-boolean arraylist of board.
            boardSize:- int, board's boardSize
            rolledDice: int, Sum of dice rolled, passed from base classes.
            toCover:- boolean, true if choice is to cover self board and false if uncover opponent's board
    Return Value: Vector of integer vectors, possibleTiles.
    Algorithm:
            1. Reset member variable vector of vectors possibleTiles.
            2. Fill up the vector with all numbers adding upto rolledDice.
            3. Filter the vector by removing elements that are already covered/uncoverd for a given choice.
            4. Continue filtering the vector by removing elements that exceeds the board size.
    Assistance Received: None
    */

    ArrayList<ArrayList<Integer>> getAllMoves(ArrayList<Boolean> currBoard, int boardSize, int rolledDice, boolean toCover) {
        int[] possibleMoves = new int[rolledDice];
        //Reset current moves
        this.possibleTiles.clear();
        //Base Condition
        getAllCombinations(possibleMoves, rolledDice, 0, rolledDice);

        //Filter combination of dice rolls for current board
        for(int i = possibleTiles.size() - 1; i >= 0; i--) {
            for(int j = 0; j < possibleTiles.get(i).size(); j++) {
                //Erase numbers in the combination that exceeds the board size
                if(possibleTiles.get(i).get(j) > boardSize) {
                    possibleTiles.remove(i);
                    break;
                }
            }
        }

        for(int i = possibleTiles.size() - 1; i >= 0; i--) {
            for (int j = 0; j < possibleTiles.get(i).size(); j++) {
                //Erase all the covered/uncovered tile from the vector
                if(currBoard.get(possibleTiles.get(i).get(j) - 1) != !toCover) {
                    possibleTiles.remove(i);
                    break;
                }
            }
        }

        return this.possibleTiles;
    }

    /*
    Function Name: saveCurrentGame
    Purpose: Save the board state and Player info in a text file.
    Parameters:
            context:- BoardView activity's context to find file location
            board:- board object to make moves in base classes using virtual functions.
            player1:- object of Player class to get information about player when saving save.
            player2:- object of Player class to get information about player when saving save.
            fileName:- string value that has the saved file name.
    Return Value: None
    Algorithm:
            1. Check if there is already a saved game for the board. If not, ask user for a game name.
            2. Open the text file and write to it.
    Assistance Received: None
    */

    public void saveCurrentGame(Context context, Board board, Player player1, Player player2, String fileName) throws IOException {
        //String variable for the game name.
        String gameName;
        String gameInfo = "";

        //Check if there is already a saved file for current game.
        if(!board.getSavedGameName().equals("Null00")) {
            gameName = board.getSavedGameName();
        }
        else {
            gameName = fileName;
            board.setSavedGameName(fileName);
        }

        gameName += ".txt";

        //First write player2's board and score followed by player1's.
        gameInfo = player2.getName() + ":\n\tSquares: " + board.getBoardForSave(false) +
                "\n\tScore: " + player2.getScore() + "\n\n" + player1.getName() +  ":\n\tSquares: "
                + board.getBoardForSave(true) + "\n\tScore: " + player1.getScore();
        gameInfo += (board.isFirstPlay() ? "\n\nfirst Turn: " : "\n\nFirst Turn: ") +
                (board.isHumanGoesFirst() ? player1.getName() : player2.getName());
        gameInfo += "\nNext Turn: " + (board.isHumanTurn() ? player1.getName() : player2.getName()) + "\n";

        //Write first turn, next turn and loaded dice combinations if any.
        if(board.getDiceCombinations().size() >= 2) {
            gameInfo += "\nDice: \n";
            for(int i = 0; i < board.getDiceCombinations().size(); i = i + 2) {
                gameInfo += " " + board.getDiceCombinations().get(i) + " " + board.getDiceCombinations().get(i + 1) + "\n";
            }
        }

        File file = new File(context.getFilesDir(), "savedGames");
        if(!file.exists()) {
            file.mkdir();
        }
        try {
            File saveFile = new File(file, gameName);
            FileWriter writer = new FileWriter(saveFile);
            writer.append(gameInfo);
            writer.flush();
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public abstract boolean oneDicePossible(Board board);
    public abstract boolean checkForMove(Board board, int diceSum);
    public abstract int makeMove(Board board, boolean coverSelf, int diceSum, int tile);
    public abstract int coverSelf(Board board, int diceSum);
    public abstract int uncoverOpp(Board board, int diceSum);

    public abstract String getName();
}
