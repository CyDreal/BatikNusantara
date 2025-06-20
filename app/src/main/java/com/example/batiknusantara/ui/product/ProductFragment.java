package com.example.batiknusantara.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.batiknusantara.activity.DetailProductActivity;
import com.example.batiknusantara.adapter.ProductAdapter;
import com.example.batiknusantara.api.ApiClient;
import com.example.batiknusantara.api.ApiService;
import com.example.batiknusantara.api.response.ProductResponse;
import com.example.batiknusantara.databinding.FragmentProductBinding;
import com.example.batiknusantara.model.CartItem;
import com.example.batiknusantara.model.Product;
import com.example.batiknusantara.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFragment extends Fragment {

    private FragmentProductBinding binding;

    private ApiService apiService;
    private SessionManager sessionManager;
    private ProductAdapter productAdapter;

    private List<Product> allProducts = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        apiService = ApiClient.getClient().create(ApiService.class);

        // Load product
        loadProduct();
        
        // Search Listener
        setupSearchListener();

        return root;
    }

    private void setupSearchListener() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProducts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchProducts(newText);
                return true;
            }
        });
    }

    private void searchProducts(String keyword) {
        Call<ProductResponse> call = apiService.searchProducts(keyword);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    List<Product> searchResults = response.body().getData();
                    if(productAdapter != null){
                        productAdapter.updateData(searchResults);
                    }
                } else {
                    Toast.makeText(getContext(), "Tidak ada hasil ditemukan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Gagal terhubung: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProduct() {
        Call<ProductResponse> call = apiService.getAllProducts();
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allProducts = response.body().getData();
                    productAdapter = new ProductAdapter(getContext(), allProducts, new ProductAdapter.OnProductClickListener() {
                        @Override
                        public void onAddToCart(Product product) {
                            if (sessionManager == null) {
                                sessionManager = new SessionManager(requireContext());
                            }
                            // Ambil keranjang saat ini
                            List<CartItem> currentCart = sessionManager.getOrderItems();
                            Map<String, CartItem> cartMap = new HashMap<>();
                            for (CartItem item : currentCart) {
                                cartMap.put(item.getProductKode(), item);
                            }
                            String kodeProduk = product.getKode();
                            CartItem existingItem = cartMap.get(kodeProduk);

                            if (existingItem != null) {
                                int newQuantity = (int) (existingItem.getQuantity() + 1);
                                if (newQuantity > product.getStok()) {
                                    Toast.makeText(getContext(), "Gagal: Stok tidak cukup", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                existingItem.setQuantity(newQuantity);
                            } else {
                                CartItem newItem = new CartItem(
                                        product.getKode(),
                                        product.getMerk(),
                                        product.getHargajual(),
                                        1,
                                        product.getFoto(),
                                        product.getStok()
                                );
                                cartMap.put(kodeProduk, newItem);
                            }

                            // Simpan kembali ke SharedPreferences
                            sessionManager.saveCart(cartMap);
                            Toast.makeText(getContext(), "Ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onViewDescription(Product product) {
                            Intent intent = new Intent(getContext(), DetailProductActivity.class);
                            intent.putExtra("product", product);
                            startActivity(intent);
                        }
                    });
                    binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
                    binding.recyclerView.setHasFixedSize(true);
                    binding.recyclerView.setAdapter(productAdapter);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}