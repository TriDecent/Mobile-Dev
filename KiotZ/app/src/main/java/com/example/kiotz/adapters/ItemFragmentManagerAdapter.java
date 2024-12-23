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

public class ItemFragmentManagerAdapter extends RecyclerView.Adapter<ItemFragmentManagerAdapter.MyViewHolder> {


    private final Context context;
    private final List<ItemFragment> itemFragmentList;
    private final IItemFragment iItemFragment;

    public ItemFragmentManagerAdapter(Context context, List<ItemFragment> itemFragments,IItemFragment iItemFragment) {
        this.context = context;
        this.itemFragmentList = itemFragments;
        this.iItemFragment=iItemFragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //return null;
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.item_fragment,parent,false);
        return new ItemFragmentManagerAdapter.MyViewHolder(view,this.iItemFragment);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemFragment currentItem=itemFragmentList.get(position);
        holder.tvTitle.setText(currentItem.getTitle());
        holder.image.setImageResource(currentItem.getImage());

    }

    @Override
    public int getItemCount() {
        return itemFragmentList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle;
        ImageView image;
        public MyViewHolder(@NonNull View itemView,IItemFragment iItemFragment) {
            super(itemView);
            bindingView();

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

        }

        public void bindingView(){
            tvTitle=itemView.findViewById(R.id.tv_item_fragment);
            image=itemView.findViewById(R.id.imageView_item_fragment);
        }
    }


}
