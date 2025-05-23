package com.enicarthage.coulisses.models;

public class AuthenticationResponse {
    private String token;
    private User user;

    // Constructors
    public AuthenticationResponse() {}

    public AuthenticationResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
