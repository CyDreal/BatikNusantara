package com.example.batiknusantara.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Emulator
//    public static final String  BASE_URL = "http://10.0.2.2:8080/api_native_sertifikasi2/api/";
//    public static final String  BASE_URL_IMAGE = "http://10.0.2.2:8080/api_native_sertifikasi2/uploads/";

    // Localhost
    public static final String  BASE_URL = "http://192.168.1.14:8080/api_native_sertifikasi2/api/";
    public static final String  BASE_URL_IMAGE = "http://192.168.1.14:8080/api_native_sertifikasi2/uploads/";

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
