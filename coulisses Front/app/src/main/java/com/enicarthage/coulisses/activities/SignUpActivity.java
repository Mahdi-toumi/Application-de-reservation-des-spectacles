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
import com.enicarthage.coulisses.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private Spectacle spectacle;
    private List<BilletSelection> selectedBillets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        spectacle = getIntent().getParcelableExtra("spectacle");
        selectedBillets = getIntent().getParcelableArrayListExtra("selected_billets");

        binding.btnSignUp.setOnClickListener(v -> registerUser());
        binding.btnSignIn.setOnClickListener(v -> navigateToSignIn());
    }

    private void registerUser() {
        if (!validateForm()) {
            return;
        }

        // Récupérer les champs après validation
        String firstName = binding.etNom.getText().toString().trim();
        String lastName = binding.etPrenom.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String tel = binding.etTelephone.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setNom(firstName);
        registerRequest.setPrenom(lastName);
        registerRequest.setEmail(email);
        registerRequest.setTel(tel);
        registerRequest.setMotDePasse(password);

        AuthApi authApi = ApiClient.getClient().create(AuthApi.class);
        Call<AuthenticationResponse> call = authApi.register(registerRequest);
        call.enqueue(new Callback<AuthenticationResponse>() {
            @Override
            public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthenticationResponse authResponse = response.body();
                    SessionManager sessionManager = new SessionManager(SignUpActivity.this);
                    sessionManager.saveUserSession(authResponse);

                    Toast.makeText(SignUpActivity.this, "Inscription réussie !", Toast.LENGTH_SHORT).show();

                    if (spectacle != null && selectedBillets != null) {
                        proceedToPayment();
                    } else {
                        finish();
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean validateForm() {
        boolean isValid = true;

        // Réinitialiser toutes les erreurs
        binding.tilNom.setError(null);
        binding.tilPrenom.setError(null);
        binding.tilEmail.setError(null);
        binding.tilTelephone.setError(null);
        binding.tilPassword.setError(null);
        binding.tilConfirmPassword.setError(null);

        String firstName = binding.etNom.getText().toString().trim();
        String lastName = binding.etPrenom.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String tel = binding.etTelephone.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        if (firstName.isEmpty()) {
            binding.tilNom.setError("Le nom est obligatoire");
            isValid = false;
        }

        if (lastName.isEmpty()) {
            binding.tilPrenom.setError("Le prénom est obligatoire");
            isValid = false;
        }

        if (email.isEmpty()) {
            binding.tilEmail.setError("L'email est obligatoire");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError("Format d'email invalide");
            isValid = false;
        }

        if (tel.isEmpty()) {
            binding.tilTelephone.setError("Le téléphone est obligatoire");
            isValid = false;
        } else if (!tel.matches("\\d{8}")) { // Numéro tunisien 8 chiffres
            binding.tilTelephone.setError("Téléphone invalide (8 chiffres)");
            isValid = false;
        }

        if (password.isEmpty()) {
            binding.tilPassword.setError("Le mot de passe est obligatoire");
            isValid = false;
        } else if (password.length() < 6) {
            binding.tilPassword.setError("Le mot de passe doit contenir au moins 6 caractères");
            isValid = false;
        }

        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.setError("Veuillez confirmer votre mot de passe");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            binding.tilPassword.setError("Les mots de passe ne correspondent pas");
            binding.tilConfirmPassword.setError("Les mots de passe ne correspondent pas");
            isValid = false;
        }

        return isValid;
    }


    private void navigateToSignIn() {
        Intent intent = new Intent(this, SignInActivity.class);
        passDataToIntent(intent);

        String previousActivity = getIntent().getStringExtra("previous_activity");
        if (previousActivity != null) {
            intent.putExtra("previous_activity", previousActivity);
        }

        startActivity(intent);
    }

    private void proceedToPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        passDataToIntent(intent);
        startActivity(intent);
        finish();
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