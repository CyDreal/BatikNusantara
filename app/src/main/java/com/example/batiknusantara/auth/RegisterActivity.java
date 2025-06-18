package com.example.batiknusantara.auth;

import android.content.Intent;
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

import com.example.batiknusantara.R;
import com.example.batiknusantara.api.ApiClient;
import com.example.batiknusantara.api.ApiService;
import com.example.batiknusantara.model.RegisterRequest;
import com.example.batiknusantara.model.RegisterResponse;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editTextNama;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;
    private TextInputEditText editTextConfirmPassword;
    private Button buttonRegister;
    private ProgressBar progressBar;
    private TextView textViewLogin;

    private ApiService apiService;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        editTextNama = findViewById(R.id.editTextNama);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        progressBar = findViewById(R.id.progressBar);
        textViewLogin = findViewById(R.id.textViewLogin);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Set up click listeners
        buttonRegister.setOnClickListener(view -> attemptRegister());

        textViewLogin.setOnClickListener(view -> {
            // Navigate back to LoginActivity
            finish();
        });
    }

    private void attemptRegister() {
        // Reset errors
        editTextNama.setError(null);
        editTextEmail.setError(null);
        editTextPassword.setError(null);
        editTextConfirmPassword.setError(null);

        // Get values
        String nama = editTextNama.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // Validate all fields
        boolean cancel = false;
        View focusView = null;

        // Check for valid name
        if (TextUtils.isEmpty(nama)) {
            editTextNama.setError("Nama tidak boleh kosong");
            focusView = editTextNama;
            cancel = true;
        }

        // Check for valid email
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email tidak boleh kosong");
            focusView = editTextEmail;
            cancel = true;
        } else if (!isValidEmail(email)) {
            editTextEmail.setError("Email tidak valid");
            focusView = editTextEmail;
            cancel = true;
        }

        // Check for valid password (minimum 6 characters)
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password tidak boleh kosong");
            focusView = editTextPassword;
            cancel = true;
        } else if (password.length() < 3) {
            editTextPassword.setError("Password minimal 3 karakter");
            focusView = editTextPassword;
            cancel = true;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Password tidak cocok");
            focusView = editTextConfirmPassword;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt registration and focus the first form field with an error
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            // Show progress and attempt registration
            showProgress(true);

            // Set all other fields to null
            RegisterRequest request = new RegisterRequest(
                nama, null, null, null, null, null, email, password
            );

            Call<RegisterResponse> call = apiService.register("register", request);

            call.enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                    showProgress(false);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        RegisterResponse registerResponse = response.body();
                        
                        if (registerResponse.isStatus()) {
                            // Registration successful
                            Toast.makeText(RegisterActivity.this, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            
                            // Navigate to login activity
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Registration failed with server message
                            Toast.makeText(RegisterActivity.this, registerResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Response error
                        Toast.makeText(RegisterActivity.this, "Gagal mendaftar. Periksa koneksi internet dan coba lagi.", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Response error: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    showProgress(false);
                    Toast.makeText(RegisterActivity.this, "Gagal mendaftar: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "API call failed", t);
                }
            });
        }
    }

    private boolean isValidEmail(CharSequence target) {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        buttonRegister.setEnabled(!show);
    }
}
