package com.example.kiotz.views.general.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kiotz.R;
import com.example.kiotz.authentication.Authenticator;
import com.example.kiotz.database.FireBaseService;
import com.example.kiotz.database.dto.EmployeeSerializer;
import com.example.kiotz.enums.Gender;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.Employee;
import com.example.kiotz.models.UserCredentials;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.utilities.EmailUtils;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;
import com.example.kiotz.views.employees.activities.GeneralEmployeeActivity;
import com.example.kiotz.views.managers.activities.GeneralManagerActivity;
import com.example.kiotz.views.managers.data.App;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Querying Employees";
    Button btnLogin;
    TextView tvRegister;

    TextInputEditText etEmail;
    TextInputEditText etPassword;

    ProgressBar pbSignIn;
    Authenticator authenticator;

    InventoryViewModel<Employee> employeeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        authenticator = Authenticator.getInstance();

        Inventory<Employee> employeeInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new EmployeeSerializer())));
        employeeViewModel = InventoryViewModelFactory.getInstance().getViewModel(employeeInventory, Employee.class);

        var currentUser = authenticator.getCurrentUser();
        if (currentUser != null) {
            performAutomaticLogin(currentUser);
        }

        btnLogin = findViewById(R.id.buttonLogin);
        tvRegister = findViewById(R.id.tv_sign_in);

        etEmail = findViewById(R.id.et_sign_in_email);
        etPassword = findViewById(R.id.et_sign_in_password);

        pbSignIn = findViewById(R.id.pb_sign_in);

        btnLogin.setOnClickListener(v -> {
            pbSignIn.setVisibility(ProgressBar.VISIBLE);

            String email = Objects.requireNonNull(etEmail.getText()).toString();
            String password = Objects.requireNonNull(etPassword.getText()).toString();

            if (email.isEmpty()) {
                etEmail.setError("Email is required");
                pbSignIn.setVisibility(ProgressBar.GONE);
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Password is required");
                pbSignIn.setVisibility(ProgressBar.GONE);
                return;
            }

            UserCredentials userCredentials = new UserCredentials(email, password);

            authenticator.signIn(userCredentials, task -> {
                pbSignIn.setVisibility(ProgressBar.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                    handleUserLogin(authenticator.getCurrentUserId());
                } else {
                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvRegister.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), RegisterActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void performAutomaticLogin(FirebaseUser user) {
        handleUserLogin(user.getUid());
    }

    private void handleUserLogin(String userId) {
        employeeViewModel.getById(userId).thenAccept(employee -> {
            if (employee != null) {
                Intent i;
                App app= (App) getApplication();
                app.setName(employee.Name());
                var position = employee.IsAdmin() ? "Manager" : "Employee";
                app.setPosition(position);
                if (employee.IsAdmin()) {
                    i = new Intent(this, GeneralManagerActivity.class);
                } else {
                    i = new Intent(this, GeneralEmployeeActivity.class);
                }
                Toast.makeText(this, "Welcome Back " +
                        EmailUtils.getUsernameFromEmail(employee.Email()), Toast.LENGTH_SHORT).show();
                startActivity(i);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Failed to retrieve employee.", Toast.LENGTH_LONG).show();
            }
        }).exceptionally(ex -> {
            Toast.makeText(LoginActivity.this, "Error retrieving employee: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        });
    }

    private void testInventoryViewModel() {
        // Test getAll
        employeeViewModel.getAll().thenAccept(employees -> {
            for (var employee : employees) {
                Log.d(TAG, "Employee: " + employee);
            }
        });

        Gender gender = Gender.MALE;

        // Test add
        String date = "12-11-2004";
        Employee newEmployee = new Employee("id123", "john.doe@example.com", "John Doe", date, gender, false);
        employeeViewModel.add(newEmployee).thenRun(() -> {
            Log.d(TAG, "Employee added: " + newEmployee);
            employeeViewModel.getAll().thenAccept(employees -> {
                if (employees.contains(newEmployee)) {
                    Log.d(TAG, "Employee successfully added to the list.");
                } else {
                    Log.d(TAG, "Failed to add employee to the list.");
                }
            });
        });

        // Test delete
        Employee employeeToDelete = new Employee("id123", "john.doe@example.com", "John Doe", date, gender, false);
        employeeViewModel.delete(employeeToDelete).thenRun(() -> {
            Log.d(TAG, "Employee deleted: " + employeeToDelete);
            employeeViewModel.getAll().thenAccept(employees -> {
                if (!employees.contains(employeeToDelete)) {
                    Log.d(TAG, "Employee successfully deleted from the list.");
                } else {
                    Log.d(TAG, "Failed to delete employee from the list.");
                }
            });
        });

        // Test update
        Employee currentEmployee = new Employee("id123", "john.doe@example.com", "John Doe", date, gender, false);
        Employee updatedEmployee = new Employee("id123", "john.doe@example.com", "John Doe", date, gender, true);
        employeeViewModel.update(currentEmployee, updatedEmployee).thenRun(() -> {
            Log.d(TAG, "Employee updated: " + updatedEmployee);
            employeeViewModel.getAll().thenAccept(employees -> {
                if (employees.contains(updatedEmployee) && !employees.contains(currentEmployee)) {
                    Log.d(TAG, "Employee successfully updated in the list.");
                } else {
                    Log.d(TAG, "Failed to update employee in the list.");
                }
            });
        });

        // Test getById
        String employeeId = "id123";
        employeeViewModel.getById(employeeId).thenAccept(employee -> {
            if (employee != null) {
                Log.d(TAG, "Employee retrieved: " + employee);
            } else {
                Log.d(TAG, "Failed to retrieve employee with ID: " + employeeId);
            }
        });
    }
}