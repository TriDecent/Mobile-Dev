package com.example.kiotz.views.general.activities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.android.material.imageview.ShapeableImageView;

import java.time.LocalDateTime;

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
    ShapeableImageView imageView;
    Uri local_image_uri;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
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
        registerPhotoPicker();
        setupOnClickListener();
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
        imageView = findViewById(R.id.product_img_siv);
    }

    private void registerPhotoPicker()
    {
        // Registers a photo picker activity launcher in single-select mode.
        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
//                        TODO: upload image to db
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        local_image_uri = uri;
                        imageView.setImageURI(local_image_uri);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                        imageView.setImageURI(null);
                        local_image_uri = null;
                    }
                });

    }

    private void setupOnClickListener()
    {
        upload_img_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());

            }
        });

        discard_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discardInformation();
            }
        });

        complete_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubmitProduct();
            }
        });
    }

    private void discardInformation()
    {
        id_et.setText("");
        name_et.setText("");
        price_et.setText("");
        quantity_et.setText("");
        unit_et.setText("");
        category_et.setText("");
        local_image_uri = null;
        imageView.setImageURI(null);
    }

    private boolean checkProductAlreadyExists()
    {
        return false;
    }

    private boolean checkCleanInput()
    {
        if (id_et.getText().length() == 0 || name_et.getText().length() == 0 || price_et.getText().length() == 0 ||
                quantity_et.getText().length() == 0 || unit_et.getText().length() == 0 || category_et.getText().length() == 0 ||
                local_image_uri == null ||
                containsNumber(String.valueOf(name_et.getText()))  ||
                containsNumber(String.valueOf(unit_et.getText())) || containsNumber(String.valueOf(category_et.getText()))
        )
            return false;
        return true;
    }

    private void SubmitProduct()
    {
        if (!checkCleanInput())
        {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            return;
        }
        LocalDateTime localDateTime = LocalDateTime.now();
        //upload image
        productViewModel.uploadImage(local_image_uri, String.valueOf(name_et.getText()) + localDateTime.toString()).thenAccept(remote_uri -> {
            Product product = new Product(String.valueOf(id_et.getText()),String.valueOf(name_et.getText()),
                    String.valueOf(category_et.getText()),Double.parseDouble(String.valueOf(price_et.getText())),
                    String.valueOf(unit_et.getText()), Integer.valueOf(String.valueOf(quantity_et.getText())),
                    remote_uri);
                    productViewModel.add(product);
                    Log.d("SubmitProduct", "SubmitProduct: " + product.toString());
                    Log.d("RemoteUri",remote_uri);
                    discardInformation();
        });

        Toast.makeText(this, "Product added", Toast.LENGTH_SHORT).show();
    }

    private boolean containsNumber(String s)
    {
        return s.matches(".*\\d.*");
    }
}