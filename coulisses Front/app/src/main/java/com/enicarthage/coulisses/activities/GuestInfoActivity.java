package com.enicarthage.coulisses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.enicarthage.coulisses.databinding.ActivityGuestInfoBinding;
import com.enicarthage.coulisses.models.Spectacle;
import com.enicarthage.coulisses.util.BilletSelection;

import java.util.ArrayList;
import java.util.List;

public class GuestInfoActivity extends AppCompatActivity {

    private ActivityGuestInfoBinding binding;
    private Spectacle spectacle;
    private List<BilletSelection> selectedBillets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuestInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Récupérer les données
        spectacle = getIntent().getParcelableExtra("spectacle");
        selectedBillets = getIntent().getParcelableArrayListExtra("selected_billets");

        binding.btnContinue.setOnClickListener(v -> validateGuestInfo());
    }

    private void validateGuestInfo() {
        if (!validateForm()) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

       // proceedToPayment();
    }

    private boolean validateForm() {
        // Implémenter la validation des champs
        return true;
    }


    /*private void proceedToPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        passDataToIntent(intent);
        startActivity(intent);
    }*/

    private void passDataToIntent(Intent intent) {
        intent.putExtra("spectacle", spectacle);
        intent.putParcelableArrayListExtra("selected_billets", new ArrayList<>(selectedBillets));
        // Ajouter les infos invité si nécessaire
    }
}