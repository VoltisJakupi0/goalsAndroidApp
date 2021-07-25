package com.riinvest.goals.Model;

public class UserModel {

    private int id;
    private String user_name;
    private String user_surname;
    private String email;
    private String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_surname() {
        return user_surname;
    }

    public void setUser_surname(String user_surname) {
        this.user_surname = user_surname;
    }


    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email;}


    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password;}
}
