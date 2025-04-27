package com.enicarthage.coulisses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.enicarthage.coulisses.databinding.ActivityGuestInfoBinding;
import com.enicarthage.coulisses.models.Guest;
import com.enicarthage.coulisses.models.Spectacle;
import com.enicarthage.coulisses.network.ApiClient;
import com.enicarthage.coulisses.network.ApiGuest;
import com.enicarthage.coulisses.util.BilletSelection;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuestInfoActivity extends AppCompatActivity {

    private ActivityGuestInfoBinding binding;
    private Spectacle spectacle;
    private ArrayList<BilletSelection> selectedBillets;

    private Guest guesttopass ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuestInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent() != null) {
            spectacle = getIntent().getParcelableExtra("spectacle");
            selectedBillets = getIntent().getParcelableArrayListExtra("selected_billets");

            if (spectacle == null || selectedBillets == null) {
                finish(); // Close activity if data is missing
                return;
            }
        } else {
            finish();
            return;
        }

        binding.btnContinue.setOnClickListener(v -> validateGuestInfo());
    }

    private void validateGuestInfo() {
        if (!validateForm()) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        Guest guest = new Guest();
        guest.setNom(binding.etNom.getText().toString().trim());
        guest.setPrenom(binding.etPrenom.getText().toString().trim());
        guest.setEmail(binding.etEmail.getText().toString().trim());
        guest.setTel(binding.etTelephone.getText().toString().trim());

        ApiGuest apiGuest = ApiClient.getClient().create(ApiGuest.class);
        Call<Guest> call = apiGuest.createGuest(guest);
        call.enqueue(new Callback<Guest>() {
            @Override
            public void onResponse(Call<Guest> call, Response<Guest> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(GuestInfoActivity.this, "Invité ajouté avec succès !", Toast.LENGTH_SHORT).show();

                    guesttopass = response.body();
                    proceedToPayment();
                } else {
                    Toast.makeText(GuestInfoActivity.this, "Erreur : " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Guest> call, Throwable t) {
                Toast.makeText(GuestInfoActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
            }
        });

       // proceedToPayment();
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Réinitialiser les erreurs
        binding.tilNom.setError(null);
        binding.tilPrenom.setError(null);
        binding.tilEmail.setError(null);
        binding.tilTelephone.setError(null);

        // Vérification du nom
        String nom = binding.etNom.getText().toString().trim();
        if (nom.isEmpty()) {
            binding.tilNom.setError("Champ obligatoire");
            isValid = false;
        }

        // Vérification du prénom
        String prenom = binding.etPrenom.getText().toString().trim();
        if (prenom.isEmpty()) {
            binding.tilPrenom.setError("Champ obligatoire");
            isValid = false;
        }

        // Vérification de l'email
        String email = binding.etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            binding.tilEmail.setError("Champ obligatoire");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError("Email invalide");
            isValid = false;
        }

        // Vérification du téléphone
        String tel = binding.etTelephone.getText().toString().trim();
        if (tel.isEmpty()) {
            binding.tilTelephone.setError("Champ obligatoire");
            isValid = false;
        } else if (!tel.matches("\\d{8}")) { // Exemple pour un téléphone tunisien à 8 chiffres
            binding.tilTelephone.setError("Téléphone invalide");
            isValid = false;
        }

        return isValid;
    }



    private void proceedToPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        passDataToIntent(intent);
        startActivity(intent);
    }

    private void passDataToIntent(Intent intent) {
        intent.putExtra("spectacle", spectacle);
        intent.putParcelableArrayListExtra("selected_billets", new ArrayList<>(selectedBillets));

        if (guesttopass != null && guesttopass.getId() != null) {
            intent.putExtra("guest_id", guesttopass.getId());
        }

        // Ajouter les infos invité si nécessaire
    }
}