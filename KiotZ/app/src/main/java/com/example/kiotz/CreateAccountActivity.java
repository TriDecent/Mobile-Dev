package com.example.kiotz;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kiotz.authentication.Authenticator;
import com.example.kiotz.database.FireBaseService;
import com.example.kiotz.database.dto.EmployeeSerializer;
import com.example.kiotz.enums.Gender;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.Account;
import com.example.kiotz.models.Employee;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {
    TextInputEditText etEmail, etPassword;
    EditText etName, etDate;
    SwitchCompat swIsAdmin;
    Button btnCreateAccount;
    ProgressBar pbCreateAccount;
    RadioButton rbMale, rbFemale;

    Authenticator authenticator;
    InventoryViewModel<Employee> employeeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.tiet_email);
        etPassword = findViewById(R.id.tiet_password);
        etName = findViewById(R.id.et_employee_name);
        etDate = findViewById(R.id.et_employee_date);
        swIsAdmin = findViewById(R.id.sw_is_admin);
        btnCreateAccount = findViewById(R.id.btn_create_account);
        pbCreateAccount = findViewById(R.id.pb_create_account);
        rbMale = findViewById(R.id.rb_male);
        rbFemale = findViewById(R.id.rb_female);

        etDate.setFocusable(false);
        etDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String date = String.format(Locale.getDefault(), "%d-%d-%d", dayOfMonth, month + 1, year);
                etDate.setText(date);
            }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        authenticator = Authenticator.getInstance();

        Inventory<Employee> employeeInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new EmployeeSerializer())));
        employeeViewModel = InventoryViewModelFactory.getInstance().getViewModel(employeeInventory, Employee.class);

        btnCreateAccount.setOnClickListener(v -> {
            pbCreateAccount.setVisibility(ProgressBar.VISIBLE);

            String email = Objects.requireNonNull(etEmail.getText()).toString();
            String password = Objects.requireNonNull(etPassword.getText()).toString();
            String name = Objects.requireNonNull(etName.getText()).toString();
            String date = Objects.requireNonNull(etDate.getText()).toString();
            Gender gender = rbMale.isChecked() ? Gender.MALE : Gender.FEMALE;

            boolean isAdmin = swIsAdmin.isChecked();

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email is required");
                pbCreateAccount.setVisibility(ProgressBar.GONE);
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Password is required");
                pbCreateAccount.setVisibility(ProgressBar.GONE);
                return;
            }

            if (TextUtils.isEmpty(name)) {
                etName.setError("Name is required");
                pbCreateAccount.setVisibility(ProgressBar.GONE);
                return;
            }

            if (TextUtils.isEmpty(date)) {
                etDate.setError("Date is required");
                pbCreateAccount.setVisibility(ProgressBar.GONE);
                return;
            }

            Account account = new Account(email, password);

            authenticator.register(account, task -> {
                pbCreateAccount.setVisibility(ProgressBar.GONE);
                if (task.isSuccessful()) {
                    var employeeId = authenticator.getCurrentUserId();
                    employeeViewModel.add(new Employee(employeeId, email, name, date, gender, isAdmin));
                    Toast.makeText(this, "Account created.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Create account failed (Your email probably already exists).", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}