package com.example.textchatapp.Fragments;

import com.example.textchatapp.Notifications.Response;
import com.example.textchatapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface APIService {

    @Headers(
        {
            "Content-Type:application/json",
            "Authorization:key = AAAAwclGpQY:APA91bECjUZzR4edWlgWJwMvsWEyk_Yd08Jzyyy-iHcwmtRynsc5lnbvauPvWIdu6mkAV7Um0HBOfWxbFti9p1Un2uBbTif2Nanjkql17uc8dEscLAhb0jZ4j4397FEgfAhhk91Rs_h9"
        }
    )

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);

}