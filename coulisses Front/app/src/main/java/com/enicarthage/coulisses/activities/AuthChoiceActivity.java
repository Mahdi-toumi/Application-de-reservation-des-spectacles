package com.enicarthage.coulisses.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.enicarthage.coulisses.R;
import com.enicarthage.coulisses.models.Spectacle;
import com.enicarthage.coulisses.util.BilletSelection;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class AuthChoiceActivity extends AppCompatActivity {

    private Spectacle spectacle;
    private ArrayList<BilletSelection> selectedBillets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_auth_choice);

        if (getIntent() != null) {
            spectacle = getIntent().getParcelableExtra("spectacle");
            selectedBillets = getIntent().getParcelableArrayListExtra("selected_billets");

            if (spectacle == null || selectedBillets == null) {
                finish();
                return;
            }
        } else {
            finish();
            return;
        }

        MaterialButton btnSignIn = findViewById(R.id.btnSignIn);
        MaterialButton btnSignUp = findViewById(R.id.btnSignUp);
        MaterialButton btnGuest = findViewById(R.id.btnGuest);

        btnSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignInActivity.class);
            passDataToIntent(intent);
            intent.putExtra("previous_activity", "AuthChoiceActivity");
            startActivity(intent);
        });

        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            passDataToIntent(intent);
            intent.putExtra("previous_activity", "AuthChoiceActivity");
            startActivity(intent);
        });

        btnGuest.setOnClickListener(v -> proceedAsGuest());
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