package com.example.kiotz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kiotz.R;
import com.example.kiotz.models.ItemReceipt;

import java.util.List;

public class ProductInReceiptAdapter extends RecyclerView.Adapter<ProductInReceiptAdapter.MyViewHolder> {

    private Context context;
    private List<ItemReceipt> itemReceipts;

    public ProductInReceiptAdapter(Context context, List<ItemReceipt> itemReceipts) {
        this.context = context;
        this.itemReceipts = itemReceipts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.item_form7,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ItemReceipt itemReceipt=itemReceipts.get(position);
        holder.tvId.setText(itemReceipt.getId());
        holder.tvName.setText(itemReceipt.getName());
        holder.tvPrice.setText(String.valueOf(itemReceipt.getPrice()));
        holder.tvQuantity.setText(String.valueOf(itemReceipt.getQuantity()));

        Glide.with(holder.imageView.getContext())
                .load(itemReceipt.getImage())
                .placeholder(R.drawable.loading)
                .error(R.drawable.img_error)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return itemReceipts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvName,tvId,tvPrice,tvQuantity;
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            bindingViews(itemView);
        }

        private void bindingViews(View view){
            tvName=view.findViewById(R.id.tv_product_name);
            tvId=view.findViewById(R.id.tv_product_id);
            tvPrice=view.findViewById(R.id.tv_product_price);
            tvQuantity=view.findViewById(R.id.tv_product_quantity);
            imageView=view.findViewById(R.id.imageView6);
        }
    }
}
