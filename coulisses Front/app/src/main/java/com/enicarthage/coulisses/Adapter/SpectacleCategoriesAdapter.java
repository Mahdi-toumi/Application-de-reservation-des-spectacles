package com.enicarthage.coulisses.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enicarthage.coulisses.R;

import java.util.List;

public class SpectacleCategoriesAdapter extends RecyclerView.Adapter<SpectacleCategoriesAdapter.CategoryViewHolder> {

    private final List<String> categories;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String category);
    }

    public SpectacleCategoriesAdapter(List<String> categories, OnItemClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.bind(category, listener);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryText;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(R.id.categoryText);
        }

        public void bind(final String category, final OnItemClickListener listener) {
            categoryText.setText(category);
            itemView.setOnClickListener(v -> listener.onItemClick(category));
        }
    }
}