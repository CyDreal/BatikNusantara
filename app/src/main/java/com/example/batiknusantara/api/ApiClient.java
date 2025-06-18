package com.example.batiknusantara.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Localhost
    public static final String  BASE_URL = "http://192.168.1.13/api_native_sertifikasi2/api/";
    public static final String  BASE_URL_IMAGE = "http://192.168.1.13/api_native_sertifikasi2/uploads/";

    // Hosting
//    public static final String  BASE_URL = "https://androidfisco.umrmaulana.my.id/api-mobile-2/";
//    public static final String  BASE_URL_IMAGE = "https://androidfisco.umrmaulana.my.id/api-mobile-2/images/";

    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
