package com.hosnydev.customfcm.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface API {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=YOUR_KEY_HERE"
    })
    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
