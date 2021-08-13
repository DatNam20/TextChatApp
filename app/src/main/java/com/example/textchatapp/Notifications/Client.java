package com.example.textchatapp.Notifications;

import com.example.textchatapp.R;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {

    private static Retrofit retrofit = null;
    public static final String CLIENT_URL = "https://fcm.googleapis.com/";


    public static Retrofit getClient(){

        if (retrofit == null)
            retrofit = new Retrofit.Builder().baseUrl(CLIENT_URL).addConverterFactory(GsonConverterFactory.create()).build();

        return retrofit;
    }

}
