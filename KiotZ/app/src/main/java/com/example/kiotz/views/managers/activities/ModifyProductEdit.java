package com.example.kiotz.views.managers.activities;

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

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class ModifyProductEdit extends AppCompatActivity {
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
    Uri image_uri;
    ArrayList<Product> productArrayList;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    public final static String MODIFY_PRODUCT_INTENT_KEY = "MODIFY_PRODUCT_ITEM_ID";
    Product productToEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modify_product_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initVariables();
        setupViewModel();
        loadProductList().thenRun(this::setupOnClickListener)
                .thenRun(this::loadProductToEdit)
                .thenRun(this::adaptDataToViews)
                .thenRun(this::registerPhotoPicker);

    }

    private void setupViewModel() {
        var employeeInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new ProductSerializer())));
        productViewModel = InventoryViewModelFactory.getInstance().getViewModel(employeeInventory, Product.class);
    }

    private CompletableFuture<Void> loadProductList() {
        return productViewModel.getAll().thenAccept(fetchedProduct ->
                runOnUiThread(() -> productArrayList = new ArrayList<>(fetchedProduct)));
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
                        image_uri = uri;
                        imageView.setImageURI(image_uri);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                        imageView.setImageURI(null);
                        image_uri = null;
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
        image_uri = null;
        imageView.setImageURI(null);
    }

    private boolean checkProductAlreadyExists()
    {
//        TODO: check if product already exists
        return false;
    }

    private boolean checkCleanInput()
    {
        if (id_et.getText().length() == 0 || name_et.getText().length() == 0 || price_et.getText().length() == 0 ||
                quantity_et.getText().length() == 0 || unit_et.getText().length() == 0 || category_et.getText().length() == 0 ||
                image_uri == null ||
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

        Product product = new Product(String.valueOf(id_et.getText()),String.valueOf(name_et.getText()),
                String.valueOf(category_et.getText()),Double.parseDouble(String.valueOf(price_et.getText())),
                String.valueOf(unit_et.getText()), Integer.valueOf(String.valueOf(quantity_et.getText())),
                "");
//        TODO: update imageURL to real uri
        Log.d("SubmitProduct", "SubmitProduct: " + product.toString());
        productViewModel.update(productToEdit, product);
        discardInformation();
        Toast.makeText(this, "Product edited", Toast.LENGTH_SHORT).show();
    }

    private boolean containsNumber(String s)
    {
        return s.matches(".*\\d.*");
    }

    private void loadProductToEdit()
    {
        String product_to_edit_id;
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        product_to_edit_id = bundle.getString(MODIFY_PRODUCT_INTENT_KEY);

        for (Product i: productArrayList) {
            if (i.ID().equals(product_to_edit_id))
            {
                productToEdit = i;
                return;
            }
        }
    }

    private void adaptDataToViews()
    {
        if (productToEdit == null)
            return;
        id_et.setText(productToEdit.ID());
        name_et.setText(productToEdit.Name());
        price_et.setText(String.valueOf(productToEdit.Price()));
        quantity_et.setText(String.valueOf(productToEdit.Quantity()));
        unit_et.setText(String.valueOf(productToEdit.Unit()));
        category_et.setText(productToEdit.Category());
        image_uri = Uri.parse(productToEdit.imageURL());
        imageView.setImageURI(image_uri);
    }
}