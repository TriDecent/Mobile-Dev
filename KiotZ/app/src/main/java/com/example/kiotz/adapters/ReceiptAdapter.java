package com.example.kiotz.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiotz.R;
import com.example.kiotz.models.Employee;
import com.example.kiotz.models.Receipt;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptViewHolder> {
    Context context;
    List<Receipt> receiptList;
    public ReceiptAdapter(Context context1, List<Receipt> list)
    {
        context = context1;
        receiptList = list;
    }

    @NonNull
    @Override
    public ReceiptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReceiptViewHolder(LayoutInflater.from(context).inflate(R.layout.item_invoice,parent,false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ReceiptViewHolder holder, int position) {
//        TODO: set user avatar
//      holder.client_avatar_iv =
        int employee_id_size = receiptList.get(position).EmployeeId().length();
        if (employee_id_size > 15)
            holder.employee_id_tv.setText(receiptList.get(position).EmployeeId().substring(0, 15) + "...");
        else
            holder.employee_id_tv.setText(receiptList.get(position).EmployeeId());

        int client_name_size = receiptList.get(position).CustomerName().length();
        if (client_name_size > 15)
            holder.employee_id_tv.setText(receiptList.get(position).CustomerName().substring(0, 15) + "...");
        else
            holder.client_name_tv.setText(receiptList.get(position).CustomerName());

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###");
        holder.total_price_tv.setText(String.valueOf(decimalFormat.format(receiptList.get(position).TotalPrice())));
        LocalDateTime dateTime = receiptList.get(position).DateTime();
        holder.date_tv.setText(dateTime.getDayOfMonth() + "/" + dateTime.getMonthValue() + "/" + dateTime.getYear());
        holder.time_tv.setText(dateTime.getHour() + ":" + dateTime.getMinute());
    }

    @Override
    public int getItemCount() {
        return  receiptList.size();
    }
}
