package com.example.batiknusantara.ui.order;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.batiknusantara.adapter.OrderAdapter;
import com.example.batiknusantara.auth.LoginActivity;
import com.example.batiknusantara.databinding.FragmentOrderBinding;
import com.example.batiknusantara.model.CartItem;
import com.example.batiknusantara.utils.SessionManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderFragment extends Fragment {

    private FragmentOrderBinding binding;

    private SessionManager sessionManager;
    private OrderAdapter orderAdapter;
    private List<CartItem> cartItems;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sessionManager = new SessionManager(requireContext());

        if(sessionManager.isLoggedIn()==false){
            binding.btnCheckout.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            });
        }

        setupRecyclerView(cartItems);
        loadOrderItems();

        return root;
    }

    private void loadOrderItems() {
        List<CartItem> cartItems = sessionManager.getOrderItems();
        if (cartItems.isEmpty()) {
            showEmptyOrder();
        } else {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.tvTotalPrice.setVisibility(View.VISIBLE);
            binding.btnCheckout.setVisibility(View.VISIBLE);
            binding.emptyCartText.setVisibility(View.GONE);
            orderAdapter.setItems(cartItems);
            updateTotalPrice();
        }
    }

    private void showEmptyOrder() {
        binding.recyclerView.setVisibility(View.GONE);
        binding.tvTotalPrice.setVisibility(View.GONE);
        binding.btnCheckout.setVisibility(View.GONE);
        binding.emptyCartText.setVisibility(View.VISIBLE);
    }

    private void updateTotalPrice() {
        int total = 0;
        for (CartItem item : sessionManager.getOrderItems()) {
            total += item.getSubtotal();
        }
        binding.tvTotalPrice.setText("Total: " + NumberFormat.getCurrencyInstance(new Locale("in", "ID")).format(total));
    }


    private void setupRecyclerView(List<CartItem> cartItems) {
        orderAdapter = new OrderAdapter(getContext(), cartItems, item -> {
            sessionManager.removeFromCart(item.getProductKode());
            loadOrderItems();
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(orderAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}