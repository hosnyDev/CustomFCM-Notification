package com.hosnydev.customfcm.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface API {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAtV-zEmU:APA91bFOmmg28XdPmMAfze0rjanaFqxyKQhw80DXd_4v-ygCfPhhaBBoYrRKuAgCBYJ21CPZTvDhCV8ofhI6LgGQFd5U9UYkUHctscMSycqAMq692RdO_kGxSK13DqOQtV2lDxzzkFw_"
    })
    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
