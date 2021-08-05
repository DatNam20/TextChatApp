package com.example.textchatapp.Notifications;

public class Data {

    private String notificationReceiver;
    private String notificationBody;
    private String notificationTitle;
    private String messageSender;
    private int notificationIcon;


    public Data() {
    }

    public Data(String notificationReceiver, String notificationBody, String notificationTitle, String messageSender, int notificationIcon)
    {
        this.notificationReceiver = notificationReceiver;
        this.notificationBody = notificationBody;
        this.notificationTitle = notificationTitle;
        this.messageSender = messageSender;
        this.notificationIcon = notificationIcon;
    }

    public String getNotificationReceiver() {
        return notificationReceiver;
    }

    public void setNotificationReceiver(String notificationReceiver) {
        this.notificationReceiver = notificationReceiver;
    }

    public String getNotificationBody() {
        return notificationBody;
    }

    public void setNotificationBody(String notificationBody) {
        this.notificationBody = notificationBody;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(String messageSender) {
        this.messageSender = messageSender;
    }

    public int getNotificationIcon() {
        return notificationIcon;
    }

    public void setNotificationIcon(int notificationIcon) {
        this.notificationIcon = notificationIcon;
    }

}
