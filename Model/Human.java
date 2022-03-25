package com.example.canoga.Model;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Human extends Player{
    private String humanName;
    private ArrayList<Integer> distinctMoves = new ArrayList<>();

    public Human(String humanName) {
        this.humanName = humanName;
    }

    @Override
    public boolean oneDicePossible(Board board) {
        boolean possible = true;
        //Make sure tile from 7 up to last number is filled to roll only one dice
        for(int i = board.getBoardSize() - 1; i >=6; i--) {
            //Remove after computer
            if(board.isHumanTurn()) {
                if(!board.getHumanBoard().get(i)) {
                    possible = false;
                    break;
                }
            }
            else {
                if(!board.getComputerBoard().get(i)) {
                    possible = false;
                    break;
                }
            }
        }
        return possible;
    }

    @Override
    public boolean checkForMove(Board board, int diceSum) {
        boolean moveExists;
        ArrayList<Boolean> myBoard, oppBoard;

        if(board.isHumanTurn()) {
            myBoard = board.getHumanBoard();
            oppBoard = board.getComputerBoard();
        }
        else {
            oppBoard = board.getHumanBoard();
            myBoard = board.getComputerBoard();
        }

        if(board.isFirstPlay()) {
            moveExists = this.checkForCoverUncover(myBoard, board.getBoardSize(), diceSum, true);
        }
        else {
            moveExists = this.checkForCoverUncover(myBoard, board.getBoardSize(), diceSum, true)
            || this.checkForCoverUncover(oppBoard, board.getBoardSize(), diceSum, false);;
        }

        if(!moveExists) {
            board.changeTurn();
        }

        return moveExists;
    }


    @Override
    public int makeMove(Board board, boolean coverSelf, int diceSum, int tile) {
        int count = 0;
        ArrayList<Boolean> currBoard;

        if(board.isHumanTurn()) {
            if(coverSelf) {
                currBoard = board.getHumanBoard();
            }
            else {
                currBoard = board.getComputerBoard();
            }
        }
        else {
            if(coverSelf) {
                currBoard = board.getComputerBoard();
            }
            else {
                currBoard = board.getHumanBoard();
            }
        }

        //refresh all moves
        this.setPossibleTiles(this.getAllMoves(currBoard, currBoard.size(), diceSum, coverSelf));

        if(checkValidMove(tile + 1)) {
            board.changeTile(currBoard, tile, coverSelf);
            count = (tile + 1);
        }

        return count;

    }

    @Override
    public int coverSelf(Board board, int diceSum) {
        ArrayList<Boolean> currBoard;
        if(board.isHumanTurn()) {
            currBoard = board.getHumanBoard();
        }
        else {
            currBoard = board.getComputerBoard();
        }
        int cover = 0;
        cover = (this.checkForCoverUncover(currBoard, board.getBoardSize(), diceSum, true)) ? 1 : 0;
        return cover;
    }

    @Override
    public int uncoverOpp(Board board, int diceSum) {
        ArrayList<Boolean> currBoard;
        if(board.isHumanTurn()) {
            currBoard = board.getComputerBoard();
        }
        else {
            currBoard = board.getHumanBoard();
        }
        int uncover = 0;
        uncover = (this.checkForCoverUncover(currBoard, board.getBoardSize(), diceSum, false)) ? 1 : 0;
        return uncover;
    }

    private boolean checkValidMove(int tile) {
        distinctMoves = getDistinctMoves(getPossibleTiles());
        Log.d("moves", getPossibleTiles().toString());
        Log.d("Distinct", distinctMoves.toString());
        if(distinctMoves.contains(tile)) {
            this.setPossibleTiles(this.getMovesAfterTilesMoved(tile, this.getPossibleTiles()));
            return true;
        }
        return false;
    }

    private ArrayList<Integer> getDistinctMoves(ArrayList<ArrayList<Integer>> allPossibleTiles) {
        //Store elements from arraylist of arraylist in a single arraylist.
        ArrayList<Integer> distinctMoves = new ArrayList<>();

        for(int i = allPossibleTiles.size() - 1; i >= 0; i--) {
            for(int j = 0; j < allPossibleTiles.get(i).size(); j++) {
                distinctMoves.add(allPossibleTiles.get(i).get(j));
            }
        }

        //Remove duplicates from Arraylist
        Set<Integer> distinctSet = new HashSet<>(distinctMoves);
        distinctMoves.clear();
        distinctMoves.addAll(distinctSet);

        return distinctMoves;
    }

    private ArrayList<ArrayList<Integer>> getMovesAfterTilesMoved(int option, ArrayList<ArrayList<Integer>> allPossibleMoves) {
        //Insert all remaining elements in the new arraylist and return it.
        ArrayList<ArrayList<Integer>> movesAfterTilesMoved = new ArrayList<>(new ArrayList<>());

        for(int i = allPossibleMoves.size() - 1; i >= 0; i--) {
            for(int j = allPossibleMoves.get(i).size() - 1; j >= 0; j--) {
                if(allPossibleMoves.get(i).get(j) == option) {
                    allPossibleMoves.get(i).remove(j);
                    movesAfterTilesMoved.add(allPossibleMoves.get(i));
                    break;
                }
            }
        }

        return movesAfterTilesMoved;

    }


    @Override
    public String getName() {
        return this.humanName;
    }
}
