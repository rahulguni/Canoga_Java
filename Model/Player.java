package com.example.canoga.Model;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public abstract class Player {
    private int score = 0;
    private boolean coverChoice;
    ArrayList<ArrayList<Integer>> possibleTiles = new ArrayList<>();
    public Misc misc = new Misc();

    public Player() {
    }

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

    public ArrayList<Integer> rollDice(Board board, boolean twoDice) {
        ArrayList<Integer> diceSum = new ArrayList<>();
        int diceOne, diceTwo;

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

    public boolean checkForCoverUncover(ArrayList<Boolean> board, int boardSize, int rolledDice, boolean coverSelf) {
        if(getAllMoves(board, boardSize, rolledDice, coverSelf).size() >= 1) {
            return true;
        }
        else {
            return false;
        }
    }

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

    public void saveCurrentGame(Context context, Board board, Player player1, Player player2, String fileName) throws IOException {
        String gameName;
        String gameInfo = "";

        if(!board.getSavedGameName().equals("Null00")) {
            gameName = board.getSavedGameName();
        }
        else {
            gameName = fileName;
            board.setSavedGameName(fileName);
        }

        gameName += ".txt";

        gameInfo = player2.getName() + ":\n\tSquares: " + board.getBoardForSave(false) +
                "\n\tScore: " + player2.getScore() + "\n\n" + player1.getName() +  ":\n\tSquares: "
                + board.getBoardForSave(true) + "\n\tScore: " + player1.getScore();
        gameInfo += (board.isFirstPlay() ? "\n\nfirst Turn: " : "\n\nFirst Turn: ") +
                (board.isHumanGoesFirst() ? player1.getName() : player2.getName());
        gameInfo += "\nNext Turn: " + (board.isHumanTurn() ? player1.getName() : player2.getName()) + "\n";

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
