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
import com.example.kiotz.models.ItemFragment;

import java.util.List;

public class ItemFragmentEmployeeAdapter extends RecyclerView.Adapter<ItemFragmentEmployeeAdapter.MyViewHolder> {


    private final Context context;
    private final List<ItemFragment> itemFragments;

    public ItemFragmentEmployeeAdapter(Context context, List<ItemFragment> itemFragments) {
        this.context = context;
        this.itemFragments = itemFragments;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //return null;
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.item_fragment_employee,parent,false);
        return new ItemFragmentEmployeeAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemFragment currentItem=itemFragments.get(position);
        holder.tvTitle.setText(currentItem.getTitle());
        holder.image.setImageResource(currentItem.getImage());

    }

    @Override
    public int getItemCount() {
        return itemFragments.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle;
        ImageView image;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            bindingView();
        }

        public void bindingView(){
            tvTitle=itemView.findViewById(R.id.tv_item_fragment);
            image=itemView.findViewById(R.id.imageView_item_fragment);
        }
    }


}
