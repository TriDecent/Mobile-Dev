package com.example.kiotz.views.general.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kiotz.R;
import com.example.kiotz.database.FireBaseService;
import com.example.kiotz.database.dto.ProductSerializer;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.Product;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;

public class CreateProductActivity extends AppCompatActivity {
    private InventoryViewModel<Product> productViewModel;
    private EditText id_et;
    private  EditText name_et;
    private  EditText price_et;
    private  EditText quantity_et;
    private  EditText unit_et;
    private  EditText category_et;
    private Button upload_img_bt;
    private Button discard_bt;
    private Button complete_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initVariables();
        setupViewModel();
    }

    private void setupViewModel() {
        var employeeInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new ProductSerializer())));
        productViewModel = InventoryViewModelFactory.getInstance().getViewModel(employeeInventory, Product.class);
    }

    private  void initVariables()
    {
        id_et = findViewById(R.id.editTextID);
        name_et = findViewById(R.id.editTextName);
        price_et = findViewById(R.id.editTextSellingPrice);
        quantity_et = findViewById(R.id.editTextQuantily);
        unit_et = findViewById(R.id.editUnit);
        category_et = findViewById(R.id.editTextCategory);
        upload_img_bt = findViewById(R.id.buttonUploadImg);
        discard_bt = findViewById(R.id.buttonDiscard);
        complete_bt = findViewById(R.id.buttonComplete);
    }

    private void setupOnClickListener()
    {
        upload_img_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        discard_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id_et.setText("");
                name_et.setText("");
                price_et.setText("");
                quantity_et.setText("");
                unit_et.setText("");
                category_et.setText("");
            }
        });

        complete_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}