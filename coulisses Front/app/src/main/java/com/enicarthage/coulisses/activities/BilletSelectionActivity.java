package com.enicarthage.coulisses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.enicarthage.coulisses.Adapter.BilletAdapter;
import com.enicarthage.coulisses.R;
import com.enicarthage.coulisses.models.Billet;
import com.enicarthage.coulisses.models.Spectacle;
import com.enicarthage.coulisses.network.ApiClient;
import com.enicarthage.coulisses.network.BilletApi;
import com.enicarthage.coulisses.util.BilletSelection;
import com.enicarthage.coulisses.util.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BilletSelectionActivity extends AppCompatActivity implements BilletAdapter.OnBilletSelectedListener {

    private RecyclerView recyclerView;
    private BilletAdapter adapter;
    private BilletApi billetApi;
    private Spectacle spectacle;
    private MaterialButton btnContinue;
    private List<BilletSelection> selectedBillets = new ArrayList<>();
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billet_selection);

        sessionManager = new SessionManager(this);
        spectacle = getIntent().getParcelableExtra("spectacle");

        if (spectacle == null) {
            Toast.makeText(this, "Spectacle non disponible", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        loadBillets();

        btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setEnabled(false);
        btnContinue.setOnClickListener(v -> {
            if (!selectedBillets.isEmpty()) {
                if (sessionManager.isLoggedIn()) {
                    proceedToPayment();
                } else {
                    proceedToAuthentication();
                }
            }
        });
    }

    private void initViews() {
        TextView title = findViewById(R.id.spectacleTitle);
        TextView dateTime = findViewById(R.id.spectacleDateTime);
        TextView lieu = findViewById(R.id.spectacleLieu);

        title.setText(spectacle.getTitre());
        dateTime.setText(String.format("%s • %s",
                spectacle.getFormattedDate(),
                spectacle.getFormattedHeureDebut()));
        lieu.setText(spectacle.getLieu().getNom());
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.billetRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new BilletAdapter(new ArrayList<>(), this, this);
        recyclerView.setAdapter(adapter);
    }

    private void loadBillets() {
        billetApi = ApiClient.getClient().create(BilletApi.class);
        billetApi.getBilletBySpectacleId(spectacle.getId()).enqueue(new Callback<List<Billet>>() {
            @Override
            public void onResponse(Call<List<Billet>> call, Response<List<Billet>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new BilletAdapter(response.body(), BilletSelectionActivity.this,
                            BilletSelectionActivity.this);
                    recyclerView.setAdapter(adapter);

                    if (response.body().isEmpty()) {
                        showNoTicketsAvailable();
                    }
                } else {
                    showErrorLoading();
                }
            }

            @Override
            public void onFailure(Call<List<Billet>> call, Throwable t) {
                showErrorLoading();
            }
        });
    }

    private void showNoTicketsAvailable() {
        TextView emptyView = new TextView(this);
        emptyView.setText("Aucun billet disponible pour ce spectacle");
        emptyView.setTextColor(ContextCompat.getColor(this, R.color.white));
        emptyView.setTextSize(16f);
        emptyView.setGravity(Gravity.CENTER);
        ((ViewGroup) recyclerView.getParent()).addView(emptyView);
        recyclerView.setVisibility(View.GONE);
    }

    private void showErrorLoading() {
        Toast.makeText(this, "Erreur de chargement des billets", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBilletsSelected(List<BilletSelection> selectedBillets) {
        this.selectedBillets = selectedBillets;
        btnContinue.setEnabled(!selectedBillets.isEmpty());
        updateContinueButtonText();
    }

    private void updateContinueButtonText() {
        int totalBillets = 0;
        double totalPrice = 0;

        for (BilletSelection selection : selectedBillets) {
            totalBillets += selection.getQuantity();
            totalPrice += selection.getBillet().getPrix() * selection.getQuantity();
        }

        String buttonText = String.format(Locale.getDefault(),
                "Continuer (%d billets - %.2f DT)",
                totalBillets, totalPrice);
        btnContinue.setText(buttonText);
    }

    private void proceedToAuthentication() {
        Intent intent = new Intent(this, AuthChoiceActivity.class);
        intent.putExtra("spectacle", spectacle);
        intent.putParcelableArrayListExtra("selected_billets", new ArrayList<>(selectedBillets));
        startActivity(intent);
    }

    private void proceedToPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("spectacle", spectacle);
        intent.putParcelableArrayListExtra("selected_billets", new ArrayList<>(selectedBillets));
        startActivity(intent);
    }
}