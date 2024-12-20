package com.example.kiotz.views.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.kiotz.R;
import com.example.kiotz.models.Employee;

import java.util.Locale;
import java.util.Objects;

public class EmployeeDetailsDialog {
    private final Context context;
    private final Employee currentSessionEmployee;
    private Employee selectedEmployee;
    private OnEmployeeUpdateListener updateListener;

    private TextView tvDialogId, tvDialogName, tvDialogEmail, tvDialogDate, tvGender, tvDialogPosition;

    public EmployeeDetailsDialog(Context context, Employee selectedEmployee, Employee currentSessionEmployee) {
        this.context = context;
        this.selectedEmployee = selectedEmployee;
        this.currentSessionEmployee = currentSessionEmployee;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.employee_view_dialog, null);
        builder.setView(dialogView);

        tvDialogId = dialogView.findViewById(R.id.tv_dialog_id);
        tvDialogName = dialogView.findViewById(R.id.tv_dialog_name);
        tvDialogEmail = dialogView.findViewById(R.id.tv_dialog_email);
        tvDialogDate = dialogView.findViewById(R.id.tv_dialog_date);
        tvGender = dialogView.findViewById(R.id.tv_dialog_gender);
        tvDialogPosition = dialogView.findViewById(R.id.tv_dialog_position);

        ImageView ivEdit = dialogView.findViewById(R.id.iv_edit);

        displayStudentDetails(selectedEmployee);

        ivEdit.setOnClickListener(v -> {
            if (currentSessionEmployee == selectedEmployee) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle("Not Available");
                builder1.setMessage("You are not allowed to edit your own account.");
                builder1.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                builder1.create().show();
                return;
            }

            var dialog = new EmployeeEditedDialog(context, selectedEmployee);
            dialog.show();

            dialog.setOnSaveClickListener(editedEmployee -> {
                if (editedEmployee == selectedEmployee) return;

                selectedEmployee = editedEmployee;

                displayStudentDetails(selectedEmployee);

                if (updateListener != null) {
                    updateListener.onEmployeeUpdated(selectedEmployee);
                }
            });
        });

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    public void setOnEmployeeUpdateListener(OnEmployeeUpdateListener listener) {
        this.updateListener = listener;
    }

    private void displayStudentDetails(Employee employee) {
        String employeeId = employee.ID();
        String employeeName = Objects.equals(employee.Name(), "") ? "N/A" : employee.Name();
        String employeeEmail = employee.Email();
        String employeeDate = Objects.equals(employee.Date(), "") ? "N/A" : employee.Date();
        String employeeGender = employee.Gender() == null ? "N/A" : employee.Gender().toString();
        String employeePosition = employee.IsAdmin() ? "Manager" : "Employee";

        tvDialogId.setText(String.format(Locale.getDefault(), employeeId));
        tvDialogName.setText(employeeName == null ? "" : String.format(Locale.getDefault(), employeeName));
        tvDialogEmail.setText(String.format(Locale.getDefault(), employeeEmail));
        tvDialogDate.setText(employeeDate == null ? "" : String.format(Locale.getDefault(), employeeDate));
        tvGender.setText(String.format(Locale.getDefault(), employeeGender));
        tvDialogPosition.setText(String.format(Locale.getDefault(), employeePosition));
    }

    public interface OnEmployeeUpdateListener {
        void onEmployeeUpdated(Employee employee);
    }
}