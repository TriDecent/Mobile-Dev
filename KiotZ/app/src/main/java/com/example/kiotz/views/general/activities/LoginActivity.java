package com.example.kiotz.views.general.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.kiotz.database.MockDataBaseService;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.Product;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.views.employees.activities.GeneralEmployeeActivity;
import com.example.kiotz.views.managers.activities.GeneralManagerActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    TextView tvRegister;

    TextInputEditText etEmail;
    TextInputEditText etPassword;

    ProgressBar pbSignIn;
    FirebaseAuth mAuth;

    private static final String TAG = "InventoryViewModelTest";

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

        List<Product> products = Arrays.asList(
                new Product(1, "Laptop", "Electronics", 999.99, "Piece", "/path/to/qr1"),
                new Product(2, "Smartphone", "Electronics", 499.99, "Piece", "/path/to/qr2"),
                new Product(3, "Tablet", "Electronics", 299.99, "Piece", "/path/to/qr3"),
                new Product(4, "Headphones", "Electronics", 199.99, "Piece", "/path/to/qr4"),
                new Product(5, "Smartwatch", "Electronics", 149.99, "Piece", "/path/to/qr5"),
                new Product(6, "Camera", "Electronics", 799.99, "Piece", "/path/to/qr6"),
                new Product(7, "Printer", "Electronics", 129.99, "Piece", "/path/to/qr7"),
                new Product(8, "Monitor", "Electronics", 199.99, "Piece", "/path/to/qr8"),
                new Product(9, "Keyboard", "Electronics", 49.99, "Piece", "/path/to/qr9"),
                new Product(10, "Mouse", "Electronics", 29.99, "Piece", "/path/to/qr10")
        );

        MockDataBaseService<Product> mockDataBaseService = new MockDataBaseService<>(products);
        Repository<Product> repository = new Repository<>(mockDataBaseService);
        Inventory<Product> productInventory = new Inventory<>(repository);
        InventoryViewModel<Product> productViewModel = new InventoryViewModel<>(productInventory);

        // Add products to the database using productViewModel
        for (Product product : products) {
            productViewModel.add(product);
        }

        // Observe and log added items
        productViewModel.getObservableAddedItem().observe(this, addedItem -> Log.d(TAG, "Added Product: " + addedItem));

        // Observe and log all items
        productViewModel.getObservableItems().observe(this, allItems -> {
            for (Product product : allItems) {
                Log.d(TAG, "Product from ViewModel: " + product);
            }
        });

        // Update a product using productViewModel
        Product updatedProduct = new Product(1, "Updated Laptop", "Electronics", 1099.99, "Piece", "/path/to/qr1");
        productViewModel.update(products.get(0), updatedProduct);

        // Observe and log updated items
        productViewModel.getObservableUpdatedItemPosition().observe(this, position -> Log.d(TAG, "Updated Product at position: " + position));

        // Delete a product using productViewModel
        productViewModel.delete(updatedProduct);

        // Observe and log deleted items
        productViewModel.getObservableDeletedItemPosition().observe(this, position -> Log.d(TAG, "Deleted Product at position: " + position));

        btnLogin = findViewById(R.id.buttonLogin);
        tvRegister = findViewById(R.id.tv_sign_in);

        etEmail = findViewById(R.id.et_sign_in_email);
        etPassword = findViewById(R.id.et_sign_in_password);

        pbSignIn = findViewById(R.id.pb_sign_in);
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> {
            pbSignIn.setVisibility(ProgressBar.VISIBLE);

            var email = etEmail.getText().toString();
            var password = etPassword.getText().toString();

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email is required");
                pbSignIn.setVisibility(ProgressBar.GONE);
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Password is required");
                pbSignIn.setVisibility(ProgressBar.GONE);
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        pbSignIn.setVisibility(ProgressBar.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                            if (email.equals("manager") && password.equals("manager")) {
                                Intent i = new Intent(v.getContext(), GeneralManagerActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Intent i = new Intent(v.getContext(), GeneralEmployeeActivity.class);
                                startActivity(i);
                                finish();
                            }
                        } else {

                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            pbSignIn.setVisibility(ProgressBar.GONE);

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