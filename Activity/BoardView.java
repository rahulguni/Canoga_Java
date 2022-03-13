package com.example.canoga.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canoga.Adapters.BoardCustomAdapter;
import com.example.canoga.Model.Board;
import com.example.canoga.Model.Game;
import com.example.canoga.Model.Player;
import com.example.canoga.R;

import java.util.ArrayList;

public class BoardView extends AppCompatActivity implements BoardCustomAdapter.OnTileClickListener {
    private TextView player1info, player2info;
    private RecyclerView humanBoard, computerBoard;
    private Button rollDiceBtn, helpBtn, viewMovesBtn;
    private ImageView dice1, dice2;
    private BoardCustomAdapter humanBoardAdapter, computerBoardAdapter;
    private Game game;
    private Board board;
    private Player currPlayer;
    private int[] allDice = new int[]{R.drawable.dice1, R.drawable.dice2, R.drawable.dice3,
            R.drawable.dice4,
            R.drawable.dice5, R.drawable.dice6};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boardview);

        loadNewGame();

        player1info = findViewById(R.id.player1_info_id);
        player2info = findViewById(R.id.player2_info_id);
        humanBoard = findViewById(R.id.humanBoard_recyclerview_id);
        computerBoard = findViewById(R.id.computerBoard_recyclerview_id);
        dice1 = findViewById(R.id.dice1_id);
        dice2 = findViewById(R.id.dice2_id);
        dice1.setImageResource(allDice[0]);
        dice2.setImageResource(allDice[5]);
        rollDiceBtn = findViewById(R.id.roll_dice_btn_id);
        helpBtn = findViewById(R.id.help_btn_id);
        viewMovesBtn = findViewById(R.id.view_moves_btn_id);
        humanBoardAdapter = new BoardCustomAdapter(this, board.getHumanBoard(), this);
        humanBoard.setAdapter(humanBoardAdapter);
        humanBoard.setLayoutManager(new LinearLayoutManager(this));
        computerBoardAdapter = new BoardCustomAdapter(this, board.getComputerBoard(), this);
        computerBoard.setAdapter(computerBoardAdapter);
        computerBoard.setLayoutManager(new LinearLayoutManager(this));

        fillBoardDetails();
    }

    void loadNewGame() {
        String player1Name = getIntent().getExtras().getString("player1Name");
        String player2Name = getIntent().getExtras().getString("player2Name");
        int boardSize = Integer.valueOf(getIntent().getExtras().getString("boardSize"));
        game = new Game(player1Name, player2Name, boardSize);
        board = game.getNewBoard();
        currPlayer = game.getPlayer1();
    }

    void fillBoardDetails() {
        player1info.setText(game.getPlayer1().getName() + ":  " + String.valueOf(game.getPlayer1().getScore()));
        player2info.setText(game.getPlayer2().getName() + ":  " + String.valueOf(game.getPlayer2().getScore()));
    }

    @Override
    public void onTileClick(int position) {

    }

    public void rollDice(View v) {

    }

}
