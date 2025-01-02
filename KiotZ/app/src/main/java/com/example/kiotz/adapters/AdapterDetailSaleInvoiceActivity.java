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
import com.example.kiotz.models.Product;
import com.example.kiotz.models.ProductInvoice;

import java.util.ArrayList;
import java.util.List;

public class AdapterDetailSaleInvoiceActivity extends RecyclerView.Adapter<AdapterDetailSaleInvoiceActivity.MyViewHolder> {

    private List<ProductInvoice> productInvoiceList;
    private Context context;

    private List<Product> productList;

    private final IItemFragment iItemFragment;

    public AdapterDetailSaleInvoiceActivity(List<ProductInvoice> productInvoiceList, Context context,List<Product> list,IItemFragment iItemFragment) {
        this.productInvoiceList = productInvoiceList;
        this.context = context;
        this.productList=list;
        this.iItemFragment=iItemFragment;


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.item_form7,parent,false);
        return new MyViewHolder(view,iItemFragment);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProductInvoice currentProductInvoice=productInvoiceList.get(position);
        Product currentProduct=null;
        for(Product p:productList){
            if(p.ID().equals(currentProductInvoice.getId())){
                currentProduct=new Product(p.ID(),p.Name(),p.Category(),p.Price(),p.Unit(),p.Quantity(),p.imageURL());
            }
        }

        if(currentProduct!=null){
            holder.tvName.setText(currentProduct.Name());
            holder.tvQuantity.setText(String.valueOf(currentProductInvoice.getQuantity()));
            holder.tvID.setText(currentProduct.ID());
            holder.tvTotalPrice.setText(String.valueOf(currentProductInvoice.getTotalPrice()));

            Glide.with(holder.imageView.getContext())
                    .load(currentProduct.imageURL())
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.img_error)
                    .into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return productInvoiceList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView tvName,tvQuantity,tvID,tvTotalPrice;


        public MyViewHolder(@NonNull View itemView, IItemFragment iItemFragment) {
            super(itemView);

            bindingViews(itemView);

           itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if(iItemFragment!=null){
                       int position=getAdapterPosition();
                       if(position!=RecyclerView.NO_POSITION){
                           iItemFragment.onItemClick(position);
                       }
                   }
               }
           });

           itemView.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View v) {
                   if(iItemFragment!=null){
                       int position=getAdapterPosition();
                       if(position!=RecyclerView.NO_POSITION){
                           iItemFragment.onLongItemClick(position);
                       }

                       return true;
                   }
                   return false;

               }
           });
        }

        private void bindingViews(View view){
            imageView=view.findViewById(R.id.imageView6);
            tvName=view.findViewById(R.id.tv_product_name);
            tvTotalPrice=view.findViewById(R.id.tv_product_price);
            tvID=view.findViewById(R.id.tv_product_id);
            tvQuantity=view.findViewById(R.id.tv_product_quantity);
        }
    }
}
