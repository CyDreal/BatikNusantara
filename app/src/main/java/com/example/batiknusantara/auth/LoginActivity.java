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
import com.example.batiknusantara.api.request.AuthRequest;
import com.example.batiknusantara.api.response.UserResponse;
import com.example.batiknusantara.model.User;
import com.example.batiknusantara.utils.SessionManager;

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

    private SessionManager sessionManager;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(LoginActivity.this);

        // Check if user is already logged in
        if(sessionManager.isLoggedIn()){
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
        Call<UserResponse> call = apiService.login("login", request);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                showProgress(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();

                    if (userResponse.isStatus() == true && userResponse.getUser() != null) {
                        User user = userResponse.getUser();
                        
                        SessionManager.saveUser(user);
                        navigateToMainActivity();

                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid user data received", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Response error
                    Toast.makeText(LoginActivity.this, "Gagal login. Periksa koneksi internet dan coba lagi.", Toast.LENGTH_LONG).show();
                    Log.e("Login Activity", "Response error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                showProgress(false);
                Toast.makeText(LoginActivity.this, "Gagal login: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("Login Activity", "API call failed", t);
            }
        });
    }


    private boolean isValidEmail(CharSequence target) {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
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