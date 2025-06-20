package com.example.batiknusantara.api;

import com.example.batiknusantara.api.request.AuthRequest;
import com.example.batiknusantara.api.request.RegisterRequest;
import com.example.batiknusantara.api.response.CategoryResponse;
import com.example.batiknusantara.api.response.ProductResponse;
import com.example.batiknusantara.api.response.RegisterResponse;
import com.example.batiknusantara.api.response.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("auth.php")
    Call<UserResponse> login(@Query("action") String action, @Body AuthRequest request);
    
    @POST("auth.php")
    Call<RegisterResponse> register(@Query("action") String action, @Body RegisterRequest request);
    
    @GET("user/{id}")
    Call<UserResponse> getUserProfile(@Path("id") int userId);

    @GET("products.php")
    Call<ProductResponse> getAllProducts();

    @GET("products.php")
    Call<ProductResponse> searchProducts(@Query("search") String keyword);

    @GET("products.php?action=categories")
    Call<CategoryResponse> getCategories();
}
