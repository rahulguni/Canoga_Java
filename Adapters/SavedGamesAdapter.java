package com.example.canoga.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canoga.R;

import java.util.ArrayList;

public class SavedGamesAdapter extends RecyclerView.Adapter<SavedGamesAdapter.RecyclerViewHolder>{

    Context parentContext;
    ArrayList<String> savedGames;
    private OnSaveGameClickListener onSaveGameClickListener;

    public SavedGamesAdapter(Context parentContext, ArrayList<String> savedGames, OnSaveGameClickListener onSaveGameClickListener) {
        this.parentContext = parentContext;
        this.savedGames = savedGames;
        this.onSaveGameClickListener = onSaveGameClickListener;
    }

    @NonNull
    @Override
    public SavedGamesAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parentContext).inflate(R.layout.saved_game_outline, parent, false);
        SavedGamesAdapter.RecyclerViewHolder recyclerViewHolder = new SavedGamesAdapter.RecyclerViewHolder(view, this.onSaveGameClickListener);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SavedGamesAdapter.RecyclerViewHolder holder, int position) {
        holder.gameName.setText(this.savedGames.get(position));
    }

    @Override
    public int getItemCount() {
        return this.savedGames.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView gameName;
        OnSaveGameClickListener onSaveGameClickListener;

        public RecyclerViewHolder(@NonNull View itemView, OnSaveGameClickListener onSaveGameClickListener) {
            super(itemView);
            this.gameName = itemView.findViewById(R.id.saved_game_name_id);
            this.onSaveGameClickListener = onSaveGameClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onSaveGameClickListener.loadCurrGame(getAdapterPosition());
        }
    }

    public interface OnSaveGameClickListener {
        void loadCurrGame(int position);
    }
}
