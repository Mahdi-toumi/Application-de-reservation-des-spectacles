package com.enicarthage.coulisses.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.enicarthage.coulisses.Adapter.SpectacleAdapter;
import com.enicarthage.coulisses.R;
import com.enicarthage.coulisses.models.Spectacle;
import com.enicarthage.coulisses.network.ApiClient;
import com.enicarthage.coulisses.network.SpectacleApi;
import com.enicarthage.coulisses.util.CursorUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpectacleListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SpectacleAdapter adapter;
    private SpectacleApi spectacleApi;
    private MaterialButton btnToggleFilters;
    private View filtersMainContainer;
    private boolean filtersVisible = false;
    private TextInputEditText searchField, lieuFilter, dateFilter, timeFilter, priceFilter;
    private TextView titreText;
    private List<Spectacle> originalSpectacleList = new ArrayList<>();
    private Map<Long, Double> originalPrixMinMap = new HashMap<>();
    private String filterTitre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectacle_list);

        filterTitre = getIntent().getStringExtra("titre");
        initViews();
        setupListeners();
        setupDateTimePickers(); // Nouvelle méthode pour configurer les pickers

        spectacleApi = ApiClient.getClient().create(SpectacleApi.class);
        loadSpectacles();
    }

    private void setupDateTimePickers() {
        // Configurer le DatePicker pour le champ date
        dateFilter.setFocusable(false);
        dateFilter.setOnClickListener(v -> showDatePicker());

        // Configurer le TimePicker pour le champ heure
        timeFilter.setFocusable(false);
        timeFilter.setOnClickListener(v -> showTimePicker());
    }

    private void showDatePicker() {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    dateFilter.setText(sdf.format(calendar.getTime()));
                    filterSpectacles();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePicker = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    timeFilter.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                    filterSpectacles();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true // Format 24h
        );
        timePicker.show();
    }

    private void initViews() {
        btnToggleFilters = findViewById(R.id.btnToggleFilters);
        filtersMainContainer = findViewById(R.id.filtersMainContainer);
        recyclerView = findViewById(R.id.recyclerViewSpectacles);
        searchField = findViewById(R.id.searchField);
        lieuFilter = findViewById(R.id.lieuFilter);
        dateFilter = findViewById(R.id.dateFilter);
        timeFilter = findViewById(R.id.timeFilter);
        priceFilter = findViewById(R.id.priceFilter);
        titreText = findViewById(R.id.titreText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CursorUtil.setCursorColor(searchField, R.drawable.cursor_green);

        if (filterTitre != null) {
            titreText.setText(filterTitre);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(filterTitre);
            }
        }
    }

    private void setupListeners() {
        btnToggleFilters.setOnClickListener(v -> toggleFiltersVisibility());

        TextWatcher filterWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSpectacles();
            }
        };

        searchField.addTextChangedListener(filterWatcher);
        lieuFilter.addTextChangedListener(filterWatcher);
        dateFilter.addTextChangedListener(filterWatcher);
        timeFilter.addTextChangedListener(filterWatcher);
        priceFilter.addTextChangedListener(filterWatcher);
    }

    private void toggleFiltersVisibility() {
        filtersVisible = !filtersVisible;
        filtersMainContainer.setVisibility(filtersVisible ? View.VISIBLE : View.GONE);
        btnToggleFilters.setIconResource(filtersVisible ? R.drawable.ic_filter_active : R.drawable.ic_filter);
    }

    public void clearFilters(View view) {
        searchField.setText("");
        lieuFilter.setText("");
        dateFilter.setText("");
        timeFilter.setText("");
        priceFilter.setText("");
        filterSpectacles();
    }

    private void filterSpectacles() {
        if (originalSpectacleList.isEmpty()) return;

        String searchQuery = searchField.getText().toString().toLowerCase();
        String lieuQuery = lieuFilter.getText().toString().toLowerCase();
        String dateQuery = dateFilter.getText().toString();
        String timeQuery = timeFilter.getText().toString();
        String priceQuery = priceFilter.getText().toString();

        List<Spectacle> filteredList = new ArrayList<>();
        Map<Long, Double> filteredPrixMap = new HashMap<>();

        for (Spectacle spectacle : originalSpectacleList) {
            // Filtre principal par titre si présent dans l'Intent
            if (filterTitre != null && !spectacle.getTitre().equals(filterTitre)) {
                continue;
            }

            boolean matches = true;

            // Filtre par recherche texte
            if (!searchQuery.isEmpty()) {
                matches &= spectacle.getTitre().toLowerCase().contains(searchQuery) ||
                        (spectacle.getDescription() != null &&
                                spectacle.getDescription().toLowerCase().contains(searchQuery));
            }

            // Filtre par lieu
            if (!lieuQuery.isEmpty() && spectacle.getLieu() != null) {
                matches &= spectacle.getLieu().getNom().toLowerCase().contains(lieuQuery);
            }

            // Filtre par date
            if (!dateQuery.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date filterDate = sdf.parse(dateQuery);
                    Date spectacleDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(spectacle.getDate());
                    matches &= sdf.format(spectacleDate).equals(dateQuery);
                } catch (ParseException e) {
                    matches = false;
                }
            }

            // Filtre par heure
            if (!timeQuery.isEmpty()) {
                String spectacleTime = spectacle.getFormattedHeureDebut();
                matches &= spectacleTime.equals(timeQuery);
            }

            // Filtre par prix
            if (!priceQuery.isEmpty()) {
                try {
                    double maxPrice = Double.parseDouble(priceQuery);
                    Double spectaclePrice = originalPrixMinMap.get(spectacle.getId());
                    matches &= spectaclePrice != null && spectaclePrice <= maxPrice;
                } catch (NumberFormatException e) {
                    matches = false;
                }
            }

            if (matches) {
                filteredList.add(spectacle);
                filteredPrixMap.put(spectacle.getId(), originalPrixMinMap.get(spectacle.getId()));
            }
        }

        if (adapter != null) {
            adapter.updateList(filteredList, filteredPrixMap);
        }
    }

    private void loadSpectacles() {
        spectacleApi.getAllSpectacles().enqueue(new Callback<List<Spectacle>>() {
            @Override
            public void onResponse(Call<List<Spectacle>> call, Response<List<Spectacle>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    originalSpectacleList = response.body();
                    originalPrixMinMap = new HashMap<>();

                    if (originalSpectacleList.isEmpty()) {
                        setupAdapter(originalSpectacleList, originalPrixMinMap);
                        return;
                    }

                    loadPrices(originalSpectacleList, originalPrixMinMap);
                } else {
                    showError();
                }
            }

            @Override
            public void onFailure(Call<List<Spectacle>> call, Throwable t) {
                showError();
            }
        });
    }

    private void loadPrices(List<Spectacle> spectacles, Map<Long, Double> prixMinMap) {
        final int[] loadedCount = {0};

        for (Spectacle spectacle : spectacles) {
            spectacleApi.getBilletPrixMin(spectacle.getId()).enqueue(new Callback<Double>() {
                @Override
                public void onResponse(Call<Double> call, Response<Double> response) {
                    prixMinMap.put(spectacle.getId(), response.isSuccessful() && response.body() != null ? response.body() : 0.0);
                    if (++loadedCount[0] == spectacles.size()) {
                        setupAdapter(spectacles, prixMinMap);
                    }
                }

                @Override
                public void onFailure(Call<Double> call, Throwable t) {
                    prixMinMap.put(spectacle.getId(), 0.0);
                    if (++loadedCount[0] == spectacles.size()) {
                        setupAdapter(spectacles, prixMinMap);
                    }
                }
            });
        }
    }

    private void setupAdapter(List<Spectacle> spectacles, Map<Long, Double> prixMinMap) {
        adapter = new SpectacleAdapter(this, spectacles, prixMinMap, spectacle -> {
            Intent intent = new Intent(this, DetailSpectacleActivity.class);
            intent.putExtra("spectacle", spectacle);

            Bundle bundle = new Bundle();
            for (Map.Entry<Long, Double> entry : prixMinMap.entrySet()) {
                bundle.putDouble(entry.getKey().toString(), entry.getValue());
            }
            intent.putExtra("prixMinMapBundle", bundle);

            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        filterSpectacles(); // Applique les filtres initiaux
    }

    private void showError() {
        Toast.makeText(this, "Erreur de chargement", Toast.LENGTH_SHORT).show();
        setupAdapter(new ArrayList<>(), new HashMap<>());
    }
}