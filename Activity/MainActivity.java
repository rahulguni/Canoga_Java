package com.example.canoga.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canoga.Adapters.SavedGamesAdapter;
import com.example.canoga.Model.Game;
import com.example.canoga.Model.Misc;
import com.example.canoga.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SavedGamesAdapter.OnSaveGameClickListener {
    Button startGame, resumeGame;
    //ArrayList of Strings that will contain all saved file names
    ArrayList<String> savedGames = new ArrayList<>();
    //helper function
    Misc misc = new Misc();

    /*
    Function Name: onCreate
    Purpose: Assign Buttons to ids.
    Parameters: None
    Return Value: None
    Algorithm: None
    Assistance Received: None
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startGame = findViewById(R.id.startGameBtn);
        resumeGame = findViewById(R.id.resumeGameBtn);
    }

    /*
    Function Name: loadCurrGame
    Purpose: Load new game and display screen contents
    Parameters:
        position: Integer index of savedGames arraylist for recyclerView.
    Return Value: None
    Algorithm:
            1. Migrate to BoardView Class with necessary bundle.
    Assistance Received: None
    */

    @Override
    public void loadCurrGame(int position) {
        Intent intent = new Intent(this, BoardView.class);
        Bundle bundle = new Bundle();
        bundle.putString("gameMode", "resumeGame");
        bundle.putString("fileName", this.getFilesDir() + "/savedGames/" + this.savedGames.get(position) + ".txt");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /*
    Function Name: newGameOptions
    Purpose: Present option to play human vs human or human vs computer
    Parameters: None
    Return Value: None
    Algorithm: None
    Assistance Received: None
    */

    public void newGameOptions(View v) {
        String[] option = {"Player vs Computer", "Player vs Player"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a game mode");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startNewGame(i);
            }
        });
        builder.show();
    }

    /*
    Function Name: resumeGameOptions
    Purpose: Present dialog that has all saved files for game.
    Parameters: None
    Return Value: None
    Algorithm:
            1. Migrate to BoardView activity that initializes the game object with fileName from savedFiles.
    Assistance Received: None
    */

    public void resumeGameOptions(View v) {
        this.savedGames = misc.gameNames(this);

        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.load_game_dialog);

        final RecyclerView savedGamesView = dialog.findViewById(R.id.saved_games_recycler_view_id);
        SavedGamesAdapter savedGamesAdapter = new SavedGamesAdapter(dialog.getContext(), this.savedGames, this);
        savedGamesView.setAdapter(savedGamesAdapter);
        savedGamesView.setLayoutManager(new LinearLayoutManager(this));

        dialog.show();
    }

    /*
    Function Name: startNewGame
    Purpose: Present a dialog and migrate to  BoardView class with necessary information.
    Parameters:
        players:- integer that determines if two humans are playing or one.
    Return Value: None
    Algorithm:
            1. Display dialog that has player name fields and board size spinner.
            2. Start activity with intent and bundle with data from dialog box.
    Assistance Received: None
    */

    private void startNewGame(int players) {
        //Dialog box
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.start_game_dialog);

        //Intent for BoardView Class
        Intent intent = new Intent(this, BoardView.class);
        Bundle bundle = new Bundle();

        //Initializing the views of the dialog
        final TextView boardSizeTextView = dialog.findViewById(R.id.board_size_text_id);
        final EditText player1Name = dialog.findViewById(R.id.player1_name_id);
        final EditText player2Name = dialog.findViewById(R.id.player2_name_id);
        final Spinner boardSizeSpinner = dialog.findViewById(R.id.boardSize_spinner_id);
        //change board size text when spinner is changed.
        boardSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                boardSizeTextView.setText(String.valueOf(9 + i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        final Button beginGameBtn = dialog.findViewById(R.id.beginGame_btn);

        //begin game button
        beginGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(misc.checkPlayerName(player1Name.getText().toString())) {
                    bundle.putString("player1Name", player1Name.getText().toString());
                    bundle.putString("player2Name", player2Name.getText().toString());
                    bundle.putString("boardSize", String.valueOf(boardSizeSpinner.getSelectedItemPosition() + 9));
                    bundle.putString("gameMode", "newGame");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else {
                    Toast toast = new Toast(dialog.getContext());
                    toast.setText("Invalid Player Name");
                    toast.show();
                }
            }
        });

        if(players == 0) {
            player2Name.setEnabled(false);
            player2Name.setText("Computer");
        }

        dialog.show();
    }

}