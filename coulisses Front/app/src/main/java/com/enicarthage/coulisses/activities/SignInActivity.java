package com.enicarthage.coulisses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.enicarthage.coulisses.R;
import com.enicarthage.coulisses.databinding.ActivitySignInBinding;
import com.enicarthage.coulisses.models.AuthenticationRequest;
import com.enicarthage.coulisses.models.AuthenticationResponse;
import com.enicarthage.coulisses.models.Spectacle;
import com.enicarthage.coulisses.network.ApiClient;
import com.enicarthage.coulisses.network.AuthApi;
import com.enicarthage.coulisses.util.BilletSelection;
import com.enicarthage.coulisses.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private Spectacle spectacle;
    private List<BilletSelection> selectedBillets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        spectacle = getIntent().getParcelableExtra("spectacle");
        selectedBillets = getIntent().getParcelableArrayListExtra("selected_billets");

        binding.btnSignIn.setOnClickListener(v -> authenticateUser());
        binding.btnSignUp.setOnClickListener(v -> navigateToSignUp());
    }

    private void authenticateUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Adresse email invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthApi authApi = ApiClient.getClient().create(AuthApi.class);
        AuthenticationRequest request = new AuthenticationRequest(email, password);

        authApi.login(request).enqueue(new Callback<AuthenticationResponse>() {
            @Override
            public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthenticationResponse authResponse = response.body();
                    SessionManager sessionManager = new SessionManager(SignInActivity.this);
                    sessionManager.saveUserSession(authResponse);

                    Toast.makeText(SignInActivity.this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                    proceedAfterLogin();
                } else {
                    Toast.makeText(SignInActivity.this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                Toast.makeText(SignInActivity.this, "Erreur de connexion: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        passDataToIntent(intent);

        String previousActivity = getIntent().getStringExtra("previous_activity");
        if (previousActivity != null) {
            intent.putExtra("previous_activity", previousActivity);
        }

        startActivity(intent);
    }

    private void proceedAfterLogin() {
        if (spectacle != null && selectedBillets != null) {
            proceedToPayment();
        } else {
            finish();
        }
    }

    private void proceedToPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        passDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private boolean isValidEmail(String email) {
        // Regex for validating email format
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void passDataToIntent(Intent intent) {
        if (spectacle != null) {
            intent.putExtra("spectacle", spectacle);
        }
        if (selectedBillets != null) {
            intent.putParcelableArrayListExtra("selected_billets", new ArrayList<>(selectedBillets));
        }
    }
}