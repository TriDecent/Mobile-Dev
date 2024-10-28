package com.example.kiotz;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.kiotz.database.MockDataBaseService;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.Product;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "InventoryViewModelTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edittext_material_ui);

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


        productViewModel.getTotalItemsLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer totalItems) {
                // Handle UI here
                Log.d(TAG, "Total Items: " + totalItems);
            }
        });

        // Observe totalQuantity LiveData
        productViewModel.getTotalQuantityLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer totalQuantity) {
                // Handle UI here
                Log.d(TAG, "Total Quantity: " + totalQuantity);
            }
        });


        try {
            // Test getTotalItemsAsync
            productViewModel.getTotalItemsAsync().thenAccept(totalItems -> Log.d(TAG, "Total Items: " + totalItems)).get();

            // Test getTotalQuantity
            productViewModel.getTotalQuantity().thenAccept(totalQuantity -> Log.d(TAG, "Total Quantity: " + totalQuantity)).get();

            // Test addItem
            Product newProduct = new Product(11, "Speaker", "Electronics", 59.99, "Piece", "/path/to/qr11");
            productViewModel.addItem(newProduct).thenAccept(v -> Log.d(TAG, "Added new product")).get();

            // Test removeItem
            productViewModel.removeItem(1).thenAccept(v -> Log.d(TAG, "Removed product with ID 1")).get();

            // Test getItem
            productViewModel.getItem(2).thenAccept(item -> Log.d(TAG, "Retrieved item: " + item.getID())).get();

            // Test getAllItems
            productViewModel.getAllItems().thenAccept(items -> Log.d(TAG, "All items count: " + items.size())).get();

            // Test getQuantity
            productViewModel.getQuantity(newProduct).thenAccept(quantity -> Log.d(TAG, "Quantity of new product: " + quantity)).get();

            // Test setQuantity
            productViewModel.setQuantity(newProduct, 50).thenAccept(v -> Log.d(TAG, "Set quantity of new product to 50")).get();

            // Test updateItemQuantity
            productViewModel.updateItemQuantity(newProduct, 60).thenAccept(v -> Log.d(TAG, "Updated quantity of new product to 60")).get();

            // Test incrementQuantity
            productViewModel.incrementQuantity(newProduct, 10).thenAccept(v -> Log.d(TAG, "Incremented quantity of new product by 10")).get();

            // Test decrementQuantity
            productViewModel.decrementQuantity(newProduct, 5).thenAccept(v -> Log.d(TAG, "Decremented quantity of new product by 5")).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Error executing async task", e);
        }
    }
}