package com.innovacionti.domain;

public class User {
    private String email;
    private String password;
    private String alias;
    private boolean registered;
    private boolean institutional;
    private String role; // STUDENT | RESTAURANT

    public User() {
    }

    public User(String email, String password, String alias, boolean registered, boolean institutional, String role) {
        this.email = email;
        this.password = password;
        this.alias = alias;
        this.registered = registered;
        this.institutional = institutional;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public boolean isInstitutional() {
        return institutional;
    }

    public void setInstitutional(boolean institutional) {
        this.institutional = institutional;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
