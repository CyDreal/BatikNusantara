package com.example.batiknusantara.api;

import com.example.batiknusantara.model.AuthRequest;
import com.example.batiknusantara.model.AuthResponse;
import com.example.batiknusantara.model.RegisterRequest;
import com.example.batiknusantara.model.RegisterResponse;
import com.example.batiknusantara.model.UserProfileResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("auth.php")
    Call<AuthResponse> login(@Query("action") String action, @Body AuthRequest request);
    
    @POST("auth.php")
    Call<RegisterResponse> register(@Query("action") String action, @Body RegisterRequest request);
    
    @GET("user/{id}")
    Call<UserProfileResponse> getUserProfile(@Path("id") int userId);
}
