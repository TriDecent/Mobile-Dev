package com.example.kiotz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiotz.R;
import com.example.kiotz.models.CustomProduct;

import java.util.ArrayList;

public class ProductsAdapterManager extends RecyclerView.Adapter<ProductsAdapterManager.MyViewHolder> {

    private Context context;
    private ArrayList<CustomProduct> products;

    public ProductsAdapterManager(Context context, ArrayList<CustomProduct> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductsAdapterManager.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.item_inventory,parent,false);

        return new ProductsAdapterManager.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapterManager.MyViewHolder holder, int position) {

        CustomProduct currentProduct=products.get(position);
        holder.imageViewProduct.setImageResource(currentProduct.Image());
        holder.textViewID.setText(currentProduct.ID());
        holder.textViewName.setText(currentProduct.Name());
        holder.textViewPrice.setText(String.valueOf(currentProduct.sellingPrice())+" VND");
        holder.textViewCategory.setText(currentProduct.Category());

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageViewProduct;
        TextView textViewID,textViewName,textViewPrice,textViewCategory;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            bindingView();

        }

        private void bindingView(){
            imageViewProduct=itemView.findViewById(R.id.imageViewProduct);
            textViewID=itemView.findViewById(R.id.tvIDProduct);
            textViewName=itemView.findViewById(R.id.tvNameProduct);
            textViewPrice=itemView.findViewById(R.id.tvPriceProduct);
            textViewCategory=itemView.findViewById(R.id.tvCategoryProduct);
        }
    }
}
