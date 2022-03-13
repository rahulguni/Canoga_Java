package com.example.canoga.Model;

import java.util.ArrayList;
import java.util.Random;

public abstract class Player {
    private int score = 0;
    private boolean coverChoice;
    ArrayList<ArrayList<Integer>> possibleTiles;
    private Misc misc = new Misc();

    public Player() {
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

    public boolean isCoverChoice() {
        return coverChoice;
    }

    public ArrayList<ArrayList<Integer>> getPossibleTiles() {
        return possibleTiles;
    }

    public ArrayList<Integer> rollDice(Board board) {
        ArrayList<Integer> diceSum = new ArrayList<>();

        boolean twoDice = true;
        //Check if one die roll is possible in the current board. If yes, let human choose if they want to roll only one die.
        twoDice = !oneDicePossible(board);

        int diceOne, diceTwo;

        Random random = new Random();
        diceOne = random.nextInt(6) + 1;
        if(twoDice) {
            diceTwo = random.nextInt(6) + 1;
        }
        else {
            diceTwo = 0;
        }
        diceSum.add(diceOne);
        diceSum.add(diceTwo);

        return diceSum;
    }

    void getAllCombinations(int arr[], int rolledDice, int index, int reducedSum) {
        //Arraylist to store all moves available temporarily before filtering for duplicates.
        ArrayList<Integer> currMoves = new ArrayList<>();
        //Base Condition
        if(reducedSum < 0) return;

        //If combination is found, store it in currMove
        if(reducedSum == 0) {
            for(int i = 0; i < index; i++) {
                currMoves.set(i, (arr[i]));
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
                //Erase all the covered/uncovered tile from the vector
                if(currBoard.get(possibleTiles.get(i).get(j) - 1) != !toCover) {
                    possibleTiles.remove(i);
                    break;
                }

                //Erase numbers in the combination that exceeds the board size
                if(possibleTiles.get(i).get(j) > boardSize) {
                    possibleTiles.remove(i);
                    break;
                }
            }
        }

        return this.possibleTiles;
    }


    public abstract boolean oneDicePossible(Board board);

    public abstract String getName();
}
