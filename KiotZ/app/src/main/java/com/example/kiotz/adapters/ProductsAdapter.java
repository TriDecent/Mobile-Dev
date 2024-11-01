package com.example.kiotz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiotz.R;
import com.example.kiotz.models.Product;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.MyViewHolder> {
    private final Context context;
    private final List<Product> tempProductList; // Must change after implementing the database

    public ProductsAdapter(Context context) {
        this.context = context;

        tempProductList = Arrays.asList(
                new Product(1211, "RITZ: Original Cracker", "Electronics", 10, "Piece", "/path/to/qr1"),
                new Product(1211, "RITZ: Original Cracker", "Electronics", 10, "Piece", "/path/to/qr1"),
                new Product(1211, "RITZ: Original Cracker", "Electronics", 10, "Piece", "/path/to/qr1"),
                new Product(1211, "RITZ: Original Cracker", "Electronics", 10, "Piece", "/path/to/qr1"),
                new Product(1211, "RITZ: Original Cracker", "Electronics", 10, "Piece", "/path/to/qr1"),
                new Product(1211, "RITZ: Original Cracker", "Electronics", 10, "Piece", "/path/to/qr1"),
                new Product(1211, "RITZ: Original Cracker", "Electronics", 10, "Piece", "/path/to/qr1"),
                new Product(1211, "RITZ: Original Cracker", "Electronics", 10, "Piece", "/path/to/qr1"),
                new Product(1211, "RITZ: Original Cracker", "Electronics", 10, "Piece", "/path/to/qr1"),
                new Product(1211, "RITZ: Original Cracker", "Electronics", 10, "Piece", "/path/to/qr1")
        );
    }

    @NonNull
    @Override
    public ProductsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var inflater = LayoutInflater.from(context);
        var view = inflater.inflate(R.layout.item_form7, parent, false);
        return new ProductsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapter.MyViewHolder holder, int position) {
        if (tempProductList == null) return;

        var currentStudent = tempProductList.get(position);
        holder.bindData(currentStudent);
    }

    @Override
    public int getItemCount() {
        return tempProductList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvId, tvName, tvPrice;
        private final CardView cvStudent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_product_id);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            cvStudent = itemView.findViewById(R.id.cv_contact);
        }

        public void bindData(Product product) {
            tvId.setText(String.format(Locale.getDefault(), "SP%d", product.ID()));
            tvName.setText(String.format(Locale.getDefault(), "%s", product.Name()));
            tvPrice.setText(String.format(Locale.getDefault(), "$%s", product.Price()));
        }
    }
}
