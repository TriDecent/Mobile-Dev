package com.example.kiotz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    Button buttonLogin;
    TextView tvRegister;

    TextInputLayout textInputLayoutUsername;
    TextInputLayout textInputLayoutPassword;

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


        buttonLogin=findViewById(R.id.buttonLogin);
        tvRegister=findViewById(R.id.tvRegister);

        textInputLayoutUsername=findViewById(R.id.login_username_ti);
        textInputLayoutPassword=findViewById(R.id.login_password_ti);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i=new Intent(v.getContext(), GeneralManagerActivity.class);
//                startActivity(i);
//                finish();
                String username=textInputLayoutUsername.getEditText().getText().toString();
                String password=textInputLayoutPassword.getEditText().getText().toString();
                if(!TextUtils.isEmpty(username)&&!TextUtils.isEmpty(password)){
                    if(username.equals("manager") && password.equals("manager")){
                        Intent i=new Intent(v.getContext(), GeneralManagerActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else if(username.equals("employee") && password.equals("employee")){
                        Intent i=new Intent(v.getContext(), GeneralEmployeeActivity.class);
                        startActivity(i);
                        finish();
                    }
                }


            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(v.getContext(), RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
}