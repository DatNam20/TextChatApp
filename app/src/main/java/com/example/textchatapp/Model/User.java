package com.example.textchatapp.Model;

public class User {


    private String id, username, imageURL, activity;


    public User() { }

    public User(String id, String username, String imageURL, String activity) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.activity = activity;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
