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
import com.example.kiotz.models.Employee;

import java.util.List;

public class DeleteEmployeesAdapter extends RecyclerView.Adapter<DeleteEmployeesAdapter.MyViewHolder> {


    private final Context context;
    private final List<Employee> employees;

    private final IItemFragment iItemFragment;

    public DeleteEmployeesAdapter(Context context, List<Employee> employees,IItemFragment iItemFragment) {
        this.context = context;
        this.employees = employees;
        this.iItemFragment=iItemFragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //return null;
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.item_employee_delete,parent,false);
        return new MyViewHolder(view,iItemFragment);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Employee currentEmployee=employees.get(position);
        holder.tvID.setText(currentEmployee.ID());
        holder.tvName.setText(currentEmployee.Name());

    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private  ImageView imageViewAvatar,imageViewRecycle;
        private  TextView tvID, tvName;

        public MyViewHolder(@NonNull View itemView,IItemFragment iItemFragment) {
            super(itemView);
            bindingViews(itemView);

            imageViewRecycle.setOnClickListener(new View.OnClickListener() {
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


        private void bindingViews(View v){
            imageViewAvatar=v.findViewById(R.id.iv_employee_avatar_delete);
            imageViewRecycle=v.findViewById(R.id.imageViewRecycleBin);
            tvID=v.findViewById(R.id.tv_employee_id_delete);
            tvName=v.findViewById(R.id.tv_employee_name_delete);

        }
    }
}
