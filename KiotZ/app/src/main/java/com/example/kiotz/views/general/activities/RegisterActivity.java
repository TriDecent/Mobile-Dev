package com.example.kiotz.views.general.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.Employee;
import com.example.kiotz.models.UserCredentials;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    TextView tvSignIn;
    Button btnRegister;

    TextInputEditText tietEmail;
    TextInputEditText tietPassword;
    TextInputEditText tietName;

    ProgressBar pbSignUp;
    Authenticator authenticator;

    Inventory<Employee> employeeInventory;
    InventoryViewModel<Employee> employeeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvSignIn = findViewById(R.id.tv_sign_in);
        btnRegister = findViewById(R.id.btn_register);
        tietEmail = findViewById(R.id.et_sign_up_email);
        tietPassword = findViewById(R.id.et_sign_up_password);
        tietName = findViewById(R.id.et_sign_up_username);

        pbSignUp = findViewById(R.id.pb_sign_up);
        authenticator = Authenticator.getInstance();

        employeeInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new EmployeeSerializer())));
        employeeViewModel = InventoryViewModelFactory.getInstance().getViewModel(employeeInventory, Employee.class);

        tvSignIn.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), LoginActivity.class);
            startActivity(i);
            finish();
        });

        btnRegister.setOnClickListener(v -> {
            pbSignUp.setVisibility(ProgressBar.VISIBLE);

            String email = Objects.requireNonNull(tietEmail.getText()).toString();
            String password = Objects.requireNonNull(tietPassword.getText()).toString();
            String name = Objects.requireNonNull(tietName.getText()).toString();

            if (TextUtils.isEmpty(email)) {
                tietEmail.setError("Email is required");
                pbSignUp.setVisibility(ProgressBar.GONE);
                return;
            }

            if (TextUtils.isEmpty(password)) {
                tietPassword.setError("Password is required");
                pbSignUp.setVisibility(ProgressBar.GONE);
                return;
            }

            UserCredentials userCredentials = new UserCredentials(email, password);

            authenticator.register(userCredentials, task -> {
                pbSignUp.setVisibility(ProgressBar.GONE);
                if (task.isSuccessful()) {
                    var employeeId = Objects.requireNonNull(task.getResult().getUser()).getUid();
                    var newEmployee = new Employee(employeeId, email, name, null, null, false);
                    employeeViewModel
                            .add(newEmployee)
                            .thenRun(() ->
                                    Toast.makeText(this, "Account created.", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(this, "Account creation failed. Please try again. " +
                            "Your email may already be in use.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}