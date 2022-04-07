package com.example.canoga.Model;

import android.util.Log;

import java.util.ArrayList;

public class Computer extends Player{

    //ArrayList to contain the best available move for computer in the board.
    ArrayList<Integer> bestCombination;

    public Computer() {
        bestCombination = new ArrayList<>();
    }

    public ArrayList<Integer> getBestCombination() {
        return bestCombination;
    }

    /*
    Function Name: oneDicePossible
    Purpose: To determine whether or not rolling one dice is possible
    Parameters:
            board:- board object passed by value to know whether or not rolling one dice is possible
    Return Value: Boolean value to return true if one die roll is possible
    Algorithm:
            1. Loop through the board starting from board size to the 7th tile
            2. If one of the tiles is not covered, return false, else check if it is more advantageous to roll a single dice
                using oneDiceChoice function.
    Assistance Received: None
    */

    @Override
    public boolean oneDicePossible(Board board) {
        boolean possible = true;

        //Make sure tiles from 7 up to last tile is filled to roll only one dice
        for(int i = board.getBoardSize() - 1; i >= 6; i--) {
            if(!board.getComputerBoard().get(i)) {
                possible = false;
                break;
            }
        }

        if(possible) {
            possible = oneDiceChoice(board);
        }

        return possible;
    }

    /*
    Function Name: oneDiceChoice
    Purpose: To determine whether or not rolling one dice is possible
    Parameters:
            board:- board object to know whether or not rolling one dice is possible
    Return Value: Boolean value to return true if one die roll is more advantageous.
    Algorithm:
            1. If board's first play, make coverScore minimum and uncoverScore maximum.
            2. If any of coverScore and uncoverScore is 0, make it largest value possible.
            3. If coverScore is less than uncoverScore, return true, else check if one dice roll is possible for
                opponent's board and return.
    Assistance Received: None
    */

    private boolean oneDiceChoice(Board board) {
        //Boolean variable to return at the end of function.
        boolean rollOne = false;
        //coverScore and uncoverScore integers, make move for whichever is smaller.
        int coverScore, uncoverScore;

        if(!board.isFirstPlay()) {
            coverScore = misc.calculateScore(board.getComputerBoard(), true);
            uncoverScore = misc.calculateScore(board.getHumanBoard(),  false);
        }
        else {
            coverScore = 1;
            uncoverScore = board.getBoardSize() - 1;
        }

        //Make cover and uncover scores maximum value if no moves available.
        if(coverScore == 0) {
            coverScore = 13;
        }
        if(uncoverScore == 0) {
            uncoverScore = 13;
        }

        if(coverScore <= uncoverScore) {
            rollOne = true;
        }
        else {
            rollOne = checkOppBoardForOneDice(board.getHumanBoard());
        }

        return rollOne;

    }
    /*
    Function Name: checkOppBoardForOneDice
    Purpose: To determine whether or not rolling one dice is advantageous for opponent's board
    Parameters:
            humanBoard:- Arraylist of humanBoard.
    Return Value: Boolean value to return true if one die roll is more advantageous.
    Algorithm:
            1. initiate integers oneScore and twoScore to 0.
            2. Increment oneScore if any of tiles from 1 to 6 is covered for opponent's board, else increment twoScore.
            3. If oneScore is greater, it means there are tiles from 1-6 in greater number covered than 7-n. In this case, return true, else false.
    Assistance Received: None
    */

    private boolean checkOppBoardForOneDice(ArrayList<Boolean> humanBoard) {
        //Boolean value to return later.
        boolean rollOne = false;
        // oneScore integer for all tiles less than 7 to uncover, twoScore integer for all tiles greater than 6 to uncover.
        int oneScore = 0;
        int twoScore = 0;

        for(int i = humanBoard.size() - 1; i >= 6; i--) {
            if(humanBoard.get(i) == true) {
                twoScore += 1;
            }
        }
        for(int i = 0; i < humanBoard.size(); i++) {
            if(humanBoard.get(i) == true) {
                oneScore += 1;
            }
        }

        //If the number of tiles less than 7 are more in number, rollOne is true.
        if(oneScore > twoScore) {
            rollOne = true;
        }
        else {
            rollOne = false;

        }
        return rollOne;
    }

    /*
    Function Name: checkForMove
    Purpose: Check if a move is available for current dice roll
    Parameters:
            board:- board object  to change turns if no more moves are available.
            diceSum:- Current dice roll sum used to check if any move is available for either boolean arrays in board.
    Return Value: Boolean, true if move exists.
    Algorithm:
            1. If it is the round's first turn, checkMove exists only for covering self board. Else, check for both cover and uncover.
            2. If moveExists is false, change turn in board.
    Assistance Received: None
    */

    @Override
    public boolean checkForMove(Board board, int diceSum) {
        boolean moveExists;
        //If it is the round's first turn, check moveExists only for covering computer board.
        if(board.isFirstPlay()) {
            if((this.coverSelf(board, diceSum)) > 0) {
                moveExists = true;
            }
            else {
                moveExists = false;
            }
        }
        else {
            if((this.coverSelf(board, diceSum) > 0) || this.uncoverOpp(board, diceSum) > 0) {
                moveExists = true;
            }
            else {
                moveExists = false;
            }
        }

        //When no more moves available for player after first move, make uncover option available after first play
        if(!moveExists) {
            board.changeTurn();
        }

        return moveExists;
    }

    @Override
    public int makeMove(Board board, boolean coverSelf, int diceSum, int tile) {
        askHelp(board, diceSum, true, true);
        return 1;
    }

    /*
    Function Name: askHelp
    Purpose: Print the best possible move for current dice roll and make move if called for computer player
    Parameters:
            board:- board object passed by value to check boolean arrays for moves.
            rolledDice:- int, Sum of dice roll
            forComputer:- bool, check if function called for computer player to make a move.
            makeMoveComputer:- bool, check if move is to be made or just information be shown.
    Return Value: String variable that contains information of why computer recommends/makes the move
    Algorithm:
            1. Get coverScore and uncoverScore.
            2. If coverScore is less, print/make moves to cover squares because the lesser the score, the lesser squares to cover. Same for uncoverScore.
            3. If cover and uncover scores are same, choose to cover self's squares.
    Assistance Received: None
    */


    public String askHelp(Board board, int rolledDice, boolean forComputer, boolean makeComputerMove) {
        String helpStr = "";

        int uncoverScore = 0;
        int coverScore = this.coverSelf(board, rolledDice);
        if(!board.isFirstPlay()) {
            uncoverScore = this.uncoverOpp(board, rolledDice);
        }

        if(coverScore == 0) {
            coverScore = 13;
        }
        if(uncoverScore == 0) {
            uncoverScore = 13;
        }

        if(coverScore <= uncoverScore) {
            String noun = (forComputer) ? "Computer" : " you";
            String possNoun = (forComputer) ? "Computer's" : " your";
            if(!forComputer) {
                helpStr += "Computer suggests you cover your own tiles because";
            }
            if(uncoverScore == 13) {
                helpStr += noun + " cannot uncover opponent's tiles with the current dice roll: " + rolledDice + ".\n";
            }
            else {
                String comparison = " less than";
                if(coverScore == uncoverScore) {
                    comparison = " equal to";
                }
                helpStr += possNoun + " tiles to cover is" + comparison + " opponent's tiles to uncover.\n";
            }
            if(board.isHumanTurn()) {
                this.getAllMoves(board.getHumanBoard(), board.getBoardSize(), rolledDice, true);
            }
            else {
                this.getAllMoves(board.getComputerBoard(), board.getBoardSize(), rolledDice, true);
            }
            this.setCoverChoice(true);
            if(forComputer) {
                helpStr += "Computer has decided to cover its own tiles.\n";
            }
        }
        else {
            String noun = (forComputer) ? "Computer" : " you";
            String possNoun = (forComputer) ? "Computer's" : " your";
            if(!forComputer) {
                helpStr += "Computer suggests you uncover opponent's tiles because";
            }
            if(coverScore == 13) {
                helpStr += noun + " cannot cover " + possNoun + " tiles with the current dice roll: " + rolledDice + ".\n";
            }
            else {
                helpStr += possNoun + " opponent's tiles to uncover is less than " + possNoun + " tiles to cover.\n";
            }
            if(board.isHumanTurn()) {
                this.getAllMoves(board.getComputerBoard(), board.getBoardSize(), rolledDice, false);
            }
            else {
                this.getAllMoves(board.getHumanBoard(), board.getBoardSize(), rolledDice, false);
            }
            this.setCoverChoice(false);
            if(forComputer) {
                helpStr += "Computer has decided to uncover opponent's tiles.\n";
            }
        }
        this.bestCombination = misc.getBestCombination(this.getPossibleTiles());
        if(makeComputerMove) {
            for(int tile : bestCombination) {
                if(this.getCoverChoice()) {
                    board.changeTile(board.getComputerBoard(), tile - 1, this.getCoverChoice());
                }
                else {
                    board.changeTile(board.getHumanBoard(), tile - 1, this.getCoverChoice());
                }
            }
        }

        String temp = (forComputer) ? " has decided to " : " suggests you ";
        helpStr += "Computer" + temp + ((this.getCoverChoice()) ? "cover" : "uncover") + " tile(s) ";
        for(int tile: bestCombination) {
            helpStr += String.valueOf(tile) + " ";
        }
        helpStr += "because ";
        String noun = (forComputer) ? " it" : " you";
        String possNoun = (forComputer) ? " its" : " your";
        if(this.getPossibleTiles().size() == 1) {
            helpStr += "that is the only available option to" + noun + " if" + noun + " choose to "
                    + (this.getCoverChoice() ? ("cover" + possNoun + " tile ") : ("uncover" + possNoun  + " opponent's tile "))
                    + "for best result.\n";
        }
        else {
            helpStr += "that is the best available option that contains the greatest available tile to "
                    + (this.getCoverChoice() ? ("cover" + possNoun + " tile ") : ("uncover" + possNoun  + " opponent's tile "))
                    + "for best result.\n";
        }
        return helpStr;
    }

    /*
    Function Name: coverSelf
    Purpose: Check if a move is available to cover own tiles for current dice roll.
    Parameters:
            board:- board object to check moves
            diceSum:- integer value, sum of current dice roll
    Return Value: Int value, Cover score for computer board.
    Algorithm:
            1. Initiate coverScore to 0 and Check if move exists for covering computer's tiles.
            2. If true, give coverScore the value equal to the sum of numbers of tiles that are left to cover and return
    Assistance Received: None
    */

    @Override
    public int coverSelf(Board board, int diceSum) {
        int coverScore = 0;
        ArrayList<Boolean> currBoard;
        if(board.isHumanTurn()) {
            currBoard = board.getHumanBoard();
        }
        else {
            currBoard = board.getComputerBoard();
        }
        if(this.checkForCoverUncover(currBoard, board.getBoardSize(), diceSum, true)) {
            coverScore = misc.calculateScore(currBoard, true);
        }
        return coverScore;
    }

    /*
    Function Name: uncoverOpp
    Purpose: Check if a move is available to cover own tiles for current dice roll.
    Parameters:
            board:- board object to check moves
            diceSum:- integer value, sum of current dice roll
    Return Value: Int value, Uncover score for opponent's board.
    Algorithm:
            1. Initiate uncoverScore to 0 and Check if move exists for uncovering opponent's tiles.
            2. If true, give uncoverScore the value equal to the sum of numbers of tiles that are left to uncover and return
    Assistance Received: None
    */

    @Override
    public int uncoverOpp(Board board, int diceSum) {
        int uncoverScore = 0;
        ArrayList<Boolean> currBoard;
        if(board.isHumanTurn()) {
            currBoard = board.getComputerBoard();
        }
        else {
            currBoard = board.getHumanBoard();
        }
        if(this.checkForCoverUncover(currBoard, board.getBoardSize(), diceSum, false)) {
            uncoverScore = misc.calculateScore(currBoard, false);
        }
        return uncoverScore;
    }

    @Override
    public String getName() {
        return "Computer";
    }
}
