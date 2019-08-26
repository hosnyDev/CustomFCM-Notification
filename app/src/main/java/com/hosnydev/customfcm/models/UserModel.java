package com.hosnydev.customfcm.models;

public class UserModel {

    private String id,email,name,tokin;

    public UserModel() {
    }

    public UserModel(String id, String email, String name, String tokin) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.tokin = tokin;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getTokin() {
        return tokin;
    }
}
