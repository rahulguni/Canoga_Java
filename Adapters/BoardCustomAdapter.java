package com.example.canoga.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canoga.Activity.BoardView;
import com.example.canoga.R;

import java.util.ArrayList;

public class BoardCustomAdapter extends RecyclerView.Adapter<BoardCustomAdapter.RecyclerViewHolder> {

    Context parentContext;
    ArrayList<Boolean> tiles;
    private OnTileClickListener mOnTileListener;

    public BoardCustomAdapter(Context parentContext, ArrayList<Boolean> tiles, OnTileClickListener mOnTileListener) {
        this.parentContext = parentContext;
        this.tiles = tiles;
        this.mOnTileListener = mOnTileListener;
    }

    @NonNull
    @Override
    public BoardCustomAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parentContext).inflate(R.layout.tile_outline, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view, mOnTileListener);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BoardCustomAdapter.RecyclerViewHolder holder, int position) {
        holder.tileNumber.setText(String.valueOf(position + 1));
        if(tiles.get(position) == true) {
            holder.tileNumber.setBackgroundColor(Color.BLACK);
            holder.tileNumber.setTextColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return tiles.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tileNumber;
        OnTileClickListener onTileClickListener;

        public RecyclerViewHolder(@NonNull View itemView, OnTileClickListener onTileClickListener ) {
            super(itemView);
            this.tileNumber = itemView.findViewById(R.id.tile_number_id);
            this.onTileClickListener = onTileClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public interface OnTileClickListener {
        void onTileClick(int position);
    }
}
