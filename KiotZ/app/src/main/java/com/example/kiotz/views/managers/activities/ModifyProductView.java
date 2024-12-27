package com.example.kiotz.views.managers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiotz.R;
import com.example.kiotz.adapters.IRecycleManagerDetail;
import com.example.kiotz.adapters.ProductsAdapterManager;
import com.example.kiotz.database.FireBaseService;
import com.example.kiotz.database.dto.ProductSerializer;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.DetailProduct;
import com.example.kiotz.models.Product;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;
import com.example.kiotz.views.managers.data.App;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class ModifyProductView extends AppCompatActivity implements IRecycleManagerDetail {
    ArrayList<Product> productArrayList;
    SearchView search;
    TextView tv_username, tv_position;
    RecyclerView recycleView_rv;
    InventoryViewModel<Product> productViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modify_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initVariables();
        setupViewModel();
        setupStatusBar();
        loadProduct()
                .thenRun(this::setupRecyclerView);
    }

    private void initVariables()
    {
        search = findViewById(R.id.search);
        recycleView_rv = findViewById(R.id.recycleView_rv);
        tv_username = findViewById(R.id.tv_username);
        tv_position = findViewById(R.id.tv_position);
    }

    @Override
    public void onItemClick(int position) {

        Intent intent = new Intent(this, ModifyProductEdit.class);
        intent.putExtra(ModifyProductEdit.MODIFY_PRODUCT_INTENT_KEY, productArrayList.get(position).ID());
        startActivity(intent);
    }

    private void setupViewModel() {
        var employeeInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new ProductSerializer())));
        productViewModel = InventoryViewModelFactory.getInstance().getViewModel(employeeInventory, Product.class);
    }

    private void setupStatusBar(){
        App app=(App) getApplication();
        tv_username.setText(app.getName());
        tv_position.setText(app.getPosition());
    }

    private CompletableFuture<Void> loadProduct() {
        return productViewModel.getAll().thenAccept(fetched_product ->
                runOnUiThread(() -> productArrayList = new ArrayList<>(fetched_product)));
    }

    private void setupRecyclerView()
    {
        recycleView_rv.setLayoutManager(new LinearLayoutManager(this));
        ProductsAdapterManager productsAdapterManager = new ProductsAdapterManager(this,productArrayList, this);
        recycleView_rv.setAdapter(productsAdapterManager);
    }
}