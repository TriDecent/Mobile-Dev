package com.example.kiotz.views.general.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.kiotz.database.serializers.EmployeeSerializer;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.Account;
import com.example.kiotz.models.Employee;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;
import com.example.kiotz.views.employees.activities.GeneralEmployeeActivity;
import com.example.kiotz.views.managers.activities.GeneralManagerActivity;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Querying Employees";
    Button btnLogin;
    TextView tvRegister;

    TextInputEditText etEmail;
    TextInputEditText etPassword;

    ProgressBar pbSignIn;
    Authenticator authenticator;

    Inventory<Employee> employeeInventory;
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

        authenticator = new Authenticator();

        btnLogin = findViewById(R.id.buttonLogin);
        tvRegister = findViewById(R.id.tv_sign_in);

        etEmail = findViewById(R.id.et_sign_in_email);
        etPassword = findViewById(R.id.et_sign_in_password);

        pbSignIn = findViewById(R.id.pb_sign_in);

        testInventoryViewModel();

        btnLogin.setOnClickListener(v -> {
            pbSignIn.setVisibility(ProgressBar.VISIBLE);

            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

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

            Account account = new Account(email, password);

            authenticator.signIn(account, task -> {
                pbSignIn.setVisibility(ProgressBar.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                    String userId = authenticator.getCurrentUserId();
                    employeeViewModel.getById(userId).thenAccept(employee -> {
                        if (employee != null) {
                            Intent i;
                            if (employee.IsAdmin()) {
                                i = new Intent(this, GeneralManagerActivity.class);
                            } else {
                                i = new Intent(this, GeneralEmployeeActivity.class);
                            }

                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed to retrieve employee.", Toast.LENGTH_SHORT).show();
                        }
                    }).exceptionally(ex -> {
                        Toast.makeText(LoginActivity.this, "Error retrieving employee: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                        return null;
                    });
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

    private void testInventoryViewModel() {
        employeeInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new EmployeeSerializer())));
        employeeViewModel = InventoryViewModelFactory.getInstance().getViewModel(employeeInventory, Employee.class);

        // Test getAll
        employeeViewModel.getAll().thenAccept(employees -> {
            for (var employee : employees) {
                Log.d(TAG, "Employee: " + employee);
            }
        });

        // Test add
        Employee newEmployee = new Employee("id123", "john.doe@example.com", "John Doe", "2023-10-01", false);
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
        Employee employeeToDelete = new Employee("id123", "john.doe@example.com", "John Doe", "2023-10-01", false);
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
        Employee currentEmployee = new Employee("id123", "john.doe@example.com", "John Doe", "2023-10-01", false);
        Employee updatedEmployee = new Employee("id123", "john.doe@example.com", "John Doe", "2023-10-01", true);
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