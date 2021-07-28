package com.example.textchatapp.Model;

public class User {


    private String id, username, imageURL, activityStatus;


    public User() { }

    public User(String id, String username, String imageURL, String activityStatus) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.activityStatus = activityStatus;
    }

    public String getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(String activityStatus) {
        this.activityStatus = activityStatus;
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
