package com.example.batiknusantara.adapter;

import static com.example.batiknusantara.api.ApiClient.BASE_URL;
import static com.example.batiknusantara.api.ApiClient.BASE_URL_IMAGE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.batiknusantara.R;
import com.example.batiknusantara.model.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onAddToCart(Product product);
        void onViewDescription(Product product);
    }

    private Context context;
    private List<Product> productList;
    private OnProductClickListener listener;

    public ProductAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, imgBtnAddToCart, imgBtnDescription;
        TextView tvProductName, tvPrice, tvCategory, tvStock;

        public ProductViewHolder(View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            imgBtnAddToCart = itemView.findViewById(R.id.imgBtnAddToCart);
            imgBtnDescription = itemView.findViewById(R.id.imgBtnDescription);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvStock = itemView.findViewById(R.id.tv_stock);
        }

        public void bind(final Product product) {
            tvProductName.setText(product.getMerk());
            tvCategory.setText("Kategori: " + product.getKategori());
            tvStock.setText("Stok: " + product.getStok());

            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
            tvPrice.setText(format.format(product.getHargajual()));

            Glide.with(context)
                    .load(BASE_URL_IMAGE+"products/"+product.getFoto())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imgProduct);

            imgBtnAddToCart.setOnClickListener(v -> listener.onAddToCart(product));
            imgBtnDescription.setOnClickListener(v -> listener.onViewDescription(product));
        }
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        holder.bind(productList.get(position));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateData(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }
}

