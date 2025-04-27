package com.enicarthage.coulisses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.enicarthage.coulisses.databinding.ActivityPaymentBinding;
import com.enicarthage.coulisses.models.Billet;
import com.enicarthage.coulisses.models.Reservation;
import com.enicarthage.coulisses.models.ReservationRequest;
import com.enicarthage.coulisses.models.Spectacle;
import com.enicarthage.coulisses.network.ApiClient;
import com.enicarthage.coulisses.network.ApiReservation;
import com.enicarthage.coulisses.util.BilletSelection;
import com.enicarthage.coulisses.util.CreditCardNumberFormattingTextWatcher;
import com.enicarthage.coulisses.util.SessionManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {

    private ActivityPaymentBinding binding;
    private Spectacle spectacle;
    private ArrayList<BilletSelection> selectedBillets;
    private Long guestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent() != null) {
            spectacle = getIntent().getParcelableExtra("spectacle");
            selectedBillets = getIntent().getParcelableArrayListExtra("selected_billets");
            guestId = getIntent().getLongExtra("guest_id", -1);

            if (spectacle == null || selectedBillets == null) {
                finish(); // Close activity if data is missing
                return;
            }
        } else {
            finish();
            return;
        }

        // Vérification des données reçues
        if (selectedBillets != null) {
            for (BilletSelection bs : selectedBillets) {
                Log.d("PaymentActivity", "Catégorie: " + bs.getBillet().getCategorie()
                        + ", Qty: " + bs.getQuantity());
            }
        }

        setupUI();

        binding.btnConfirmPayment.setOnClickListener(v -> processPayment());
    }

    private void setupUI() {
        // Afficher les détails du spectacle
        binding.tvEventName.setText(spectacle.getTitre());

        // Afficher les billets sélectionnés
        StringBuilder ticketsInfo = new StringBuilder();
        double total = 0;

        for (BilletSelection selection : selectedBillets) {
            Billet billet = selection.getBillet();
            int quantity = selection.getQuantity();
            double price = billet.getPrix();
            double subtotal = price * quantity;
            total += subtotal;

            ticketsInfo.append(String.format(Locale.getDefault(),
                    "%s x%d - %.2f DT (%.2f DT chacun)\n",
                    billet.getCategorie(),
                    quantity,
                    subtotal,
                    price));
        }

        binding.tvTicketsInfo.setText(ticketsInfo.toString());
        binding.tvTotalAmount.setText(String.format(Locale.getDefault(), "%.2f DT", total));

        // Définir la date et heure actuelles comme date par défaut
        binding.etCardNumber.addTextChangedListener(new CreditCardNumberFormattingTextWatcher());
    }

    private String formatPrice(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        return format.format(amount);
    }

    private void processPayment() {
        if (!validatePaymentForm()) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifier si l'ID du guest est valide
        if (guestId == -1) {
            // Si l'ID du guest est -1, utiliser l'ID de l'utilisateur connecté
            SessionManager sessionManager = new SessionManager(this);
            if (sessionManager.isLoggedIn()) {
                guestId = sessionManager.getUserId(); // Obtenir l'ID de l'utilisateur connecté
            } else {
                Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Créer la réservation avec l'ID du client
        createReservation(guestId);

        // Simuler un paiement réussi
        Toast.makeText(this, "Paiement effectué avec succès!", Toast.LENGTH_SHORT).show();

        // Naviguer vers l'écran de confirmation
        Intent intent = new Intent(this, PaymentConfirmationActivity.class);
        intent.putExtra("spectacle", spectacle);
        intent.putParcelableArrayListExtra("selected_billets", new ArrayList<>(selectedBillets));
        startActivity(intent);
        finish();
    }

    private void createReservation(Long guestId) {
        // Créer une réservation pour chaque billet sélectionné
        for (BilletSelection billetSelection : selectedBillets) {
            Billet billet = billetSelection.getBillet();
            long quantity = billetSelection.getQuantity();

            ReservationRequest reservation = new ReservationRequest();
            reservation.setIdClient(guestId); // Définir l'ID du client
            reservation.setIdBillet(billet.getId());
            reservation.setQuantite(quantity);

            // Appeler l'API pour créer la réservation
            ApiReservation apiReservation = ApiClient.getClient().create(ApiReservation.class);
            Call<Reservation> call = apiReservation.createReservation(reservation);
            call.enqueue(new Callback<Reservation>() {
                @Override
                public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                    if (response.isSuccessful()) {
                        Log.d("PaymentActivity", "Réservation créée avec succès !");
                    } else {
                        Log.e("PaymentActivity", "Erreur lors de la création de la réservation : " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Reservation> call, Throwable t) {
                    Log.e("PaymentActivity", "Échec de la connexion : " + t.getMessage());
                }
            });
        }
    }

    private boolean validatePaymentForm() {
        boolean isValid = true;

        String cardNumber = binding.etCardNumber.getText().toString().replaceAll("\\s+", ""); // remove spaces
        String cardName = binding.etCardName.getText().toString().trim();
        String expiryDate = binding.etExpiryDate.getText().toString().trim();
        String cvv = binding.etCvv.getText().toString().trim();

        // Validate card number (simple check: 16 digits)
        if (cardNumber.isEmpty() || !cardNumber.matches("\\d{16}")) {
            binding.tilCardNumber.setError("Numéro de carte invalide (16 chiffres requis)");
            isValid = false;
        } else {
            binding.tilCardNumber.setError(null);
        }

        // Validate cardholder name (not empty)
        if (cardName.isEmpty()) {
            binding.tilCardName.setError("Nom du titulaire requis");
            isValid = false;
        } else {
            binding.tilCardName.setError(null);
        }

        // Validate expiry date (format MM/AA)
        if (!expiryDate.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            binding.tilExpiryDate.setError("Date d'expiration invalide (format MM/AA)");
            isValid = false;
        } else {
            // Check if the card is expired
            String[] parts = expiryDate.split("/");
            int expMonth = Integer.parseInt(parts[0]);
            int expYear = Integer.parseInt("20" + parts[1]); // assuming 20XX

            java.util.Calendar now = java.util.Calendar.getInstance();
            int currentMonth = now.get(java.util.Calendar.MONTH) + 1; // Month is 0-based
            int currentYear = now.get(java.util.Calendar.YEAR);

            if (expYear < currentYear || (expYear == currentYear && expMonth < currentMonth)) {
                binding.tilExpiryDate.setError("Carte expirée");
                isValid = false;
            } else {
                binding.tilExpiryDate.setError(null);
            }
        }

        // Validate CVV (3 digits)
        if (!cvv.matches("\\d{3}")) {
            binding.tilCvv.setError("CVV invalide (3 chiffres requis)");
            isValid = false;
        } else {
            binding.tilCvv.setError(null);
        }

        return isValid;
    }

}
