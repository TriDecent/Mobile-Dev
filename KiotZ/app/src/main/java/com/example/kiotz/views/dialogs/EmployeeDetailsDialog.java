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
    private final Employee employee;

    public EmployeeDetailsDialog(Context context, Employee employee) {
        this.context = context;
        this.employee = employee;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.employee_view_dialog, null);
        builder.setView(dialogView);

        TextView tvDialogId = dialogView.findViewById(R.id.tv_dialog_id);
        TextView tvDialogName = dialogView.findViewById(R.id.tv_dialog_name);
        TextView tvDialogEmail = dialogView.findViewById(R.id.tv_dialog_email);
        TextView tvDialogDate = dialogView.findViewById(R.id.tv_dialog_date);
        TextView tvGender = dialogView.findViewById(R.id.tv_dialog_gender);
        TextView tvDialogStatus = dialogView.findViewById(R.id.tv_dialog_position);
        ImageView ivEdit = dialogView.findViewById(R.id.iv_edit);

        // Set initial student details
        updateStudentDetails(tvDialogId, tvDialogName, tvDialogEmail, tvDialogDate, tvGender, tvDialogStatus);

        //        // Open EditStudentDialog with a callback to refresh this dialog's UI
        //        ivEdit.setOnClickListener(v ->
        //                new EditStudentDialog(context, employee, viewModel, (newStudent) -> {
        //                    // Re-fetch updated student data and refresh UI
        //                    updateStudentDetails(tvDialogId, tvDialogName, tvDialogEmail, tvDialogDate, tvDialogStatus, newStudent);
        //                }).show()
        //        );

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void updateStudentDetails(
            TextView tvId, TextView tvName, TextView tvEmail,
            TextView tvDate, TextView tvGender, TextView tvPosition) {

        String employeeId = employee.ID();
        String employeeName = Objects.equals(employee.Name(), "") ? "N/A" : employee.Name();
        String employeeEmail = employee.Email();
        String employeeDate = Objects.equals(employee.Date(), "") ? "N/A" : employee.Date();
        String employeeGender = employee.Gender() == null ? "N/A" : employee.Gender().toString();
        String employeePosition = employee.IsAdmin() ? "Manager" : "Employee";

        tvId.setText(String.format(Locale.getDefault(), employeeId));
        tvName.setText(employeeName == null ? "" : String.format(Locale.getDefault(), employeeName));
        tvEmail.setText(String.format(Locale.getDefault(), employeeEmail));
        tvDate.setText(employeeDate == null ? "" : String.format(Locale.getDefault(), employeeDate));
        tvGender.setText(String.format(Locale.getDefault(), employeeGender));
        tvPosition.setText(String.format(Locale.getDefault(), employeePosition));
    }
}
