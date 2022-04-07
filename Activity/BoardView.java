package com.example.canoga.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
    /******************************** Variables for UI ***********************************************/
    private TextView player1info, player2info, gameInfo;
    private RecyclerView humanBoard, computerBoard;
    private Button rollDiceBtn, helpBtn, viewMovesBtn, startNewGameBtn;
    private ImageView dice1, dice2, player1GoesFirstSign, player1TurnSign, player2GoesFirstSign, player2TurnSign;
    private BoardCustomAdapter humanBoardAdapter, computerBoardAdapter;
    private MenuItem menuItem;
    /************************************************************************************************/
    //game object for the game
    private Game game;
    //game's board
    private Board board;
    //current player in the round
    private Player currPlayer;
    //Helper function
    private Misc misc = new Misc();
    //Dice roll for current turn
    private int currDiceRoll;
    //Boolean array of dice images to show
    private int[] allDice = new int[]{R.drawable.dice0, R.drawable.dice1, R.drawable.dice2, R.drawable.dice3,
            R.drawable.dice4,
            R.drawable.dice5, R.drawable.dice6};

    /*
    Function Name: onCreate
    Purpose: Load new game and display screen contents
    Parameters: None
    Return Value: None
    Algorithm: None
    Assistance Received: None
    */

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boardview);
        loadNewGame();
        setFields();
        fillBoardDetails(false);
    }

    /*
    Function Name: onCreateOptionsMenu
    Purpose: Sets up menu item bar for save game and game info.
    Parameters: None
    Return Value: None
    Algorithm: None
    Assistance Received: None
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_game, menu);
        menuItem = menu.getItem(0);
        menuItem.setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }

    /*
    Function Name: onCreateOptionsMenu
    Purpose: Function for menu bar items
    Parameters: None
    Return Value: None
    Algorithm:
            1. Check if the menu item bar is for save game or display game help.
            2. Perform actions accordingly.
    Assistance Received: None
    */

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Function for save game click.
        if(item.getTitle().equals("Save Game")) {
            try {
                saveGameClicked();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Show help info otherwise.
        else {
            final Dialog dialog = new Dialog(this);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.help_dialog);
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    Function Name: onBackPressed
    Purpose: Display function to save game if back button is pressed.
    Parameters: None
    Return Value: None
    Algorithm: None
    Assistance Received: None
    */

    @Override
    public void onBackPressed() {
        //Alert dialog box for quit game.
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Quit Game");
        alert.setMessage("Do you want to quit game without saving?");

        //Button to save
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    saveGameClicked();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //Button to quit without saving.
        alert.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                endGame();
            }
        });

        AlertDialog alertDialog = alert.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

    }

    /*
    Function Name: fillBoardDetails
    Purpose: Display players name and score on the UI
    Parameters:
            forContinue:- boolean variable that determines whether to fill info for new game or load game.
    Return Value: None
    Algorithm: None
    Assistance Received: None
    */

    private void fillBoardDetails(boolean forContinue) {
        player1info.setText(game.getPlayer1().getName() + ":  " + String.valueOf(game.getPlayer1().getScore()));
        player2info.setText(game.getPlayer2().getName() + ":  " + String.valueOf(game.getPlayer2().getScore()));
        if(!forContinue) {
            this.fillGameInfo("Press Start!");
        }
    }

    /*
    Function Name: startNewGame
    Purpose: Start game button click function
    Parameters: None
    Return Value: None
    Algorithm:
            1. Roll dice for both players and determine first player.
            2. Set up currPlayer and currDiceRoll member variables.
    Assistance Received: None
    */

    public void startNewGame(View v) {
        this.startNewGameBtn.setEnabled(false);

        //boolean variable that is true only if two dice rolls are different.
        boolean gameBegin = false;

        //Game begins with button title as Start, change to Continue after first dice roll.
        if(this.startNewGameBtn.getText().equals("Start")) {
            ArrayList<Integer> diceSum = this.game.getPlayer1().rollDice(this.board, true);
            this.viewDice(diceSum);
            currDiceRoll = misc.getDiceRollSum(diceSum);
            displayDiceRoll(this.game.getPlayer1().getName(), misc.getDiceRollSum(diceSum));
            this.startNewGameBtn.setText("Continue");
        }
        //Roll dice for second player and check
        else if(this.startNewGameBtn.getText().equals("Continue")) {
            ArrayList<Integer> diceSum = this.game.getPlayer2().rollDice(this.board,true);
            this.viewDice(diceSum);
            displayDiceRoll(this.game.getPlayer2().getName(), misc.getDiceRollSum(diceSum));
            int anotherDice = misc.getDiceRollSum(diceSum);

            //Set up parameters in the game object
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
            //Both dice rolls are equal, change button text back to Start and start over again.
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

        //Set up dice resources and help buttons if gameBegin is true.
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

    /*
    Function Name: onTileClick
    Purpose: Cover/Uncover tiles with a click in the recyclerView element.
    Parameters:
            position:- integer index of arrayList for the recyclerView.
    Return Value: None
    Algorithm:
            1. This is strictly for human players only, recyclerViews touch is disabled if computer plays.
            2. Check if the dice roll equals the sum of tiles changed and make moves.
    Assistance Received: None
    */

    @Override
    public void onTileClick(int position) {
        if(this.currPlayer != null) {
            //variable to track how many tiles can still be moved.
            int tileSumLeft;
            //Subtract tiles left from currDiceRoll and make recyclerViews unclickable after no moves are available.
            if((tileSumLeft = this.currPlayer.makeMove(this.board, this.currPlayer.getCoverChoice(), this.currDiceRoll, position)) > 0) {
                this.displayTileState(position + 1);
                this.currDiceRoll -= tileSumLeft;

                //Reset UI to avoid any more clicks.
                if(this.currDiceRoll == 0) {
                    String gameInfoText = this.gameInfo.getText().toString();
                    gameInfoText += (!this.game.gameOver()) ? "Roll Again!" : "";
                    this.fillGameInfo(gameInfoText);
                    restrictBoardAfterChoice(true);
                    this.rollDiceBtn.setEnabled(true);
                    this.rollDiceBtn.setText("Roll Dice");
                    checkGameOver();
                    this.menuItem.setEnabled(true);
                }
            }
            //When no more moves are left, display no moves toast.
            else {
                this.displayNoMoves();
            }
            refreshView(position);
        }
    }

    /*
    Function Name: helpButtonClick
    Purpose: Action for help button click.
    Parameters: None
    Return Value: None
    Algorithm:
            1. Check if player is computer or human.
            2. If human, display help and if computer, display steps and make moves.
    Assistance Received: None
    */

    public void helpButtonClick(View v) {
        if(!(this.currPlayer instanceof Computer)) {
            this.displayHelp(false);
        }
        else {
            this.displayHelp(true);
        }
    }

    /*
    Function Name: viewMovesButtonClick
    Purpose: Action for help button click.
    Parameters: None
    Return Value: None
    Algorithm:
            1. Check if player is computer or human.
            2. If human, display all available moves, not available for computer.
    Assistance Received: None
    */

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

    /*
    Function Name: rollDice
    Purpose: Action for roll dice button click.
    Parameters: None
    Return Value: None
    Algorithm:
            1. Check if player is computer or human.
            2. AS this button is used to both roll dice and make moves, perform actions according to button name.
    Assistance Received: None
    */

    public void rollDice(View v) {
        //If button name 'Roll Dice' roll dice and change button title to make move.
        if(this.rollDiceBtn.getText().equals("Roll Dice")) {
            this.menuItem.setEnabled(false);
            rollDice();
        }

        //If button name 'make move' make moves according to player derived classed.
        else if(this.rollDiceBtn.getText().equals("Make Move")) {
            //If player is computer, make moves.
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

                this.displayComputerMoves();
                this.menuItem.setEnabled(true);
            }
            //If player human, show dialog box for cover or uncover board.
            else {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Next Move");
                alert.setMessage("Choose if you want to cover your own tiles or uncover " + misc.getOpponent(this.game, this.currPlayer)
                        + "'s tiles.");

                //Action for cover
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

                //Action for Uncover.
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

        //game over
        else if(this.rollDiceBtn.getText().equals("Wrap Up")) {
            roundOverNotification();
        }
    }

    /*
    Function Name: loadNewGame
    Purpose: Load a new game
    Parameters: None
    Return Value: None
    Algorithm:
            1. Get information from intent to know whether game is new or loaded.
            2. Initialize the game object accordingly.
    Assistance Received: None
    */

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadNewGame() {

        //For new game
        if(getIntent().getExtras().get("gameMode").equals("newGame")) {
            String player1Name = getIntent().getExtras().getString("player1Name");
            String player2Name = getIntent().getExtras().getString("player2Name");
            int boardSize = Integer.valueOf(getIntent().getExtras().getString("boardSize"));
            game = new Game(player1Name, player2Name, boardSize);
        }
        //For load game
        else {
            game = new Game(getIntent().getExtras().getString("fileName"));
        }

        //Set up board object
        board = game.getNewBoard();
    }

    /*
    Function Name: checkGameOver
    Purpose: Check if game is over for the game object and make changes to the UI.
    Parameters: None
    Return Value: None
    Algorithm: None
    Assistance Received: None
    */

    private void checkGameOver() {
        if(this.game.gameOver()) {
            restrictBoardAfterChoice(true);
            fillGameInfo(this.gameInfo.getText().toString() + "\nRound over. Click on Wrap Up Button to view scores and other options!");
            rollDiceBtn.setText("Wrap Up");
        }
    }

    /*
    Function Name: rollDice
    Purpose: Roll a dice from player
    Parameters: None
    Return Value: None
    Algorithm:
            1. If the player is computer, change help button text to Computer steps.
            2. If there are dice combinations loaded in the board from a file, do not display one dice roll option.
    Assistance Received: None
    */

    private void rollDice() {
        //Fill up game info
        this.fillGameInfo("Rolling dice for " + currPlayer.getName());

        //For computer
        if(this.currPlayer instanceof Computer) {
            ArrayList<Integer> currDiceCombo = this.currPlayer.rollDice(this.board, !this.currPlayer.oneDicePossible(this.board));
            rollDiceHelper(currDiceCombo);
            this.helpBtn.setText("Computer Steps");
        }

        //For player, display one dice option if dice not loaded from file and if possible to roll one dice.
        else {
            if(this.board.getDiceCombinations().isEmpty()) {
                if(this.currPlayer.oneDicePossible(this.board)) {
                    //Dialog box for one dice option.
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("One Dice Roll");
                    alert.setMessage("You have the option to roll only one die. Do you want to roll only one dice?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ArrayList<Integer> currDiceCombo = currPlayer.rollDice(board, false);
                            rollDiceHelper(currDiceCombo);
                        }
                    });
                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ArrayList<Integer> currDiceCombo = currPlayer.rollDice(board,true);
                            rollDiceHelper(currDiceCombo);
                        }
                    });

                    AlertDialog alertDialog = alert.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
                else {
                    ArrayList<Integer> currDiceCombo = currPlayer.rollDice(board, true);
                    rollDiceHelper(currDiceCombo);
                }
            }
            else {
                ArrayList<Integer> currDiceCombo = currPlayer.rollDice(board, true);
                rollDiceHelper(currDiceCombo);
            }
            this.helpBtn.setText("Help");
        }
    }

    /*
    Function Name: rollDiceHelper
    Purpose: Check if any more move is available for current dice roll and set up UI.
    Parameters:
            currDiceCombo:- ArrayList of Integers that contains two dice rolls.
    Return Value: None
    Algorithm:
            1. If moves are available, set up rollDiceBtn title to 'Make Move'.
            2. Otherwise, change turns
    Assistance Received: None
    */

    private void rollDiceHelper(ArrayList<Integer> currDiceCombo) {
        this.viewDice(currDiceCombo);
        //Set up game and view moves buttons.
        enableHelpButtons(true, false);
        //Set up currDiceRoll
        this.currDiceRoll = misc.getDiceRollSum(currDiceCombo);
        this.displayDiceRoll(currPlayer.getName(), this.currDiceRoll);

        //Moves available
        if(this.currPlayer.checkForMove(this.board, this.currDiceRoll)) {
            this.rollDiceBtn.setText("Make Move");
        }
        //No more moves, switch turns.
        else {
            this.fillGameInfo(this.currPlayer.getName() + " has no more moves for current dice roll. It is " +
                    misc.getOpponent(this.game, this.currPlayer) + "'s turn!");
            this.currPlayer = misc.switchPlayer(this.game, this.currPlayer);
            this.displayTurns();
        }
    }

    /*
    Function Name: nextMoveMade
    Purpose: Fill game info and set recyclerViews to un-clickable.
    Parameters:
            coverChoice:- boolean, determines whether to cover own tiles or uncover opponent tiles.
    Return Value: None
    Algorithm: None
    Assistance Received: None
    */

    private void nextMoveMade(boolean coverChoice) {
        this.rollDiceBtn.setText("Roll Dice");
        this.rollDiceBtn.setEnabled(false);
        //Set up help buttons
        enableHelpButtons(true, true);
        if(coverChoice) {
            this.fillGameInfo("Click on your tiles to cover!");
        }
        else {
            this.fillGameInfo("Click on " + misc.getOpponent(this.game, this.currPlayer) + "'s tiles to uncover");
        }
        restrictBoardAfterChoice(false);
    }

    /*
    Function Name: roundOverNotification
    Purpose: Function for after round over.
    Parameters: None
    Return Value: None
    Algorithm:
            1. Display dialog box with round winner and options to continue or end game.
            2. Perform actions according to button click.
    Assistance Received: None
    */

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

        //get round score from game
        int roundScore = game.getNewRound().updateScore(this.board, this.game.getPlayer1(), this.game.getPlayer2());
        //Set winner info
        winnerInfo.setText(this.game.getNewRound().getWinner() + ": " + String.valueOf(Math.abs(roundScore)));

        //Function for View score button, display scores.
        viewScoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAllScores(false);
            }
        });

        //Function for end game button, display scores and end game.
        endGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAllScores(true);
            }
        });

        //Function for continue game button, display the dialog to choose board size and reset game
        continueGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Dialog to choose board size, fill up player names and disable textview.
                final Dialog nDialog = new Dialog(BoardView.this);
                nDialog.setCancelable(false);
                nDialog.setContentView(R.layout.start_game_dialog);
                //Initializing the views of the dialog
                final TextView boardSizeTextView = nDialog.findViewById(R.id.board_size_text_id);
                final EditText player1Name = nDialog.findViewById(R.id.player1_name_id);
                final EditText player2Name = nDialog.findViewById(R.id.player2_name_id);
                final Spinner boardSizeSpinner = nDialog.findViewById(R.id.boardSize_spinner_id);

                player1Name.setText(game.getPlayer1().getName());
                player2Name.setText(game.getPlayer2().getName());
                player1Name.setEnabled(false);
                player2Name.setEnabled(false);

                boardSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        boardSizeTextView.setText(String.valueOf(9 + i));
                        board.setBoardSize(9 + i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                final Button beginGameBtn = nDialog.findViewById(R.id.beginGame_btn);

                //Begin game button click function.
                beginGameBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fillGameInfo(game.continueGame(roundScore, board.getBoardSize()));
                        humanBoardAdapter.notifyDataSetChanged();
                        computerBoardAdapter.notifyDataSetChanged();
                        dialog.cancel();
                        nDialog.cancel();
                        resetButtonsForNewGame();
                        fillBoardDetails(true);
                    }
                });

                nDialog.show();
            }
        });

        dialog.show();
    }

    /*
    Function Name: displayAllScores
    Purpose: FDisplay all scores and end game if option is chosen
    Parameters:
            gameEnd:- boolean, end game if true.
    Return Value: None
    Algorithm:
            1. Display scores and announce winner if gameEnd is true.
    Assistance Received: None
    */

    private void displayAllScores(boolean gameEnd) {
        //Alert to show scores.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SCORES");
        String message = this.game.getPlayer1() + "\n" + this.game.getPlayer2() + "\n";

        //End game if gameEnd is true.
        if(gameEnd) {
            builder.setCancelable(false);
            if(this.game.getWinner() != null) {
                message += "Winner: " + this.game.getWinner().getName();
            }
            else {
                message += "Game ended in a draw!";
            }
            builder.setPositiveButton("End Game", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    endGame();
                }
            });
        }
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*
    Function Name: displayNextMoves
    Purpose: Display all moves for current cover choice for currPlayer
    Parameters: None
    Return Value: None
    Algorithm: None
    Assistance Received: None
    */

    private void displayNextMoves() {
        //Builder to show all available moves.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Available moves\n");
        builder.setMessage(misc.getMoves(this.currPlayer.getPossibleTiles()));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*
    Function Name: displayHelp
    Purpose: Display help dialog
    Parameters:
            forComputer:- boolean, determines to ask help for player or computer.
    Return Value: None
    Algorithm: None
    Assistance Received: None
    */

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

    /*
    Function Name: saveGameClicked
    Purpose: Save current game to a text file.
    Parameters: None
    Return Value: None
    Algorithm:
            1. If the game has no saved game name, display a alert dialog to ask for user input for game name.
            2. If game name already present, save in the same text file.
    Assistance Received: None
    */

    private void saveGameClicked() throws IOException {
        if(this.board.getSavedGameName().equals("Null00")) {
            //Dialog for new game save
            final Dialog dialog = new Dialog(this);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.save_game_dialog);

            final EditText gameName = dialog.findViewById(R.id.game_name_text_id);
            final Button saveGameButton = dialog.findViewById(R.id.save_game_btn_id);

            saveGameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList allFiles = misc.gameNames(BoardView.this);

                    //Check if file name already present in the directory.
                    if(!allFiles.contains(gameName.getText().toString())) {
                        if(misc.checkSaveGameName(gameName.getText().toString())) {
                            board.setSavedGameName(gameName.getText().toString());
                            try {
                                currPlayer.saveCurrentGame(BoardView.this, board, game.getPlayer1(), game.getPlayer2(), gameName.getText().toString());
                                endGame();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            Toast toast = new Toast(BoardView.this);
                            toast.setText("Game name cannot contain a space.");
                            toast.show();
                        }
                    }
                    //If file name present, display toast saying the same.
                    else {
                        Toast toast = new Toast(BoardView.this);
                        toast.setText("A game with this name already exists. Please try a different name.");
                        toast.show();
                    }
                }
            });
            dialog.show();
        }
        else {
            this.currPlayer.saveCurrentGame(this, this.board, game.getPlayer1(), game.getPlayer2(), board.getSavedGameName());
            endGame();
        }
    }

    /*
    Function Name: endGame
    Purpose: Go to main activity
    Parameters: None
    Return Value: None
    Algorithm: None
    Assistance Received: None
    */

    private void endGame() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /****************************************** UI FUNCTIONS *************************************************************/

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

        if (getIntent().getExtras().get("gameMode").equals("resumeGame")) {
            displayTurns();
            this.startNewGameBtn.setEnabled(false);
            this.startNewGameBtn.setVisibility(View.INVISIBLE);
            this.rollDiceBtn.setEnabled(true);
            this.rollDiceBtn.setVisibility(View.VISIBLE);
            this.currPlayer = misc.getCurrentPlayer(game, board);
            this.helpBtn.setVisibility(View.VISIBLE);
            this.viewMovesBtn.setVisibility(View.VISIBLE);
        }
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
        toast.setText("Cannot " + ((currPlayer.getCoverChoice()) ? "cover your" : "uncover opponent's ") + " tile with the current dice roll.");
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

    private void displayComputerMoves() {
        if(this.currPlayer instanceof Computer) {
            this.fillGameInfo("Computer has " + ((this.currPlayer.getCoverChoice()) ? "covered" : "uncovered") + " tile(s) " + ((Computer) this.currPlayer).getBestCombination());
            checkGameOver();
        }
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

    private void displayDiceRoll(String playerName, int diceRoll) {
        fillGameInfo("Rolling dice for " + playerName + "\n" + playerName + " rolled " + diceRoll);
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

    private void fillGameInfo(String gameInfo) {
        this.gameInfo.setText(gameInfo);
        this.gameInfo.scrollTo(0, 0);
    }

    /**************************************************************************************************************/

}
