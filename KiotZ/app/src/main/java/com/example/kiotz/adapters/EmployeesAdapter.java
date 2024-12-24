package com.example.kiotz.adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiotz.R;
import com.example.kiotz.enums.Gender;
import com.example.kiotz.models.Employee;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.views.dialogs.EmployeeDetailsDialog;
import com.example.kiotz.views.managers.activities.DetailEmployeeInforActivity;
import com.example.kiotz.views.managers.activities.ViewInformationEmployeeActivity;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EmployeesAdapter extends RecyclerView.Adapter<EmployeesAdapter.MyViewHolder> {
    private final Context context;
    private final Employee currentSessionEmployee;
    private final List<Employee> employees;
    private final InventoryViewModel<Employee> employeeViewModel;

    public EmployeesAdapter(Context context, Employee currentSessionEmployee, List<Employee> employees, InventoryViewModel<Employee> employeeViewModel) {
        this.context = context;
        this.currentSessionEmployee = currentSessionEmployee;
        this.employees = employees;
        this.employeeViewModel = employeeViewModel;
    }

    @NonNull
    @Override
    public EmployeesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var inflater = LayoutInflater.from(context);
        var view = inflater.inflate(R.layout.item_employee_view, parent, false);
        return new EmployeesAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        var selectedEmployee = employees.get(position);
        holder.bindData(selectedEmployee);

        if(employeeViewModel!=null){
            holder.cvEmployee.setOnClickListener(v -> {
                var dialog = new EmployeeDetailsDialog(context, selectedEmployee, currentSessionEmployee);
                dialog.show();

                dialog.setOnEmployeeUpdateListener(updatedEmployee ->
                        employeeViewModel
                                .update(selectedEmployee, updatedEmployee)
                                .thenRun(() -> {

                                    // the ones below is not needed because we used the observer in the EmployeesView class
                                    // to listen for changes in the list of employees
                                    // just put here for reference

                                    // employees.set(position, updatedEmployee);
                                    // notifyItemChanged(position);
                                }));
            });
        }
        else{

            holder.cvEmployee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i=holder.getAdapterPosition();
                    if(i!=RecyclerView.NO_POSITION){
                        Employee selectedEmployee=employees.get(i);
                        if(selectedEmployee!=null){
                            Intent intent=new Intent(context, DetailEmployeeInforActivity.class);
                            intent.putExtra("dataEmployee",selectedEmployee);
                            startActivity(context,intent,null);
                        }
                    }

                }
            });

        }



        if (selectedEmployee.IsAdmin()) {
            holder.ivEmployee.setImageResource(R.drawable.ic_manager);
            return;
        }

        if (selectedEmployee.Gender() == Gender.FEMALE) {
            holder.ivEmployee.setImageResource(R.drawable.ic_female_employee);
            return;
        }

        holder.ivEmployee.setImageResource(R.drawable.ic_male_employee);
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivEmployee;
        private final TextView tvId, tvName, tvDate, tvGender;
        private final CardView cvEmployee;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivEmployee = itemView.findViewById(R.id.iv_employee_avatar);
            tvId = itemView.findViewById(R.id.tv_employee_id);
            tvName = itemView.findViewById(R.id.tv_employee_name);
            tvDate = itemView.findViewById(R.id.tv_employee_item_date);
            tvGender = itemView.findViewById(R.id.tv_employee_gender);
            cvEmployee = itemView.findViewById(R.id.cv_employee);
        }

        public void bindData(Employee employee) {
            String employeeId = employee.ID();
            String employeeName = employee.Name() == null ? "N/A" : employee.Name();
            String employeeDate = employee.Date() == null ? "N/A" : employee.Date();
            String employeeGender = employee.Gender() == null ? "N/A" : employee.Gender().toString();

            tvId.setText(String.format(Locale.getDefault(), employeeId));
            tvName.setText(String.format(Locale.getDefault(), employeeName));
            tvDate.setText(String.format(Locale.getDefault(), employeeDate));
            tvGender.setText(String.format(Locale.getDefault(), employeeGender));
        }
    }
}