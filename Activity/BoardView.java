package com.example.canoga.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canoga.Adapters.BoardCustomAdapter;
import com.example.canoga.Model.Board;
import com.example.canoga.Model.Computer;
import com.example.canoga.Model.Game;
import com.example.canoga.Model.Misc;
import com.example.canoga.Model.Player;
import com.example.canoga.R;

import java.io.IOException;
import java.util.ArrayList;

public class BoardView extends AppCompatActivity implements BoardCustomAdapter.OnTileClickListener {
    private TextView player1info, player2info, gameInfo;
    private RecyclerView humanBoard, computerBoard;
    private Button rollDiceBtn, helpBtn, viewMovesBtn, startNewGameBtn;
    private ImageView dice1, dice2, player1GoesFirstSign, player1TurnSign, player2GoesFirstSign, player2TurnSign;
    private BoardCustomAdapter humanBoardAdapter, computerBoardAdapter;
    private Game game;
    private Board board;
    private Player currPlayer;
    private Misc misc = new Misc();
    private int currDiceRoll;
    private MenuItem menuItem;
    private int[] allDice = new int[]{R.drawable.dice0, R.drawable.dice1, R.drawable.dice2, R.drawable.dice3,
            R.drawable.dice4,
            R.drawable.dice5, R.drawable.dice6};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boardview);
        loadNewGame();
        setFields();
        fillBoardDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_game, menu);
        menuItem = menu.getItem(0);
        menuItem.setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try {
            saveGameClicked();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    void loadNewGame() {
        String player1Name = getIntent().getExtras().getString("player1Name");
        String player2Name = getIntent().getExtras().getString("player2Name");
        int boardSize = Integer.valueOf(getIntent().getExtras().getString("boardSize"));
        if(getIntent().getExtras().get("gameMode").equals("newGame")) {
            game = new Game(player1Name, player2Name, boardSize);
        }
        else {
            game = new Game(getIntent().getExtras().getString("fileName"));
        }
        board = game.getNewBoard();
    }

    void fillBoardDetails() {
        player1info.setText(game.getPlayer1().getName() + ":  " + String.valueOf(game.getPlayer1().getScore()));
        player2info.setText(game.getPlayer2().getName() + ":  " + String.valueOf(game.getPlayer2().getScore()));
        this.fillGameInfo("Press Start!");
    }

    public void startNewGame(View v) {
        this.startNewGameBtn.setEnabled(false);
        boolean gameBegin = false;

        if(this.startNewGameBtn.getText().equals("Start")) {
            ArrayList<Integer> diceSum = this.game.getPlayer1().rollDice(true);
            this.viewDice(diceSum);
            currDiceRoll = misc.getDiceRollSum(diceSum);
            displayDiceRoll(this.game.getPlayer1().getName(), misc.getDiceRollSum(diceSum));
            this.startNewGameBtn.setText("Continue");
        }
        else if(this.startNewGameBtn.getText().equals("Continue")) {
            ArrayList<Integer> diceSum = this.game.getPlayer2().rollDice(true);
            this.viewDice(diceSum);
            displayDiceRoll(this.game.getPlayer2().getName(), misc.getDiceRollSum(diceSum));
            int anotherDice = misc.getDiceRollSum(diceSum);
            if(this.currDiceRoll > anotherDice) {
                this.game.getNewBoard().setHumanTurn(true);
                this.game.getNewBoard().setHumanGoesFirst(true);
                this.currPlayer = this.game.getPlayer1();
                this.startNewGameBtn.setText("Start Game");
            }
            else if(this.currDiceRoll < anotherDice) {
                this.game.getNewBoard().setHumanTurn(false);
                this.game.getNewBoard().setHumanGoesFirst(false);
                this.currPlayer = this.game.getPlayer2();
                this.startNewGameBtn.setText("Start Game");
                this.currDiceRoll = anotherDice;
            }
            else {
                this.fillGameInfo(this.gameInfo.getText() + "\nBoth players rolled same dice sum\nStart over again!");
                this.startNewGameBtn.setText("Start");
                gameBegin = false;
            }
        }
        else if(this.startNewGameBtn.getText().equals("Start Game")) {
            gameBegin = true;
            this.fillGameInfo("Since " + this.currPlayer.getName() + " had a combined dice sum of " + this.currDiceRoll + ", " + this.currPlayer.getName() + " goes first!");
        }

        if(gameBegin) {
            dice1.setImageResource(allDice[0]);
            dice2.setImageResource(allDice[0]);
            this.startNewGameBtn.setVisibility(View.INVISIBLE);
            this.rollDiceBtn.setVisibility(View.VISIBLE);
            this.helpBtn.setVisibility(View.VISIBLE);
            this.viewMovesBtn.setVisibility(View.VISIBLE);
            this.displayTurns();
        }
        else {
            this.startNewGameBtn.setEnabled(true);
        }
    }

    @Override
    public void onTileClick(int position) {
        if(this.currPlayer != null) {
            int tileSumLeft;
            if((tileSumLeft = this.currPlayer.makeMove(this.board, this.currPlayer.getCoverChoice(), this.currDiceRoll, position)) > 0) {
                this.displayTileState(position + 1);
                this.currDiceRoll -= tileSumLeft;
                if(this.currDiceRoll == 0) {
                    String gameInfoText = this.gameInfo.getText().toString();
                    gameInfoText += "Roll Again!";
                    this.fillGameInfo(gameInfoText);
                    restrictBoardAfterChoice(true);
                    this.rollDiceBtn.setEnabled(true);
                    this.rollDiceBtn.setText("Roll Dice");
                    checkGameOver();
                    this.menuItem.setEnabled(true);
                }
            }
            else {
                this.displayNoMoves();
            }
            refreshView(position);
        }
    }

    private void checkGameOver() {
        if(this.game.gameOver()) {
            restrictBoardAfterChoice(true);
            fillGameInfo("Round over. Click on Wrap Up Button to view scores and other options!");
            rollDiceBtn.setText("Wrap Up");
        }
    }


    public void helpButtonClick(View v) {
        if(!(this.currPlayer instanceof Computer)) {
            this.displayHelp(false);
        }
        else {
            this.displayHelp(true);
        }
    }

    public void viewMovesButtonClick(View v) {
        if(!(this.currPlayer instanceof Computer)) {
            this.displayNextMoves();
        }
        else {
            Toast toast = new Toast(this);
            toast.setText("View moves not available for Computer.");
            toast.show();
        }
    }

    public void rollDice(View v) {
        if(this.rollDiceBtn.getText().equals("Roll Dice")) {
            this.menuItem.setEnabled(false);
            rollDice();
        }
        else if(this.rollDiceBtn.getText().equals("Make Move")) {
            if(this.currPlayer instanceof Computer) {
                this.enableHelpButtons(false, false);
                this.rollDiceBtn.setText("Roll Dice");
                this.currPlayer.makeMove(this.board, this.currPlayer.getCoverChoice(), this.currDiceRoll, 0);

                if(!this.game.gameOver()) {
                    humanBoardAdapter.notifyDataSetChanged();
                    computerBoardAdapter.notifyDataSetChanged();
                }
                else {
                    checkGameOver();
                }

                this.menuItem.setEnabled(true);
            }
            else {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Next Move");
                alert.setMessage("Choose if you want to cover your own tiles or uncover " + misc.getOpponent(this.game, this.currPlayer)
                        + "'s tiles.");
                alert.setPositiveButton("Cover", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        currPlayer.setCoverChoice(true);
                        if(currPlayer.coverSelf(board, currDiceRoll) > 0) {
                            nextMoveMade(true);
                        }
                        else {
                            displayNoMove();
                        }
                    }
                });
                alert.setNegativeButton("Uncover", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        currPlayer.setCoverChoice(false);
                        if(currPlayer.uncoverOpp(board, currDiceRoll) > 0) {
                            nextMoveMade(false);
                        }
                        else {
                            displayNoMove();
                        }
                    }
                });

                AlertDialog alertDialog = alert.create();
                alertDialog.setCancelable(false);
                alertDialog.show();

                Button button = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                if(this.board.isFirstPlay()) {
                    button.setEnabled(false);
                }
            }
        }
        else if(this.rollDiceBtn.getText().equals("Wrap Up")) {
            roundOverNotification();
        }
    }

    private void rollDice() {
        this.fillGameInfo("Rolling dice for " + currPlayer.getName());
        if(this.currPlayer instanceof Computer) {
            ArrayList<Integer> currDiceCombo = this.currPlayer.rollDice(!this.currPlayer.oneDicePossible(this.board));
            rollDiceHelper(currDiceCombo);
            this.helpBtn.setText("Computer Steps");
        }
        else {
            if(this.currPlayer.oneDicePossible(this.board)) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("One Dice Roll");
                alert.setMessage("You have the option to roll only one die. Do you want to roll only one dice?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ArrayList<Integer> currDiceCombo = currPlayer.rollDice(false);
                        rollDiceHelper(currDiceCombo);
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ArrayList<Integer> currDiceCombo = currPlayer.rollDice(true);
                        rollDiceHelper(currDiceCombo);
                    }
                });

                AlertDialog alertDialog = alert.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
            else {
                ArrayList<Integer> currDiceCombo = currPlayer.rollDice(true);
                rollDiceHelper(currDiceCombo);
            }
            this.helpBtn.setText("Help");
        }
    }

    private void rollDiceHelper(ArrayList<Integer> currDiceCombo) {
        this.viewDice(currDiceCombo);
        enableHelpButtons(true, false);
        this.currDiceRoll = misc.getDiceRollSum(currDiceCombo);
        this.displayDiceRoll(currPlayer.getName(), this.currDiceRoll);
        if(this.currPlayer.checkForMove(this.board, this.currDiceRoll)) {
            this.rollDiceBtn.setText("Make Move");
        }
        else {
            this.fillGameInfo(this.currPlayer.getName() + " has no more moves for current dice roll. It is " +
                    misc.getOpponent(this.game, this.currPlayer) + "'s turn!");
            this.currPlayer = misc.switchPlayer(this.game, this.currPlayer);
            this.displayTurns();
        }
    }

    private void nextMoveMade(boolean coverChoice) {
        this.rollDiceBtn.setText("Roll Dice");
        this.rollDiceBtn.setEnabled(false);
        enableHelpButtons(true, true);
        if(coverChoice) {
            this.fillGameInfo("Click on your tiles to cover!");
        }
        else {
            this.fillGameInfo("Click on " + misc.getOpponent(this.game, this.currPlayer) + "'s tiles to uncover");
        }
        restrictBoardAfterChoice(false);
    }

    private void roundOverNotification() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.game_over_dialog);

        //Initializing the views of the dialog
        Button viewScoreBtn, continueGameBtn, endGameBtn;
        TextView winnerInfo;

        viewScoreBtn = dialog.findViewById(R.id.all_score_button_id);
        continueGameBtn = dialog.findViewById(R.id.another_round_btn_id);
        endGameBtn = dialog.findViewById(R.id.end_game_btn_id);
        winnerInfo = dialog.findViewById(R.id.winner_info_id);

        int roundScore = game.getNewRound().updateScore(this.board, this.game.getPlayer1(), this.game.getPlayer2());
        winnerInfo.setText(this.game.getNewRound().getWinner() + ": " + String.valueOf(Math.abs(roundScore)));

        viewScoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAllScores(false);
            }
        });

        endGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAllScores(true);
            }
        });

        continueGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameInfo.setText(game.continueGame(roundScore, board.getBoardSize()));
                humanBoardAdapter.notifyDataSetChanged();
                computerBoardAdapter.notifyDataSetChanged();
                dialog.cancel();
                resetButtonsForNewGame();
            }
        });

        dialog.show();
    }

    private void resetButtonsForNewGame() {
        rollDiceBtn.setVisibility(View.INVISIBLE);
        startNewGameBtn.setVisibility(View.VISIBLE);
        startNewGameBtn.setText("Start");
        helpBtn.setVisibility(View.INVISIBLE);
        viewMovesBtn.setVisibility(View.INVISIBLE);
        startNewGameBtn.setEnabled(true);
        rollDiceBtn.setText("Roll Dice");
        this.player1TurnSign.setVisibility(View.INVISIBLE);
        this.player1GoesFirstSign.setVisibility(View.INVISIBLE);
        this.player2TurnSign.setVisibility(View.INVISIBLE);
        this.player2GoesFirstSign.setVisibility(View.INVISIBLE);
    }

    private void setFields() {
        player1info = findViewById(R.id.player1_info_id);
        player2info = findViewById(R.id.player2_info_id);
        gameInfo = findViewById(R.id.game_instructions_id);
        humanBoard = findViewById(R.id.humanBoard_recyclerview_id);
        computerBoard = findViewById(R.id.computerBoard_recyclerview_id);
        dice1 = findViewById(R.id.dice1_id);
        dice2 = findViewById(R.id.dice2_id);
        dice1.setImageResource(allDice[0]);
        dice2.setImageResource(allDice[0]);
        player1GoesFirstSign = findViewById(R.id.player1_goes_first_id);
        player2GoesFirstSign = findViewById(R.id.player2_goes_first_id);
        player1TurnSign = findViewById(R.id.player1_turn_id);
        player2TurnSign = findViewById(R.id.player2_turn_id);
        rollDiceBtn = findViewById(R.id.roll_dice_btn_id);
        helpBtn = findViewById(R.id.help_btn_id);
        viewMovesBtn = findViewById(R.id.view_moves_btn_id);
        startNewGameBtn = findViewById(R.id.start_new_game_btn);
        humanBoardAdapter = new BoardCustomAdapter(this, board.getHumanBoard(), this);
        humanBoard.setAdapter(humanBoardAdapter);
        humanBoard.setLayoutManager(new LinearLayoutManager(this));
        computerBoardAdapter = new BoardCustomAdapter(this, board.getComputerBoard(), this);
        computerBoard.setAdapter(computerBoardAdapter);
        computerBoard.setLayoutManager(new LinearLayoutManager(this));
        enableHelpButtons(false, false);
        gameInfo.setMovementMethod(new ScrollingMovementMethod());
    }

    private void viewDice(ArrayList<Integer> dice) {
        if(dice.get(1) != 0) {
            this.dice1.setImageResource(allDice[dice.get(0)]);
            this.dice2.setImageResource(allDice[dice.get(1)]);
        }
        else {
            this.dice2.setImageResource(allDice[dice.get(0)]);
            this.dice1.setImageResource(allDice[0]);
        }
    }

    private void displayDiceRoll(String playerName, int diceRoll) {
        fillGameInfo("Rolling dice for " + playerName + "\n" + playerName + " rolled " + diceRoll);
    }

    private void restrictBoardAfterChoice(boolean reset) {
        if(reset) {
            enableHelpButtons(false, false);
            this.computerBoardAdapter.setClickAble(false);
            this.humanBoardAdapter.setClickAble(false);
        }
        else {
            if(this.currPlayer == this.game.getPlayer1()) {
                if(this.currPlayer.getCoverChoice()) {
                    this.restrictBoard(true);
                }
                else {
                    this.restrictBoard(false);
                }
            }
            else {
                if(this.currPlayer.getCoverChoice()) {
                    this.restrictBoard(false);
                }
                else {
                    this.restrictBoard(true);
                }
            }
        }
    }

    private void restrictBoard(boolean clickAble) {
        this.humanBoardAdapter.setClickAble(clickAble);
        this.computerBoardAdapter.setClickAble(!clickAble);
    }
    private void displayNoMove() {
        Toast toast = new Toast(this);
        toast.setText("Cannot " + ((!currPlayer.getCoverChoice()) ? "cover your" : "uncover opponent's ") + " tile with the current dice roll.");
        toast.show();
    }

    private void displayNoMoves() {
        Toast toast = new Toast(this);
        toast.setText("Cannot " + ((currPlayer.getCoverChoice()) ? "cover" : "uncover") + " this tile. Check your available moves.");
        toast.show();
    }

    private void displayTileState( int pos) {
        this.fillGameInfo(((this.currPlayer.getCoverChoice()) ? this.currPlayer.getName() : misc.getOpponent(this.game, this.currPlayer)) + "'s " + pos + " tile has been " +
                ((this.currPlayer.getCoverChoice()) ? "covered. " : "uncovered. "));
    }

    private void refreshView(int position) {
        this.humanBoardAdapter.notifyItemChanged(position);
        this.computerBoardAdapter.notifyItemChanged(position);
    }

    private void displayTurns() {
        if(this.board.isHumanGoesFirst()) {
            player2GoesFirstSign.setVisibility(View.INVISIBLE);
            player1GoesFirstSign.setVisibility(View.VISIBLE);
        }
        else {
            player1GoesFirstSign.setVisibility(View.INVISIBLE);
            player2GoesFirstSign.setVisibility(View.VISIBLE);
        }
        if(this.board.isHumanTurn()) {
            player1TurnSign.setVisibility(View.VISIBLE);
            player2TurnSign.setVisibility(View.INVISIBLE);
        }
        else {
            player2TurnSign.setVisibility(View.VISIBLE);
            player1TurnSign.setVisibility(View.INVISIBLE);
        }
    }

    private void enableHelpButtons(boolean helpBtn, boolean viewMovesBtn) {
        this.viewMovesBtn.setEnabled(viewMovesBtn);
        this.helpBtn.setEnabled(helpBtn);
    }

    private void displayAllScores(boolean gameEnd) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SCORES");
        String message = this.game.getPlayer1() + "\n" + this.game.getPlayer2() + "\n";
        if(gameEnd) {
            builder.setCancelable(false);
            message += "Winner: " + this.game.getWinner();
            builder.setPositiveButton("End Game", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(builder.getContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void displayNextMoves() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Available moves\n");
        builder.setMessage(misc.getMoves(this.currPlayer.getPossibleTiles()));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void displayHelp(boolean forComputer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Computer computer = new Computer();
        if(!forComputer) {
            builder.setTitle("Help Mode\n");
            builder.setMessage(computer.askHelp(this.board, this.currDiceRoll, false, false));
        }
        else {
            builder.setTitle("Computer Move\n");
            builder.setMessage(computer.askHelp(this.board, this.currDiceRoll, true, false));
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void fillGameInfo(String gameInfo) {
        this.gameInfo.setText(gameInfo);
        this.gameInfo.scrollTo(0, 0);
    }

    public void saveGameClicked() throws IOException {
        if(this.board.getSavedGameName().equals("Null00")) {

        }
        else {
            this.currPlayer.saveCurrentGame(this, this.board, this.currPlayer, misc.getOpponentPlayer(this.game, this.currPlayer), board.getSavedGameName());
        }
    }

}
