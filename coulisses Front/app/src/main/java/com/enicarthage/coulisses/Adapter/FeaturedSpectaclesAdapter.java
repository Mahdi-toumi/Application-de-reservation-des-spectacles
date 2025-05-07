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

public class FeaturedSpectaclesAdapter extends RecyclerView.Adapter<SpectacleTitlesAdapter.SpectacleTitleViewHolder>  {

    private final Context context;
    private List<Spectacle> spectacleList;
    private final SpectacleTitlesAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Spectacle spectacle);
    }

    public FeaturedSpectaclesAdapter(Context context, List<Spectacle> spectacleList, SpectacleTitlesAdapter.OnItemClickListener listener) {
        this.context = context;
        this.spectacleList = spectacleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SpectacleTitlesAdapter.SpectacleTitleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_featured_spectacle, parent, false);
        return new SpectacleTitlesAdapter.SpectacleTitleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpectacleTitlesAdapter.SpectacleTitleViewHolder holder, int position) {
        Spectacle spectacle = spectacleList.get(position);

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

        ImageView image;

        public SpectacleTitleViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.spectacleImage);
        }
    }
}
