package com.example.batiknusantara.activity;

import static com.example.batiknusantara.api.ApiClient.BASE_URL_IMAGE;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.batiknusantara.R;
import com.example.batiknusantara.databinding.ActivityDetailProductBinding;
import com.example.batiknusantara.model.CartItem;
import com.example.batiknusantara.model.Product;
import com.example.batiknusantara.utils.SessionManager;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetailProductActivity extends AppCompatActivity {

    private ActivityDetailProductBinding binding;

    private SessionManager sessionManager;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDetailProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        product = (Product) getIntent().getSerializableExtra("product");

        if (product != null) {
            loadProductDetail();
        } else {
            finish();
        }

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.imageButtonCart.setOnClickListener(v -> {
            addToCart(product);
        });
    }

    private void addToCart(Product product) {
        if (sessionManager == null) {
            sessionManager = new SessionManager(DetailProductActivity.this);
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
                Toast.makeText(DetailProductActivity.this, "Gagal: Stok tidak cukup", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(DetailProductActivity.this, "Ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();
    }

    private void loadProductDetail() {
        binding.textViewMerkDetail.setText(product.getMerk());
        binding.textViewDeskripsiDetail.setText(product.getDeskripsi());

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        binding.textViewHargaDetail.setText(format.format(product.getHargajual()));
        binding.textViewStokDetail.setText("Stok: "+String.valueOf(product.getStok()));
        binding.textViewKategori.setText(product.getKategori());
        Glide.with(DetailProductActivity.this)
                .load(BASE_URL_IMAGE+"products/"+product.getFoto())
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.imageViewDetail);
    }
}