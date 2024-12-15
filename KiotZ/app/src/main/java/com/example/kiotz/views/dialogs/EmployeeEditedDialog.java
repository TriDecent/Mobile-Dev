package com.example.kiotz.views.dialogs;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.kiotz.R;
import com.example.kiotz.enums.Gender;
import com.example.kiotz.models.Employee;

import java.util.Calendar;
import java.util.Locale;

public class EmployeeEditedDialog {
    private final Context context;
    private final Employee employee;
    private OnSaveClickListener listener;
    private AlertDialog.Builder builder;
    private EditText etName, etDate;
    private TextView tvId, tvEmail;
    private RadioButton rbManager, rbEmployee, rbMale, rbFemale;

    public EmployeeEditedDialog(Context context, Employee employee) {
        this.context = context;
        this.employee = employee;
    }

    public void setOnSaveClickListener(OnSaveClickListener listener) {
        this.listener = listener;
    }

    public void show() {
        setupDialog();
        setupDatePicker();
        populateData();
        setupButtons();
        builder.show();
    }

    private void setupDialog() {
        builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Employee");

        var parent = new FrameLayout(context);
        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.employee_edit_dialog, parent, false);

        builder.setView(dialogView);

        initializeViewsFromDialog(dialogView);
    }

    private void initializeViewsFromDialog(View dialogView) {
        tvId = dialogView.findViewById(R.id.tv_edit_dialog_id);
        etName = dialogView.findViewById(R.id.et_edit_dialog_name);
        tvEmail = dialogView.findViewById(R.id.tv_edit_dialog_email);
        etDate = dialogView.findViewById(R.id.et_edit_dialog_date);
        rbManager = dialogView.findViewById(R.id.rb_edit_dialog_manager);
        rbEmployee = dialogView.findViewById(R.id.rb_edit_dialog_employee);
        rbMale = dialogView.findViewById(R.id.rb_edit_dialog_male);
        rbFemale = dialogView.findViewById(R.id.rb_edit_dialog_female);
    }

    private void setupDatePicker() {
        etDate.setFocusable(false);
        etDate.setOnClickListener(v -> showDatePicker());
    }

    private void populateData() {
        tvId.setText(String.valueOf(employee.ID()));
        etName.setText(employee.Name());
        tvEmail.setText(employee.Email());
        etDate.setText(employee.Date());
        setInitialGender();
        setInitialPosition();
    }

    private void setInitialGender() {
        if (employee.Gender() == null) {
            rbMale.setChecked(false);
            rbFemale.setChecked(false);
        } else {
            rbMale.setChecked(employee.Gender() == Gender.MALE);
            rbFemale.setChecked(employee.Gender() == Gender.FEMALE);
        }
    }

    private void setInitialPosition() {
        rbManager.setChecked(employee.IsAdmin());
        rbEmployee.setChecked(!employee.IsAdmin());
    }

    private void setupButtons() {
        builder.setPositiveButton("Save", (dialog, which) -> handleSave());
        builder.setNegativeButton("Cancel", null);
    }

    private void handleSave() {
        if (listener != null) {
            listener.onSave(createEditedEmployee());
        }
    }

    private Employee createEditedEmployee() {
        return new Employee(
                employee.ID(),
                employee.Email(),
                etName.getText().toString(),
                etDate.getText().toString(),
                determineGender(),
                rbManager.isChecked()
        );
    }

    private Gender determineGender() {
        if (!rbMale.isChecked() && !rbFemale.isChecked()) return null;
        return rbMale.isChecked() ? Gender.MALE : Gender.FEMALE;
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(context,
                (view, year, month, day) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%d-%d-%d", day, month + 1, year);
                    etDate.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    public interface OnSaveClickListener {
        void onSave(Employee employee);
    }
}

