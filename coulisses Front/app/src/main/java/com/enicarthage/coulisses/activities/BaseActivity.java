package com.enicarthage.coulisses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.enicarthage.coulisses.R;
import com.enicarthage.coulisses.util.SessionManager;

public abstract class BaseActivity extends AppCompatActivity {

    protected SessionManager sessionManager;
    private TextView userText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupUserMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);

        // Cache les options si non connecté
        MenuItem profileItem = menu.findItem(R.id.menu_profile);
        MenuItem ticketsItem = menu.findItem(R.id.menu_tickets);
        MenuItem logoutItem = menu.findItem(R.id.menu_logout);

        if (profileItem != null && ticketsItem != null && logoutItem != null) {
            boolean isLoggedIn = sessionManager.isLoggedIn();
            profileItem.setVisible(isLoggedIn);
            ticketsItem.setVisible(isLoggedIn);
            logoutItem.setVisible(isLoggedIn);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_profile) {
            navigateToProfile();
            return true;
        } else if (id == R.id.menu_tickets) {
            navigateToTickets();
            return true;
        } else if (id == R.id.menu_logout) {
            sessionManager.logoutUser();
            updateUserMenu();
            Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void setupUserMenu() {
        userText = findViewById(R.id.userText);
        View userLayout = findViewById(R.id.userLayout);

        if (userText != null && userLayout != null) {
            updateUserMenu();

            userLayout.setOnClickListener(v -> {
                if (sessionManager.isLoggedIn()) {
                    showUserMenu(v);
                } else {
                    navigateToLogin();
                }
            });
        }
    }
    private void showUserMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        MenuInflater inflater = popupMenu.getMenuInflater();

        if (sessionManager.isLoggedIn()) {
            inflater.inflate(R.menu.menu_user_logged_in, popupMenu.getMenu());
        } else {
            inflater.inflate(R.menu.menu_user_guest, popupMenu.getMenu());
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_login) {
                navigateToLogin();
                return true;
            } else if (id == R.id.menu_profile) {
                navigateToProfile();
                return true;
            } else if (id == R.id.menu_tickets) {
                navigateToTickets();
                return true;
            } else if (id == R.id.menu_logout) {
                sessionManager.logoutUser();
                updateUserMenu();
                Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    protected void updateUserMenu() {
        if (userText != null) {
            if (sessionManager.isLoggedIn()) {
                String fullName = sessionManager.getUserFullName();
                userText.setText(fullName.isEmpty() ? sessionManager.getUserEmail() : fullName);
                findViewById(R.id.userIcon).setBackgroundResource(R.drawable.ic_login);
            } else {
                userText.setText("Login");
                findViewById(R.id.userIcon).setBackgroundResource(R.drawable.ic_login);
            }
        }
    }

    protected void navigateToProfile() {
        // Implémentez la navigation vers le profil
        // Exemple : startActivity(new Intent(this, ProfileActivity.class));
    }

    protected void navigateToTickets() {
        // Implémentez la navigation vers les billets
        // Exemple : startActivity(new Intent(this, MyTicketsActivity.class));
    }

    protected void navigateToLogin() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }
}