package com.enicarthage.coulisses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.enicarthage.coulisses.Adapter.SpectacleTitlesAdapter;
import com.enicarthage.coulisses.R;
import com.enicarthage.coulisses.models.Spectacle;
import com.enicarthage.coulisses.network.ApiClient;
import com.enicarthage.coulisses.network.SpectacleApi;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpectacleTitlesActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private SpectacleTitlesAdapter adapter;
    private SpectacleApi spectacleApi;
    private TextInputEditText searchField;
    private List<Spectacle> originalSpectacles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectacle_titles);

        recyclerView = findViewById(R.id.recyclerViewTitles);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        searchField = findViewById(R.id.searchField);
        setupSearchListener();

        spectacleApi = ApiClient.getClient().create(SpectacleApi.class);
        loadUniqueSpectacleTitles();
    }

    private void setupSearchListener() {
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSpectacles(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
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

        adapter = new SpectacleTitlesAdapter(this, filteredList, spectacle -> {
            Intent intent = new Intent(this, SpectacleListActivity.class);
            intent.putExtra("titre", spectacle.getTitre());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadUniqueSpectacleTitles() {
        spectacleApi.getAllSpectacles().enqueue(new Callback<List<Spectacle>>() {
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
                Toast.makeText(SpectacleTitlesActivity.this, "Erreur de chargement", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAdapter(List<Spectacle> spectacles) {
        adapter = new SpectacleTitlesAdapter(this, spectacles, spectacle -> {
            Intent intent = new Intent(this, SpectacleListActivity.class);
            intent.putExtra("titre", spectacle.getTitre());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }
}