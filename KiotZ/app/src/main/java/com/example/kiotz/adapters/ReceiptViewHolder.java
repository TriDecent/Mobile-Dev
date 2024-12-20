package com.example.kiotz.adapters;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiotz.R;

public class ReceiptViewHolder extends RecyclerView.ViewHolder {
    ImageView client_avatar_iv;
    TextView employee_id_tv, client_name_tv, total_price_tv, date_tv, time_tv;
    public ReceiptViewHolder(@NonNull View itemView) {
        super(itemView);
        client_avatar_iv = itemView.findViewById(R.id.client_avatar_iv);
        employee_id_tv = itemView.findViewById(R.id.id_employee_tv);
        client_name_tv = itemView.findViewById(R.id.client_name_tv);
        total_price_tv = itemView.findViewById(R.id.total_price_tv);
        date_tv = itemView.findViewById(R.id.date_tv);
        time_tv = itemView.findViewById(R.id.time_tv);
    }
}
