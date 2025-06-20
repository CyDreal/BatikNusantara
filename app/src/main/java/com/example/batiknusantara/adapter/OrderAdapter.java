package com.example.batiknusantara.adapter;

import static com.example.batiknusantara.api.ApiClient.BASE_URL;
import static com.example.batiknusantara.api.ApiClient.BASE_URL_IMAGE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.batiknusantara.R;
import com.example.batiknusantara.model.CartItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    public interface OnCartItemDeleteListener {
        void onItemDeleted(CartItem item);
    }

    private Context context;
    private List<CartItem> cartItems;
    private OnCartItemDeleteListener deleteListener;

    public OrderAdapter(Context context, List<CartItem> cartItems, OnCartItemDeleteListener deleteListener) {
        this.context = context;
        this.cartItems = cartItems;
        this.deleteListener = deleteListener;
    }

    public void setItems(List<CartItem> newList) {
        this.cartItems = newList;
        notifyDataSetChanged();
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView imgOrder;
        TextView tvOrderNama, tvOrderHarga, tvQty, tvOrderSubtotal;
        ImageButton btnHapus;

        public OrderViewHolder(View itemView) {
            super(itemView);
            imgOrder = itemView.findViewById(R.id.imgOrder);
            tvOrderNama = itemView.findViewById(R.id.tvOrderNama);
            tvOrderHarga = itemView.findViewById(R.id.tvOrderHarga);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvOrderSubtotal = itemView.findViewById(R.id.tvOrderSubtotal);
            btnHapus = itemView.findViewById(R.id.btnHapus);
        }

        public void bind(CartItem item) {
            tvOrderNama.setText(item.getProductName());
            tvOrderHarga.setText(formatCurrency(item.getPrice()));
            tvQty.setText("Qty: " + item.getQuantity());
            tvOrderSubtotal.setText(formatCurrency(item.getSubtotal()));

            Glide.with(context)
                    .load(BASE_URL_IMAGE+"products/"+item.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imgOrder);

            btnHapus.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onItemDeleted(item);
                }
            });
        }

        private String formatCurrency(Double number) {
            return NumberFormat.getCurrencyInstance(new Locale("in", "ID")).format(number);
        }
    }
}

