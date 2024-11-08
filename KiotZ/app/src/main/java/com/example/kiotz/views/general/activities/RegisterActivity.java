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
import com.example.kiotz.models.Account;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    TextView tvSignIn;
    Button btnRegister;

    TextInputEditText etEmail;
    TextInputEditText etPassword;

    ProgressBar pbSignUp;
    Authenticator authenticator;

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
        etEmail = findViewById(R.id.et_sign_up_email);
        etPassword = findViewById(R.id.et_sign_up_password);

        pbSignUp = findViewById(R.id.pb_sign_up);
        authenticator = new Authenticator();

        tvSignIn.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), LoginActivity.class);
            startActivity(i);
            finish();
        });

        btnRegister.setOnClickListener(v -> {
            pbSignUp.setVisibility(ProgressBar.VISIBLE);

            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email is required");
                pbSignUp.setVisibility(ProgressBar.GONE);
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Password is required");
                pbSignUp.setVisibility(ProgressBar.GONE);
                return;
            }

            Account account = new Account(email, password, null);

            authenticator.register(account, task -> {
                pbSignUp.setVisibility(ProgressBar.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Account created.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}