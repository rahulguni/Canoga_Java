package com.example.canoga.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
    boolean isClickAble = true;

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
        if(this.isClickAble) {
            holder.setClickAble(true);
        }
        else {
            holder.setClickAble(false);
        }
        holder.tileNumber.setText(String.valueOf(position + 1));
        if(tiles.get(position) == true) {
            holder.tileNumber.setBackgroundColor(Color.BLACK);
            holder.tileNumber.setTextColor(Color.WHITE);
        }
        else {
            holder.tileNumber.setBackgroundColor(Color.WHITE);
            holder.tileNumber.setTextColor(Color.GRAY);
            holder.tileNumber.setBackgroundResource(R.drawable.tile_border);
        }
    }

    public void setClickAble(boolean isClickAble) {
        this.isClickAble = isClickAble;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tiles.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tileNumber;
        OnTileClickListener onTileClickListener;
        boolean isClickAble = true;

        public RecyclerViewHolder(@NonNull View itemView, OnTileClickListener onTileClickListener ) {
            super(itemView);
            this.tileNumber = itemView.findViewById(R.id.tile_number_id);
            this.onTileClickListener = onTileClickListener;
            itemView.setOnClickListener(this);
        }

        public void setClickAble(boolean isClickAble) {
            this.isClickAble = isClickAble;
        }

        @Override
        public void onClick(View view) {
            if(this.isClickAble){
                onTileClickListener.onTileClick(getAdapterPosition());
            }
        }
    }

    public interface OnTileClickListener {
        void onTileClick(int position);
    }
}
