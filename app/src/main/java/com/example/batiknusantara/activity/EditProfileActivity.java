package com.example.batiknusantara.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.batiknusantara.R;
import com.example.batiknusantara.databinding.ActivityEditProfileBinding;
import com.example.batiknusantara.utils.SessionManager;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        binding.ivBack.setOnClickListener(v -> finish());
    }
}