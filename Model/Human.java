package com.example.canoga.Model;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Human extends Player{
    //String to record player's name
    private String humanName;
    //Store elements from the arraylists of arraylist in a single arraylist and update after each tile move.
    private ArrayList<Integer> distinctMoves = new ArrayList<>();

    public Human(String humanName) {
        this.humanName = humanName;
    }

    /*
    Function Name: oneDicePossible
    Purpose: To determine whether or not rolling one dice is possible
    Parameters:
            board:- board object passed by value to know whether or not rolling one dice is possible
    Return Value: Boolean value to return true if one die roll is possible
    Algorithm:
            1. Loop through the board starting from board size to the 7th tile
            2. If one of the tiles is not covered, return false, else true (after confirmation from user).
    Assistance Received: None
    */

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

    /*
    Function Name: checkForMove
    Purpose: Check if a move is available for current dice roll
    Parameters:
            board:- board object to change turns if no more moves are available.
            diceSum:- Current dice roll sum used to check if any move is available for either boolean arrays in board.
    Return Value: Boolean, true if move exists.
    Algorithm:
            1. Set up myBoard and oppBoard pointer point to correct boolean arrays in board according to player turn.
            2. If it is the round's first turn, checkMove exists only for covering self board. Else, check for both cover and uncover.
            3. If moveExists is false, change turn in board.
    Assistance Received: None
    */

    @Override
    public boolean checkForMove(Board board, int diceSum) {
        //Boolean value to return later, true if move exists.
        boolean moveExists;
        //ArrayLists of board
        ArrayList<Boolean> myBoard, oppBoard;

        //Set up arraylists for 2-players mode.
        if(board.isHumanTurn()) {
            myBoard = board.getHumanBoard();
            oppBoard = board.getComputerBoard();
        }
        else {
            oppBoard = board.getHumanBoard();
            myBoard = board.getComputerBoard();
        }

        //If it is the round's firt turn, check moveExists only for covering self board.
        if(board.isFirstPlay()) {
            moveExists = this.checkForCoverUncover(myBoard, board.getBoardSize(), diceSum, true);
        }
        else {
            moveExists = this.checkForCoverUncover(myBoard, board.getBoardSize(), diceSum, true)
            || this.checkForCoverUncover(oppBoard, board.getBoardSize(), diceSum, false);;
        }

        //When no more moves available for player after first move, make uncover option available after first play
        if(!moveExists) {
            board.changeTurn();
        }

        return moveExists;
    }

    /*
    Function Name: makeMove
    Purpose: Prompt user for tiles to cover/uncover and make changes in board.
    Parameters:
            board:- board object to cover/uncover tiles as chosen by user.
            diceSum:- Int Sum of current dice roll.
            coverSelf: Boolean value for player, true if chosen to cover, false if chosen to uncover.
            tile:- Int value of arraylist index to cover/uncover.
    Return Value: Integer- the difference of rolledDice and tile integer to make moves from BoardView.
    Algorithm:
            1. Set all possible tiles for current dice roll.
            2. Check if tile is valid to cover/uncover
            3. return the difference of diceSum and tile.
    Assistance Received: None
    */

    @Override
    public int makeMove(Board board, boolean coverSelf, int diceSum, int tile) {
        int count = 0;
        //Since Human class is used for player2, declare a boolean arraylist that points to player1 or player2 board.
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

    /*
    Function Name: coverSelf
    Purpose: Check if a move is available to cover own tiles for current dice roll.
    Parameters:
            board:- board object to check arraylists
            diceSum:- integer value, sum of current dice roll
    Return Value: Integer, 1 if moves are available else 0.
    Algorithm: None
    Assistance Received: None
    */

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

    /*
    Function Name: uncoverOpp
    Purpose: Check if a move is available to cover own tiles for current dice roll.
    Parameters:
            *oppBoard:- bool pointer that points to opponent's board.
            boardSize:- board object's boardSize integer.
            diceSum:- integer value, sum of current dice roll
    Return Value: Integer, 1 if moves are available else 0.
    Algorithm: None
    Assistance Received: None
    */

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

    /*
    Function Name: checkValidMove
    Purpose: Check if selected tile is available to make a change to.
    Parameters:
            tile:- Integer value of arraylist index in board.
    Return Value: True if tile can be changed, else false
    Algorithm:
            1. Check if tile is present in distinctMoves arraylist.
            2. Update possibleTiles arraylist.
    Assistance Received: None
    */

    private boolean checkValidMove(int tile) {
        distinctMoves = getDistinctMoves(getPossibleTiles());
        if(distinctMoves.contains(tile)) {
            this.setPossibleTiles(this.getMovesAfterTilesMoved(tile, this.getPossibleTiles()));
            return true;
        }
        return false;
    }

    /*
    Function Name: getDistinctMoves
    Purpose: To get all distinct moves for current diceRoll to limit user choices after selecting a tile.
    Parameters:
            allPossibleMoves:- Vector of integer vectors that contains all the possible moves for current dice roll.
    Return Value: Vector of integers with next possible tiles.
    Algorithm:
            1. Store elements from the arraylist of arraylists in a single arraylist.
            2. Remove duplicates from the arraylist and return.
    Assistance Received: None
    */

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

    /*
    Function Name: getMovesAfterTileMoves
    Purpose: To get all remaining tiles after a tile is chosen.
    Parameters:
            option:- Integer value of tile that was chosen by user to cover/uncover
            allPossibleMoves:- Arraylist of integer arraylists that contains all the possible moves for current dice roll.
    Return Value: Vector of integer vectors with only tiles previously containing option.
    Algorithm:
            1. If no option is chosen (option = 0), return allPossibleMoves.
            2. Else, remove selected option from vector and return vectors with only tiles previously containing option.
    Assistance Received: None
    */

    private ArrayList<ArrayList<Integer>> getMovesAfterTilesMoved(int option, ArrayList<ArrayList<Integer>> allPossibleMoves) {
        //Insert all remaining elements in the new arraylist and return it.
        ArrayList<ArrayList<Integer>> movesAfterTilesMoved = new ArrayList<>(new ArrayList<>());

        //Remove selected option from vector and return vectors with only tiles previously containing option
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
