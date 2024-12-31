package com.example.kiotz.views.managers.activities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.kiotz.R;
import com.example.kiotz.database.FireBaseService;
import com.example.kiotz.database.dto.ProductSerializer;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.Product;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;
import com.example.kiotz.views.managers.data.App;
import com.google.android.material.imageview.ShapeableImageView;

import java.time.LocalDateTime;
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
    private boolean isProgressShowing = false;
    ShapeableImageView imageView;
    TextView tv_username, tv_position;
    String product_to_edit_ID;
    Uri local_image_uri;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    public final static String MODIFY_PRODUCT_INTENT_KEY = "MODIFY_PRODUCT_ITEM_ID";
    Product productToEdit;
    ViewGroup progressView;
    boolean image_has_changed = false;
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
        showProgressingView();
        initVariables();
        setupViewModel();
        setupStatusBar();
        PermissionCheck();
        loadProductID();
        registerPhotoPicker();
        loadProduct().thenRun(this::setupOnClickListener)
                .thenRun(this::adaptDataToViews)
                .thenRun(this::hideProgressingView);
    }

    private void setupViewModel() {
        var employeeInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new ProductSerializer())));
        productViewModel = InventoryViewModelFactory.getInstance().getViewModel(employeeInventory, Product.class);
    }


    private void PermissionCheck()
    {
        App app=(App) getApplication();
        if(!app.getPosition().equals("Manager"))
        {
            id_et.setEnabled(false);
            name_et.setEnabled(false);
            price_et.setEnabled(false);
            unit_et.setEnabled(false);
            category_et.setEnabled(false);
            upload_img_bt.setEnabled(false);
        }
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
        tv_username = findViewById(R.id.tv_username);
        tv_position = findViewById(R.id.tv_position);
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
                        image_has_changed = true;
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
//        id_et.setText("");
//        name_et.setText("");
//        price_et.setText("");
//        quantity_et.setText("");
//        unit_et.setText("");
//        category_et.setText("");
//        local_image_uri = null;
//        imageView.setImageURI(null);

//        restore original data
        adaptDataToViews();
    }

    private void loadProductID()
    {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        product_to_edit_ID = bundle.getString(MODIFY_PRODUCT_INTENT_KEY);
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

    private boolean checkIfAnyValueHasChanged()
    {
        return !(String.valueOf(id_et.getText()).equals(productToEdit.ID()) &&
                String.valueOf(name_et.getText()).equals(productToEdit.Name()) &&
                String.valueOf(price_et.getText()).equals(String.valueOf(productToEdit.Price())) &&
                String.valueOf(quantity_et.getText()).equals(String.valueOf(productToEdit.Quantity())) &&
                String.valueOf(unit_et.getText()).equals(productToEdit.Unit()) &&
                String.valueOf(category_et.getText()).equals(productToEdit.Category()) &&
                !image_has_changed);
    }

    private void SubmitProduct() {
        if (!checkCleanInput()) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!checkIfAnyValueHasChanged())
        {
            Toast.makeText(this, "Nothing changed", Toast.LENGTH_SHORT).show();
            return;
        }




        showProgressingView();
        LocalDateTime localDateTime = LocalDateTime.now();

        if (image_has_changed)
        productViewModel.uploadImage(local_image_uri, String.valueOf(name_et.getText()) + localDateTime.toString())
                        .thenAccept(remote_uri -> {
                            UpdateProduct(remote_uri);
                        });

        else
            UpdateProduct(String.valueOf(local_image_uri));
    }

    private void UpdateProduct(String img_uro)
    {
        String productId = id_et.getText().toString();
        Product product = new Product(
                productId,
                name_et.getText().toString(),
                category_et.getText().toString(),
                Double.parseDouble(price_et.getText().toString()),
                unit_et.getText().toString(),
                Integer.parseInt(quantity_et.getText().toString()),
                img_uro
        );
        productViewModel.update(productToEdit,product).thenRun(() -> {
//                                Log.d("SubmitProduct", "SubmitProduct: " + product.toString());
            runOnUiThread(() -> Toast.makeText(this, "Product edited", Toast.LENGTH_SHORT).show());
//                                discardInformation();
            hideProgressingView();
            finish();
        });
    }

    private boolean containsNumber(String s)
    {
        return s.matches(".*\\d.*");
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
        local_image_uri = Uri.parse(productToEdit.imageURL());
//        imageView.setImageURI(image_uri);

        Glide.with(this)
                .load(local_image_uri)
                .into(imageView);

    }

    private void setupStatusBar(){
        App app=(App) getApplication();
        tv_username.setText(app.getName());
        tv_position.setText(app.getPosition());
    }

    CompletableFuture<Void> loadProduct()
    {
        return productViewModel.getById(product_to_edit_ID).thenAccept(product -> productToEdit = product);
    }

    public void showProgressingView() {

        if (!isProgressShowing) {
            isProgressShowing = true;
            progressView = (ViewGroup) getLayoutInflater().inflate(R.layout.progress_bar, null);
            View v = this.findViewById(android.R.id.content).getRootView();
            ViewGroup viewGroup = (ViewGroup) v;
            viewGroup.addView(progressView);
        }
    }

    public void hideProgressingView() {
        View v = this.findViewById(android.R.id.content).getRootView();
        ViewGroup viewGroup = (ViewGroup) v;
        viewGroup.removeView(progressView);
        isProgressShowing = false;
    }

}