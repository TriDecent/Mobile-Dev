package com.example.kiotz.adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiotz.R;
import com.example.kiotz.authentication.Authenticator;
import com.example.kiotz.enums.Gender;
import com.example.kiotz.models.Employee;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.views.dialogs.EmployeeDetailsDialog;
import com.example.kiotz.views.managers.activities.DetailEmployeeInforActivity;

import java.util.List;
import java.util.Locale;

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
        setupClickListeners(holder, selectedEmployee);
        setEmployeeAvatar(holder, selectedEmployee);
    }

    private void setupClickListeners(MyViewHolder holder, Employee selectedEmployee) {
        if (employeeViewModel != null) {
            setupEditClickListener(holder, selectedEmployee);
            setupDeleteClickListener(holder, selectedEmployee);
        } else {
            setupViewDetailsClickListener(holder);
        }
    }

    private void setupEditClickListener(MyViewHolder holder, Employee selectedEmployee) {
        holder.cvEmployee.setOnClickListener(v -> {
            var dialog = new EmployeeDetailsDialog(context, selectedEmployee, currentSessionEmployee);
            dialog.show();
            dialog.setOnEmployeeUpdateListener(updatedEmployee ->
                    employeeViewModel
                            .update(selectedEmployee, updatedEmployee)
                            .thenRun(() -> {
                                // the ones below is not needed
                                // because I used the observer in the EmployeesView class
                                // to listen for changes in the list of employees
                                // just put here for reference

                                // employees.set(position, updatedEmployee);
                                // notifyItemChanged(position);
                            }));
        });
    }

    private void setupDeleteClickListener(MyViewHolder holder, Employee selectedEmployee) {
        holder.cvEmployee.setOnLongClickListener(v -> {
            if (selectedEmployee == currentSessionEmployee) {
                showCannotDeleteOwnAccountDialog();
            } else {
                showDeleteConfirmationDialog(selectedEmployee);
            }
            return true;
        });
    }

    private void showCannotDeleteOwnAccountDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Not Available")
                .setMessage("You cannot remove your own account")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void showDeleteConfirmationDialog(Employee willBeDeletedEmployee) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm Delete")
                .setMessage(
                        "Are you sure to delete the employee id " +
                                willBeDeletedEmployee.ID() +
                                " and " + (willBeDeletedEmployee.Gender() == Gender.FEMALE ? "her" : "his") +
                                " account?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    var authenticator = Authenticator.getInstance();
                    authenticator.deleteAccount(willBeDeletedEmployee.ID())
                            .thenAccept(result -> {
                                if (result.first) {
                                    employeeViewModel.delete(willBeDeletedEmployee).thenRun(() ->
                                            Toast.makeText(context,
                                                    "Employee and account deleted successfully",
                                                    Toast.LENGTH_SHORT).show());

                                } else {
                                    Toast.makeText(context,
                                            "Failed to delete account: " + result.second,
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void setupViewDetailsClickListener(MyViewHolder holder) {
        holder.cvEmployee.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Employee selectedEmployee = employees.get(position);
                if (selectedEmployee != null) {
                    navigateToDetails(selectedEmployee);
                }
            }
        });
    }

    private void navigateToDetails(Employee employee) {
        Intent intent = new Intent(context, DetailEmployeeInforActivity.class);
        intent.putExtra("dataEmployee", employee);
        startActivity(context, intent, null);
    }

    private void setEmployeeAvatar(MyViewHolder holder, Employee employee) {
        if (employee.IsAdmin()) {
            holder.ivEmployee.setImageResource(R.drawable.ic_manager);
        } else if (employee.Gender() == Gender.FEMALE) {
            holder.ivEmployee.setImageResource(R.drawable.ic_female_employee);
        } else {
            holder.ivEmployee.setImageResource(R.drawable.ic_male_employee);
        }
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