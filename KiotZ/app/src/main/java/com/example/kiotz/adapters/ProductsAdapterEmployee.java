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

import java.util.List;
import java.util.Locale;

public class ProductsAdapterEmployee extends RecyclerView.Adapter<ProductsAdapterEmployee.MyViewHolder> {
    private final Context context;
    private final List<Product> products;

    public ProductsAdapterEmployee(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductsAdapterEmployee.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var inflater = LayoutInflater.from(context);
        var view = inflater.inflate(R.layout.item_form7, parent, false);
        return new ProductsAdapterEmployee.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapterEmployee.MyViewHolder holder, int position) {
        if (products == null) return;

        var currentStudent = products.get(position);
        holder.bindData(currentStudent);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvId, tvName, tvPrice, tvQuantity;
        private final CardView cvStudent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_product_id);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            tvQuantity = itemView.findViewById(R.id.tv_product_quantity);
            cvStudent = itemView.findViewById(R.id.cv_contact);
        }

        public void bindData(Product product) {
            tvId.setText(String.format(Locale.getDefault(), "SP%s", product.ID()));
            tvName.setText(String.format(Locale.getDefault(), "%s", product.Name()));
            tvPrice.setText(String.format(Locale.getDefault(), "$%s", product.Price()));
            tvQuantity.setText(String.format(Locale.getDefault(), "Quantity: %s", product.Quantity()));
        }
    }
}
