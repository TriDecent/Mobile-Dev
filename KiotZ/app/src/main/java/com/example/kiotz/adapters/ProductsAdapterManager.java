package com.example.kiotz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiotz.R;
import com.example.kiotz.models.CustomProduct;
import com.example.kiotz.models.Product;

import java.util.ArrayList;

public class ProductsAdapterManager extends RecyclerView.Adapter<ProductsAdapterManager.MyViewHolder> {

    private Context context;
    private ArrayList<Product> products;

    IRecycleManagerDetail iRecycleManagerDetail;

    public ProductsAdapterManager(Context context, ArrayList<Product> products,IRecycleManagerDetail iRecycleManagerDetail) {
        this.context = context;
        this.products = products;
        this.iRecycleManagerDetail=iRecycleManagerDetail;
    }

    @NonNull
    @Override
    public ProductsAdapterManager.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.item_inventory,parent,false);

        return new ProductsAdapterManager.MyViewHolder(view,this.iRecycleManagerDetail);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapterManager.MyViewHolder holder, int position) {

        Product currentProduct=products.get(position);
        //holder.imageViewProduct.setImageResource(currentProduct.Image());
        holder.imageViewProduct.setImageResource(R.drawable.img_test2);
        holder.textViewID.setText(currentProduct.ID());
        holder.textViewName.setText(currentProduct.Name());
        holder.textViewPrice.setText(String.valueOf(currentProduct.Price())+" VND");
        holder.textViewCategory.setText(currentProduct.Category());

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageViewProduct;
        TextView textViewID,textViewName,textViewPrice,textViewCategory;

        CardView cv;

        IRecycleManagerDetail iRecycleManagerDetail;

        public MyViewHolder(@NonNull View itemView, IRecycleManagerDetail iRecycleManagerDetail) {
            super(itemView);

            bindingView();
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(iRecycleManagerDetail!=null){
                        int position=getAdapterPosition();
                        iRecycleManagerDetail.onItemClick(position);
                    }
                }
            });

        }

        private void bindingView(){
            imageViewProduct=itemView.findViewById(R.id.imageViewProduct);
            textViewID=itemView.findViewById(R.id.tvIDProduct);
            textViewName=itemView.findViewById(R.id.tvNameProduct);
            textViewPrice=itemView.findViewById(R.id.tvPriceProduct);
            textViewCategory=itemView.findViewById(R.id.tvCategoryProduct);
            cv = itemView.findViewById(R.id.cv_product);

        }
    }
}
