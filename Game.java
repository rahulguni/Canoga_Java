package com.example.canoga;

public class Game {
    private Player player1, player2;
    private Board newBoard;

    Game(String player1, String player2, int boardSize) {
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
}
