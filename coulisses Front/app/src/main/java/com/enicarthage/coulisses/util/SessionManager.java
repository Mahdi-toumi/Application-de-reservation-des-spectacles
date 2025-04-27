package com.enicarthage.coulisses.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;

import com.enicarthage.coulisses.models.AuthenticationResponse;
import com.enicarthage.coulisses.models.User;
import com.enicarthage.coulisses.activities.SignInActivity;

public class SessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_FIRSTNAME = "user_firstname";
    private static final String KEY_USER_LASTNAME = "user_lastname";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public void saveUserSession(AuthenticationResponse authResponse) {
        User user = authResponse.getUser();

        editor.putString(KEY_TOKEN, authResponse.getToken());
        editor.putLong(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_FIRSTNAME, user.getNom());
        editor.putString(KEY_USER_LASTNAME, user.getPrenom());
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    public Long getUserId() {
        return sharedPreferences.getLong(KEY_USER_ID, -1);
    }

    public String getUserFullName() {
        String firstName = sharedPreferences.getString(KEY_USER_FIRSTNAME, "");
        String lastName = sharedPreferences.getString(KEY_USER_LASTNAME, "");
        return firstName + " " + lastName;
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();

        // Rediriger vers l'écran de login
        Intent intent = new Intent(context, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // Pour mettre à jour les infos utilisateur si besoin
    public void updateUserInfo(String firstName, String lastName, String email) {
        editor.putString(KEY_USER_FIRSTNAME, firstName);
        editor.putString(KEY_USER_LASTNAME, lastName);
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }
}