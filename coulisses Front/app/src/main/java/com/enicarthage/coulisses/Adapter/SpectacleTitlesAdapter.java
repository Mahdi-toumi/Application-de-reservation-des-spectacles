package com.enicarthage.coulisses.Adapter;

import com.enicarthage.coulisses.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.enicarthage.coulisses.models.Spectacle;

import java.util.List;

public class SpectacleTitlesAdapter extends RecyclerView.Adapter<SpectacleTitlesAdapter.SpectacleTitleViewHolder> {

    private final Context context;
    private List<Spectacle> spectacleList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Spectacle spectacle);
    }

    public SpectacleTitlesAdapter(Context context, List<Spectacle> spectacleList, OnItemClickListener listener) {
        this.context = context;
        this.spectacleList = spectacleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SpectacleTitleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_spectacle_simple, parent, false);
        return new SpectacleTitleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpectacleTitleViewHolder holder, int position) {
        Spectacle spectacle = spectacleList.get(position);
        holder.titre.setText(spectacle.getTitre());

        Glide.with(context)
                .load(spectacle.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(spectacle));
    }

    @Override
    public int getItemCount() {
        return spectacleList.size();
    }

    public static class SpectacleTitleViewHolder extends RecyclerView.ViewHolder {
        TextView titre;
        ImageView image;

        public SpectacleTitleViewHolder(@NonNull View itemView) {
            super(itemView);
            titre = itemView.findViewById(R.id.titreText);
            image = itemView.findViewById(R.id.spectacleImage);
        }
    }
}
