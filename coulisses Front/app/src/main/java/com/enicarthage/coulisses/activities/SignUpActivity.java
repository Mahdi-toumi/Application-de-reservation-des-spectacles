package com.enicarthage.coulisses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.enicarthage.coulisses.R;
import com.enicarthage.coulisses.databinding.ActivitySignUpBinding;
import com.enicarthage.coulisses.models.AuthenticationResponse;
import com.enicarthage.coulisses.models.RegisterRequest;
import com.enicarthage.coulisses.models.Spectacle;
import com.enicarthage.coulisses.network.ApiClient;
import com.enicarthage.coulisses.network.AuthApi;
import com.enicarthage.coulisses.util.BilletSelection;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private Spectacle spectacle;
    private List<BilletSelection> selectedBillets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Récupérer les données
        spectacle = getIntent().getParcelableExtra("spectacle");
        selectedBillets = getIntent().getParcelableArrayListExtra("selected_billets");

        // Initialiser les listeners
        binding.btnSignUp.setOnClickListener(v -> registerUser());
        binding.btnSignIn.setOnClickListener(v -> navigateToSignIn());
    }

    private void registerUser() {
        String firstName = binding.etNom.getText().toString().trim();
        String lastName = binding.etPrenom.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String tel = binding.etTelephone.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || tel.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            binding.tilPassword.setError("Les mots de passe ne correspondent pas");
            binding.tilConfirmPassword.setError("Les mots de passe ne correspondent pas");
            return;
        }

        // Créer la requête d'inscription
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setNom(firstName);
        registerRequest.setPrenom(lastName);
        registerRequest.setEmail(email);
        registerRequest.setTel(tel);
        registerRequest.setMotDePasse(password);

        // Appeler l'API
        AuthApi authApi = ApiClient.getClient().create(AuthApi.class);
        Call<AuthenticationResponse> call = authApi.register(registerRequest);
        call.enqueue(new retrofit2.Callback<AuthenticationResponse>() {
            @Override
            public void onResponse(Call<AuthenticationResponse> call, retrofit2.Response<AuthenticationResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Inscription réussie !", Toast.LENGTH_SHORT).show();
                    navigateToSignIn();
                } else {
                    Toast.makeText(SignUpActivity.this, "Erreur: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean validateForm() {
        // Implémenter la validation des champs
        return true;
    }

    private void navigateToSignIn() {
        Intent intent = new Intent(this, SignInActivity.class);
        passDataToIntent(intent);
        startActivity(intent);
    }
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