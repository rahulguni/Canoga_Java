package com.example.canoga.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.canoga.Model.Game;
import com.example.canoga.R;

public class MainActivity extends AppCompatActivity {
    Button startGame, resumeGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startGame = findViewById(R.id.startGameBtn);
        resumeGame = findViewById(R.id.resumeGameBtn);
    }

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

    public void resumeGameOptions(View v) {

    }

    private void startNewGame(int players) {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.start_game_dialog);

        Intent intent = new Intent(this, BoardView.class);
        Bundle bundle = new Bundle();

        //Initializing the views of the dialog
        final TextView boardSizeTextView = dialog.findViewById(R.id.board_size_text_id);
        final EditText player1Name = dialog.findViewById(R.id.player1_name_id);
        final EditText player2Name = dialog.findViewById(R.id.player2_name_id);
        final Spinner boardSizeSpinner = dialog.findViewById(R.id.boardSize_spinner_id);
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

        beginGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("player1Name", player1Name.getText().toString());
                bundle.putString("player2Name", player2Name.getText().toString());
                bundle.putString("boardSize", String.valueOf(boardSizeSpinner.getSelectedItemPosition() + 9));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        if(players == 0) {
            player2Name.setEnabled(false);
            player2Name.setText("Computer");
        }

        dialog.show();
    }

    private void resumeGame() {

    }

}