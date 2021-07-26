package com.example.textchatapp.Model;

public class Chat {


    private String senderID, receiverID, message;


    public Chat() { }

    public Chat(String senderID, String receiverID, String message) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.message = message;
    }


    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
