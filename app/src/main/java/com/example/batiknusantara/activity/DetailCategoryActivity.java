package com.example.batiknusantara.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.batiknusantara.adapter.ProductAdapter;
import com.example.batiknusantara.api.ApiClient;
import com.example.batiknusantara.api.ApiService;
import com.example.batiknusantara.api.response.ProductResponse;
import com.example.batiknusantara.databinding.ActivityDetailCategoryBinding;
import com.example.batiknusantara.model.CartItem;
import com.example.batiknusantara.model.Product;
import com.example.batiknusantara.utils.SessionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailCategoryActivity extends AppCompatActivity {

    private ActivityDetailCategoryBinding binding;

    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDetailCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = ApiClient.getClient().create(ApiService.class);

        String categoryName = getIntent().getStringExtra("category_name");
        binding.tvCategoryDetail.setText(categoryName);

        binding.ivBack.setOnClickListener(v -> finish());

        searchProducts(categoryName);
    }

    private void searchProducts(String keyword) {
        Call<ProductResponse> call = apiService.searchProducts(keyword);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    List<Product> products = response.body().getData();
                    ProductAdapter adapter = new ProductAdapter(DetailCategoryActivity.this, products, new ProductAdapter.OnProductClickListener() {
                        @Override
                        public void onAddToCart(Product product) {
                            if (sessionManager == null) {
                                sessionManager = new SessionManager(DetailCategoryActivity.this);
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
                                    Toast.makeText(DetailCategoryActivity.this, "Gagal: Stok tidak cukup", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(DetailCategoryActivity.this, "Ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onViewDescription(Product product) {
                            Intent intent = new Intent(DetailCategoryActivity.this, DetailProductActivity.class);
                            intent.putExtra("product", product);
                            startActivity(intent);
                        }
                    });
                    binding.recyclerView.setLayoutManager(new GridLayoutManager(DetailCategoryActivity.this, 2));
                    binding.recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(DetailCategoryActivity.this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(DetailCategoryActivity.this, "Gagal memuat data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}