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
    private Employee employee;
    private OnEmployeeUpdateListener updateListener;

    private TextView tvDialogId, tvDialogName, tvDialogEmail, tvDialogDate, tvGender, tvDialogPosition;

    public EmployeeDetailsDialog(Context context, Employee employee) {
        this.context = context;
        this.employee = employee;
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

        displayStudentDetails(employee);

        ivEdit.setOnClickListener(v -> {
            var dialog = new EmployeeEditedDialog(context, employee);
            dialog.show();

            dialog.setOnSaveClickListener(editedEmployee -> {
                if (editedEmployee == employee) return;

                employee = editedEmployee;

                displayStudentDetails(employee);

                if (updateListener != null) {
                    updateListener.onEmployeeUpdated(employee);
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