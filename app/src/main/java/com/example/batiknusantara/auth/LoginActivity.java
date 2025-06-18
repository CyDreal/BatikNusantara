package com.example.batiknusantara.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.batiknusantara.MainActivity;
import com.example.batiknusantara.R;
import com.example.batiknusantara.api.ApiClient;
import com.example.batiknusantara.api.ApiService;
import com.example.batiknusantara.model.AuthRequest;
import com.example.batiknusantara.model.AuthResponse;
import com.example.batiknusantara.model.UserProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private ProgressBar progressBar;
    private TextView textViewRegister;
    private TextView textViewForgotPassword;

    private ApiService apiService;
    private static final String TAG = "LoginActivity";
    private static final String SHARED_PREF_NAME = "batik_app_preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if user is already logged in
        if (isLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        // Initialize views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        progressBar = findViewById(R.id.progressBar);
        textViewRegister = findViewById(R.id.textViewRegister);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Set up click listeners
        buttonLogin.setOnClickListener(view -> attemptLogin());

        textViewRegister.setOnClickListener(view -> {
            // Navigate to RegisterActivity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        textViewForgotPassword.setOnClickListener(view -> {
            // Handle forgot password functionality
            Toast.makeText(LoginActivity.this, "Fitur reset password akan segera tersedia", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean isLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getBoolean("is_logged_in", false);
    }

    private void attemptLogin() {
        // Reset errors
        editTextEmail.setError(null);
        editTextPassword.setError(null);

        // Get values
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Check for valid email
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email tidak boleh kosong");
            editTextEmail.requestFocus();
            return;
        } else if (!isValidEmail(email)) {
            editTextEmail.setError("Email tidak valid");
            editTextEmail.requestFocus();
            return;
        }

        // Check for valid password
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password tidak boleh kosong");
            editTextPassword.requestFocus();
            return;
        }

        // Show progress
        showProgress(true);

        // Create login request and call API
        AuthRequest request = new AuthRequest(email, password);
        Call<AuthResponse> call = apiService.login("login", request);

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                showProgress(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    
                    if (authResponse.isStatus()) {
                        // Login successful
                        Toast.makeText(LoginActivity.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        
                        // Save user data to SharedPreferences - using the AuthResponse directly
                        saveUserData(authResponse);
                        
                        // Navigate to main activity
                        navigateToMainActivity();
                    } else {
                        // Login failed with server message
                        Toast.makeText(LoginActivity.this, authResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Response error
                    Toast.makeText(LoginActivity.this, "Gagal login. Periksa koneksi internet dan coba lagi.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Response error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                showProgress(false);
                Toast.makeText(LoginActivity.this, "Gagal login: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "API call failed", t);
            }
        });
    }

    private boolean isValidEmail(CharSequence target) {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    // Updated to use AuthResponse instead of UserProfileResponse
    private void saveUserData(AuthResponse authResponse) {
        UserProfileResponse.UserData userData = authResponse.getData();
        if (userData != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            
            editor.putBoolean("is_logged_in", true);
            editor.putInt("user_id", userData.getId());
            editor.putString("user_name", userData.getNama());
            editor.putString("user_email", userData.getEmail());
            
            // Save photo filename if available
            if (userData.getFoto() != null && !userData.getFoto().isEmpty()) {
                editor.putString("user_foto", userData.getFoto());
                Log.d(TAG, "Saving user photo: " + userData.getFoto());
            }
            
            // Save additional user data if needed
            if (userData.getAlamat() != null) editor.putString("user_alamat", userData.getAlamat());
            if (userData.getKota() != null) editor.putString("user_kota", userData.getKota());
            if (userData.getProvinsi() != null) editor.putString("user_provinsi", userData.getProvinsi());
            if (userData.getKodepos() != null) editor.putString("user_kodepos", userData.getKodepos());
            if (userData.getTelp() != null) editor.putString("user_telp", userData.getTelp());
            
            editor.apply();
        } else {
            Log.e(TAG, "User data is null in auth response");
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        buttonLogin.setEnabled(!show);
    }
}