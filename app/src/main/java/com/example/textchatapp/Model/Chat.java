package com.example.textchatapp.Model;

public class Chat {


    private String senderID, receiverID, message;
    private boolean isSeen;


    public Chat() { }

    public Chat(String senderID, String receiverID, String message, boolean isSeen) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.message = message;
        this.isSeen = isSeen;
    }


    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
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
