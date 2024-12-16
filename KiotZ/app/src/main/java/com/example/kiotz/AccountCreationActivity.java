package com.example.kiotz;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kiotz.authentication.Authenticator;
import com.example.kiotz.database.FireBaseService;
import com.example.kiotz.database.dto.EmployeeSerializer;
import com.example.kiotz.enums.Gender;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.Employee;
import com.example.kiotz.models.UserCredentials;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AccountCreationActivity extends AppCompatActivity {
    private TextInputEditText etEmail, etPassword;
    private EditText etName, etDate;
    private Button btnCreateAccount;
    private ProgressBar pbCreateAccount;
    private RadioButton rbMale, rbManager;
    private TextView tvEmployeeName, tvEmployeePosition;

    private Authenticator authenticator;
    private InventoryViewModel<Employee> employeeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        initializeComponents();
        setupDatePicker();
        setupCreateAccountButton();
        setupStatusBar();
    }

    private void setupUI() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);
        setupWindowInsets();
        bindViews();
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void bindViews() {
        etEmail = findViewById(R.id.tiet_email);
        etPassword = findViewById(R.id.tiet_password);
        etName = findViewById(R.id.et_employee_name);
        etDate = findViewById(R.id.et_employee_date);
        rbManager = findViewById(R.id.rb_account_creation_manager);
        rbMale = findViewById(R.id.rb_account_creation_male);
        btnCreateAccount = findViewById(R.id.btn_create_account);
        pbCreateAccount = findViewById(R.id.pb_create_account);
        tvEmployeeName = findViewById(R.id.tv_status_bar_employee_name);
        tvEmployeePosition = findViewById(R.id.tv_status_bar_position);
    }

    private void initializeComponents() {
        authenticator = Authenticator.getInstance();
        var employeeInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new EmployeeSerializer())));
        employeeViewModel = InventoryViewModelFactory.getInstance().getViewModel(employeeInventory, Employee.class);
    }

    private void setupDatePicker() {
        etDate.setFocusable(false);
        etDate.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        var calendar = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%d-%d-%d", dayOfMonth, month + 1, year);
                    etDate.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void setupCreateAccountButton() {
        btnCreateAccount.setOnClickListener(v -> handleCreateAccount());
    }

    private void handleCreateAccount() {
        pbCreateAccount.setVisibility(ProgressBar.VISIBLE);
        if (!validateInputs()) {
            pbCreateAccount.setVisibility(ProgressBar.GONE);
            return;
        }

        createAccount();
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(etEmail.getText())) {
            etEmail.setError("Email is required");
            return false;
        }
        if (TextUtils.isEmpty(etPassword.getText())) {
            etPassword.setError("Password is required");
            return false;
        }
        if (TextUtils.isEmpty(etName.getText())) {
            etName.setError("Name is required");
            return false;
        }
        if (TextUtils.isEmpty(etDate.getText())) {
            etDate.setError("Date is required");
            return false;
        }
        return true;
    }

    private void createAccount() {
        var credentials = new UserCredentials(
                Objects.requireNonNull(etEmail.getText()).toString(),
                Objects.requireNonNull(etPassword.getText()).toString()
        );

        authenticator.register(credentials, this::handleRegistrationResult);
    }

    private void handleRegistrationResult(Task<AuthResult> task) {
        pbCreateAccount.setVisibility(ProgressBar.GONE);
        if (task.isSuccessful()) {
            createEmployee();
            clearForm();
        } else {
            Toast.makeText(this, "Create account failed (Email may already exist).", Toast.LENGTH_SHORT).show();
            clearForm();
        }
    }

    private void createEmployee() {
        var employeeId = authenticator.getCurrentUserId();
        var employee = new Employee(
                employeeId,
                Objects.requireNonNull(etEmail.getText()).toString(),
                Objects.requireNonNull(etName.getText()).toString(),
                Objects.requireNonNull(etDate.getText()).toString(),
                rbMale.isChecked() ? Gender.MALE : Gender.FEMALE,
                rbManager.isChecked()
        );

        employeeViewModel
                .add(employee)
                .thenRun(() -> Toast.makeText(this, "Account created.", Toast.LENGTH_SHORT).show());
    }

    private void setupStatusBar() {
        var userId = authenticator.getCurrentUserId();
        employeeViewModel.getById(userId)
                .thenAccept(currentUser -> runOnUiThread(() -> updateStatusBar(currentUser)));
    }

    private void updateStatusBar(Employee currentUser) {
        tvEmployeeName.setText(currentUser.Name());
        var position = currentUser.IsAdmin() ? "Manager" : "Employee";
        tvEmployeePosition.setText(position);
    }

    private void clearForm() {
        etEmail.setText("");
        etPassword.setText("");
        etName.setText("");
        etDate.setText("");
        rbMale.setChecked(true);
        rbManager.setChecked(false);
    }
}