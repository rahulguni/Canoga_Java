package com.example.canoga.Model;

public class Game {
    private Player player1, player2;
    private Board newBoard;
    private Round newRound;

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

    public void setNewRound(Round newRound) {
        this.newRound = newRound;
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
