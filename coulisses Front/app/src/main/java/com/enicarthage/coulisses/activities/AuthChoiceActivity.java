package com.enicarthage.coulisses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.enicarthage.coulisses.R;
import com.enicarthage.coulisses.models.Spectacle;
import com.enicarthage.coulisses.util.BilletSelection;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AuthChoiceActivity extends AppCompatActivity {

    private Spectacle spectacle;
    private List<BilletSelection> selectedBillets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_auth_choice);

        // Récupérer les données de l'intent
        spectacle = getIntent().getParcelableExtra("spectacle");
        selectedBillets = getIntent().getParcelableArrayListExtra("selected_billets");

        // Initialiser les boutons
        MaterialButton btnSignIn = findViewById(R.id.btnSignIn);
        MaterialButton btnSignUp = findViewById(R.id.btnSignUp);
        MaterialButton btnGuest = findViewById(R.id.btnGuest);

        btnSignIn.setOnClickListener(v -> navigateToSignIn());
        btnSignUp.setOnClickListener(v -> navigateToSignUp());
        btnGuest.setOnClickListener(v -> proceedAsGuest());
    }

    private void navigateToSignIn() {
        Intent intent = new Intent(this, SignInActivity.class);
        passDataToIntent(intent);
        startActivity(intent);
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        passDataToIntent(intent);
        startActivity(intent);
    }

    private void proceedAsGuest() {
        Intent intent = new Intent(this, GuestInfoActivity.class);
        passDataToIntent(intent);
        startActivity(intent);
    }

    private void passDataToIntent(Intent intent) {
        intent.putExtra("spectacle", spectacle);
        intent.putParcelableArrayListExtra("selected_billets", new ArrayList<>(selectedBillets));
    }
}