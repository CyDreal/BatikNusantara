package com.example.batiknusantara.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.batiknusantara.R;
import com.example.batiknusantara.activity.DetailCategoryActivity;
import com.example.batiknusantara.adapter.CategoryAdapter;
import com.example.batiknusantara.api.ApiClient;
import com.example.batiknusantara.api.ApiService;
import com.example.batiknusantara.api.response.CategoryResponse;
import com.example.batiknusantara.databinding.FragmentHomeBinding;
import com.example.batiknusantara.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private SessionManager sessionManager;
    private ApiService apiService;
    private CategoryAdapter categoryAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        apiService = ApiClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(requireContext());

        // Initialize the image slider
        setupImageSlider();
        
        // Display name
        displayName();

        // Load Category
        loadCategories();

        return root;
    }

    private void loadCategories() {
        Call<CategoryResponse> call = apiService.getCategories();
        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    List<String> categories = response.body().getData();

                    categoryAdapter = new CategoryAdapter(categories, category -> {
                        Intent intent = new Intent(getContext(), DetailCategoryActivity.class);
                        intent.putExtra("category_name", category);
                        startActivity(intent);
                    });
                    binding.rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    binding.rvCategories.setAdapter(categoryAdapter);
                } else {
                    Toast.makeText(getContext(), "Gagal memuat kategori", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to retrieve and display the username
    private void displayName() {
        if(sessionManager.isLoggedIn()){
            String name = sessionManager.getNama();
            binding.tvName.setText(name);
        } else {
            binding.tvName.setText("Guest");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        displayName();
    }

    private void setupImageSlider() {
        ImageSlider imageSlider = binding.imageSlider;
        List<SlideModel> slideModels = new ArrayList<>();

        // Add sample images to the slider
        // You can replace these with actual image URLs from your API
        slideModels.add(new SlideModel(R.drawable.imageslider1,  ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.imageslider2,  ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.imageslider3,  ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.imageslider4,  ScaleTypes.CENTER_CROP));

        // Set the slider adapter
        imageSlider.setImageList(slideModels);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}