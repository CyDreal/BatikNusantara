package com.example.batiknusantara.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.batiknusantara.R;
import com.example.batiknusantara.api.ApiClient;
import com.example.batiknusantara.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private static final String SHARED_PREF_NAME = "batik_app_preferences";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize the image slider
        setupImageSlider();
        
        // Display user name
        displayUserName();

        return root;
    }

    // Method to retrieve and display the username
    private void displayUserName() {
        TextView tvUsername = binding.tvUsername;
        
        // Get SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        
        // Get user name from SharedPreferences
        String userName = sharedPreferences.getString("user_name", "Pengguna");
        
        // Set the user name to the TextView
        tvUsername.setText(userName);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Update username in case it was changed
        displayUserName();
    }

    private void setupImageSlider() {
        ImageSlider imageSlider = binding.imageSlider;
        List<SlideModel> slideModels = new ArrayList<>();

        // Add sample images to the slider
        // You can replace these with actual image URLs from your API
        slideModels.add(new SlideModel(R.drawable.banner1,  ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.banner2,  ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.banner3,  ScaleTypes.CENTER_CROP));

//        // Example with URL (replace with actual images from your server)
//        slideModels.add(new SlideModel(ApiClient.BASE_URL_IMAGE + "batik_sample1.jpg", "Batik Tradisional", ScaleTypes.CENTER_CROP));
//        slideModels.add(new SlideModel(ApiClient.BASE_URL_IMAGE + "batik_sample2.jpg", "Batik Modern", ScaleTypes.CENTER_CROP));

        // Set the slider adapter
        imageSlider.setImageList(slideModels);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}