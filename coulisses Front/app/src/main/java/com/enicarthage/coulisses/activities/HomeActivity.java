package com.enicarthage.coulisses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.enicarthage.coulisses.Adapter.FeaturedSpectaclesAdapter;
import com.enicarthage.coulisses.Adapter.SpectacleTitlesAdapter;
import com.enicarthage.coulisses.R;
import com.enicarthage.coulisses.models.Spectacle;
import com.enicarthage.coulisses.network.ApiClient;
import com.enicarthage.coulisses.network.SpectacleApi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private FeaturedSpectaclesAdapter adapter;
    private SpectacleApi spectacleApi;
    private TextInputEditText searchField;
    private List<Spectacle> originalSpectacles = new ArrayList<>();

        private MaterialButton btnViewAll;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);

            // Initialisation des vues
            recyclerView = findViewById(R.id.recyclerViewTitles);
            btnViewAll = findViewById(R.id.btnViewAll);

            // Configuration du layout
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

            // Gestion du clic sur le bouton
            btnViewAll.setOnClickListener(v -> {
                Intent intent = new Intent(this, SpectacleTitlesActivity.class);
                startActivity(intent);
            });

            // Chargement des données
            spectacleApi = ApiClient.getClient().create(SpectacleApi.class);
            loadUniqueSpectacleTitles();
        }



    private void filterSpectacles(String query) {
        List<Spectacle> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            filteredList.addAll(originalSpectacles);
        } else {
            String searchQuery = query.toLowerCase().trim();
            for (Spectacle spectacle : originalSpectacles) {
                if (spectacle.getTitre().toLowerCase().contains(searchQuery)) {
                    filteredList.add(spectacle);
                }
            }
        }

        adapter = new FeaturedSpectaclesAdapter(this, filteredList, spectacle -> {
            Intent intent = new Intent(this, SpectacleTitlesActivity.class);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadUniqueSpectacleTitles() {
        spectacleApi.getFeaturedSpectacles().enqueue(new Callback<List<Spectacle>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Spectacle>> call, Response<List<Spectacle>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Spectacle> allSpectacles = response.body();

                    // Filtrer pour avoir un spectacle par titre unique
                    Map<String, Spectacle> uniqueTitles = new HashMap<>();
                    for (Spectacle spectacle : allSpectacles) {
                        if (!uniqueTitles.containsKey(spectacle.getTitre())) {
                            uniqueTitles.put(spectacle.getTitre(), spectacle);
                        }
                    }

                    originalSpectacles = new ArrayList<>(uniqueTitles.values());
                    setupAdapter(originalSpectacles);
                }
            }

            @Override
            public void onFailure(Call<List<Spectacle>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Erreur de chargement", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAdapter(List<Spectacle> spectacles) {
        adapter = new FeaturedSpectaclesAdapter(this, spectacles, spectacle -> {
            Intent intent = new Intent(this, SpectacleTitlesActivity.class);
            intent.putExtra("titre", spectacle.getTitre());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }
}