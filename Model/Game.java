package com.example.canoga.Model;

public class Game {
    private Player player1, player2;
    private Board newBoard;
    private Round newRound;
    private Misc misc;

    public Game(String player1, String player2, int boardSize) {
        this.player1 = new Human(player1);
        if(!player2.equals("Computer")) {
            this.player2 = new Human(player2);
        }
        else {
            this.player2 = new Computer();
        }
        this.newBoard = new Board(boardSize);
        this.newRound = new Round();
        this.misc = new Misc();
    }

    public Game(String fileName) {
        String path = "/data/user/0/com.example.canoga/files/savedGames/" + fileName;
        String gameData = "" ;
    }

    @Override
    public String toString() {
        return "Game{" +
                "player1=" + player1 +
                ", player2=" + player2 +
                ", newBoard=" + newBoard +
                '}';
    }

    public boolean gameOver() {
        boolean gameOver = false;
        if(newRound.roundWon(newBoard)) {
            gameOver = true;
        }
        return gameOver;
    }

    public String continueGame(int currScore, int boardSize) {
        //Bool variable to declare whether if player or computer is the handicap
        String handicapInfo = "";
        boolean forPlayer = false;

        //Start another Round
        int squareToCover = misc.getHandicapSquare(boardSize, currScore);

        //Check handicap First
        if(squareToCover > 0) {
            String firstTurn, handicap;

            //If human went first this round, computer is the handicap and vice versa.
            if(this.newBoard.isHumanGoesFirst()) {
                forPlayer = false;
                firstTurn = player1.getName();
                handicap = player2.getName();
            }
            else {
                forPlayer = true;
                firstTurn = player2.getName();
                handicap = player1.getName();
            }

            handicapInfo += "Since " + firstTurn + " went first this round, " + handicap + " has an advantage for next round.\n";
            handicapInfo += handicap + "'s tile " + squareToCover + " will be automatically covered from the beginning of next round.\n";
        }
        else {
            handicapInfo += "Since last round was a draw, there is no handicap for this round.\n";
        }

        this.newBoard.resetBoardForHandicap(boardSize, forPlayer, squareToCover);
        handicapInfo += "Press Start to continue Game.\n";
        return handicapInfo;
    }

    public Board getNewBoard() {
        return newBoard;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Round getNewRound() {
        return newRound;
    }

    public Player getWinner() {
        Player winner = null;
        if(gameOver()) {
            if(player1.getScore() > player2.getScore()) {
                winner = player1;
            }
            else if(player2.getScore() > player1.getScore()) {
                winner = player2;
            }
        }
        return winner;
    }
}
