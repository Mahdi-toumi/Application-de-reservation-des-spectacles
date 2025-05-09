package com.enicarthage.coulisses.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.enicarthage.coulisses.R;
import com.enicarthage.coulisses.models.Billet;
import com.enicarthage.coulisses.util.BilletSelection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BilletAdapter extends RecyclerView.Adapter<BilletAdapter.BilletViewHolder> {
    private final List<Billet> billetList;
    private final Context context;
    private final OnBilletSelectedListener listener;
    private final Map<Integer, Integer> selectedQuantities = new HashMap<>();

    public interface OnBilletSelectedListener {
        void onBilletsSelected(List<BilletSelection> selectedBillets);
    }

    public BilletAdapter(List<Billet> billetList, Context context, OnBilletSelectedListener listener) {
        this.billetList = billetList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BilletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_billet, parent, false);
        return new BilletViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BilletViewHolder holder, int position) {
        Billet billet = billetList.get(position);
        int quantity = selectedQuantities.containsKey(position) ? selectedQuantities.get(position) : 0;
        int available = billet.getNombre();

        holder.categorie.setText(billet.getCategorie());
        holder.prix.setText(String.format(Locale.getDefault(), "%.2f DT", billet.getPrix()));
        holder.disponibilite.setText(String.format(Locale.getDefault(), "%d disponibles", available));
        holder.quantity.setText(quantity > 0 ? String.valueOf(quantity) : "");
        holder.error.setVisibility(View.GONE);

        // Style selon la sélection
        holder.itemView.setSelected(quantity > 0);
        holder.itemView.setBackgroundTintList(ColorStateList.valueOf(
                quantity > 0 ?
                        ContextCompat.getColor(context, R.color.accent_transparent) :
                        ContextCompat.getColor(context, R.color.dark_primary)
        ));

        // Bouton moins
        holder.btnDecrease.setOnClickListener(v -> {
            int newQty = Math.max(0, selectedQuantities.getOrDefault(position, 0) - 1);
            updateQuantity(position, newQty);
        });

        // Bouton plus
        holder.btnIncrease.setOnClickListener(v -> {
            int currentQty = selectedQuantities.getOrDefault(position, 0);
            if (currentQty < available) {
                updateQuantity(position, currentQty + 1);
            } else {
                holder.error.setText("Quantité maximale atteinte");
                holder.error.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateQuantity(int position, int newQuantity) {
        if (newQuantity > 0) {
            selectedQuantities.put(position, newQuantity);
        } else {
            selectedQuantities.remove(position);
        }
        notifyItemChanged(position);
        notifySelectionChanged();
    }

    private void notifySelectionChanged() {
        List<BilletSelection> selectedBillets = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : selectedQuantities.entrySet()) {
            int position = entry.getKey();
            int quantity = entry.getValue();
            selectedBillets.add(new BilletSelection(billetList.get(position), quantity));
        }
        listener.onBilletsSelected(selectedBillets);
    }

    @Override
    public int getItemCount() {
        return billetList.size();
    }

    public List<Billet> getBilletList() {
        return billetList;
    }

    static class BilletViewHolder extends RecyclerView.ViewHolder {
        TextView categorie, prix, disponibilite, quantity, error;
        ImageButton btnDecrease, btnIncrease;

        BilletViewHolder(@NonNull View itemView) {
            super(itemView);
            categorie = itemView.findViewById(R.id.billetCategorie);
            prix = itemView.findViewById(R.id.billetPrix);
            disponibilite = itemView.findViewById(R.id.billetDisponibilite);
            quantity = itemView.findViewById(R.id.billetQuantity);
            error = itemView.findViewById(R.id.billetError);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
        }
    }
}