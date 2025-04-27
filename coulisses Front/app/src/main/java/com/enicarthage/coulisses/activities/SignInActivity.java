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
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private Spectacle spectacle;
    private List<BilletSelection> selectedBillets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Récupérer les données
        spectacle = getIntent().getParcelableExtra("spectacle");
        selectedBillets = getIntent().getParcelableArrayListExtra("selected_billets");

        // Initialiser les listeners
        binding.btnSignIn.setOnClickListener(v -> authenticateUser());
        binding.btnSignUp.setOnClickListener(v -> navigateToSignUp());
      //  binding.tvForgotPassword.setOnClickListener(v -> navigateToForgotPassword());
    }

    private void authenticateUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthApi authApi = ApiClient.getClient().create(AuthApi.class);
        AuthenticationRequest request = new AuthenticationRequest(email, password);

        authApi.login(request).enqueue(new retrofit2.Callback<AuthenticationResponse>() {
            @Override
            public void onResponse(Call<AuthenticationResponse> call, retrofit2.Response<AuthenticationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Succès de l'authentification
                    AuthenticationResponse authResponse = response.body();

                    // TODO: enregistrer l'utilisateur et le token si besoin
                    Toast.makeText(SignInActivity.this, "Connexion réussie", Toast.LENGTH_SHORT).show();

                   // proceedToPayment();
                } else {
                    // Erreur d'authentification
                    Toast.makeText(SignInActivity.this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                Toast.makeText(SignInActivity.this, "Erreur de connexion: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Pour l'instant, on simule une authentification réussie
      //  proceedToPayment();
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        passDataToIntent(intent);
        startActivity(intent);
    }

    /*
    private void navigateToForgotPassword() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }*/

    /*

    private void proceedToPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        passDataToIntent(intent);
        startActivity(intent);
    }*/

    private void passDataToIntent(Intent intent) {
        intent.putExtra("spectacle", spectacle);
        intent.putParcelableArrayListExtra("selected_billets", new ArrayList<>(selectedBillets));
    }
}