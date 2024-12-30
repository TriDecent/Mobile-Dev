package com.example.kiotz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiotz.R;
import com.example.kiotz.models.Product;
import com.example.kiotz.models.ProductInvoice;

import java.util.List;

public class AdapterInvoiceSale extends RecyclerView.Adapter<AdapterInvoiceSale.MyViewHolder> {

    private List<ProductInvoice> products;
    private Context context;

    public AdapterInvoiceSale(List<ProductInvoice> products, Context context) {
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.item_invoice_sale,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProductInvoice currentProduct=products.get(position);
        holder.tvIDProduct.setText(currentProduct.getId());
        holder.tvQuantityProduct.setText("X "+ String.valueOf(currentProduct.getQuantity()));
        holder.tvPriceProduct.setText(String.valueOf(currentProduct.getTotalPrice()));



    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{


        private TextView tvIDProduct,tvQuantityProduct,tvPriceProduct;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            bindingViews(itemView);
        }

        private void bindingViews(View view){
            tvIDProduct=view.findViewById(R.id.tvIDProductSale);
            tvQuantityProduct=view.findViewById(R.id.tvQuantityProductSale);
            tvPriceProduct=view.findViewById(R.id.tvTotalPriceProductSale);
        }
    }
}
