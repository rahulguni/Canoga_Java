package com.example.canoga.Model;

public class Game {
    private Player player1, player2;
    private Board newBoard;

    public Game(String player1, String player2, int boardSize) {
        this.player1 = new Human(player1);
        if(!player2.equals("Computer")) {
            this.player2 = new Human(player2);
        }
        else {
            this.player2 = new Computer();
        }
        this.newBoard = new Board(boardSize);
    }

    public void startGame() {

    }

    @Override
    public String toString() {
        return "Game{" +
                "player1=" + player1 +
                ", player2=" + player2 +
                ", newBoard=" + newBoard +
                '}';
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
}
