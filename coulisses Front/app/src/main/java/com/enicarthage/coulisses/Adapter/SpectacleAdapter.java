package com.enicarthage.coulisses.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.enicarthage.coulisses.R;
import com.enicarthage.coulisses.models.Spectacle;
import com.enicarthage.coulisses.network.ApiClient;
import com.enicarthage.coulisses.network.SpectacleApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpectacleAdapter extends RecyclerView.Adapter<SpectacleAdapter.SpectacleViewHolder> {
    private final Context context;
    private List<Spectacle> spectacleList;
    private final OnItemClickListener listener;
    private Map<Long, Double> prixMinMap;

    public interface OnItemClickListener {
        void onItemClick(Spectacle spectacle);
    }

    public SpectacleAdapter(Context context, List<Spectacle> spectacleList,
                            Map<Long, Double> prixMinMap, OnItemClickListener listener) {
        this.context = context;
        this.spectacleList = spectacleList;
        this.listener = listener;
        this.prixMinMap = prixMinMap;
    }

    public void updateList(List<Spectacle> newList, Map<Long, Double> newPrixMap) {
        this.spectacleList = newList;
        this.prixMinMap = newPrixMap;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SpectacleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_spectacle, parent, false);
        return new SpectacleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpectacleViewHolder holder, int position) {
        Spectacle spectacle = spectacleList.get(position);
        holder.titre.setText(spectacle.getTitre());
        holder.date.setText(spectacle.getFormattedDate());
        holder.heure.setText(spectacle.getFormattedHeureDebut());
        holder.duree.setText(spectacle.getFormattedDuree());

        Double prixMin = prixMinMap.get(spectacle.getId());
        String prixText = (prixMin != null && prixMin > 0) ?
                "À partir de " + prixMin + " DT" : "Prix non disponible";
        holder.prixmin.setText(prixText);

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

    public static class SpectacleViewHolder extends RecyclerView.ViewHolder {
        TextView titre, date, heure, duree, prixmin ;
        ImageView image;

        public SpectacleViewHolder(@NonNull View itemView) {
            super(itemView);
            titre = itemView.findViewById(R.id.titreText);
            date = itemView.findViewById(R.id.dateText);
            heure = itemView.findViewById(R.id.heureText);
            image = itemView.findViewById(R.id.spectacleImage);
            duree = itemView.findViewById(R.id.spectacleDuree);
            prixmin = itemView.findViewById(R.id.spectaclePrixMin);
        }
    }
}