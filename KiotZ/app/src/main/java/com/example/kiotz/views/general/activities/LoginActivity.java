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
import com.example.kiotz.models.Account;
import com.example.kiotz.views.employees.activities.GeneralEmployeeActivity;
import com.example.kiotz.views.managers.activities.GeneralManagerActivity;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    TextView tvRegister;

    TextInputEditText etEmail;
    TextInputEditText etPassword;

    ProgressBar pbSignIn;
    Authenticator authenticator;

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

            Account account = new Account(email, password, null);

            authenticator.signIn(account, task -> {
                pbSignIn.setVisibility(ProgressBar.GONE);
                if (task.isSuccessful()) {
                    String userId = authenticator.getCurrentUserId();
                    Account loggedInAccount = new Account(email, password, userId);

                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                    if (email.equals("manager@gmail.com") && password.equals("123456")) {
                        Intent i = new Intent(v.getContext(), GeneralManagerActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Intent i = new Intent(v.getContext(), GeneralEmployeeActivity.class);
                        startActivity(i);
                        finish();
                    }
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
}