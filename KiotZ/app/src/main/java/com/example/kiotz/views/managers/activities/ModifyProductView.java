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
import com.example.kiotz.models.Product;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;
import com.example.kiotz.views.managers.data.App;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModifyProductView extends AppCompatActivity implements IRecycleManagerDetail {
    ArrayList<Product> productArrayList;
    TextView tv_username, tv_position;
    RecyclerView recycleView_rv;
    InventoryViewModel<Product> productViewModel;
    ProductsAdapterManager productsAdapterManager;
    List<Product> temp_copy_list;

    SearchView search_view_inventory_sv;
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
                .thenRun(this::setupRecyclerView)
                .thenRun(this::copyList)
                .thenRun(this::setupObservers)
                .thenRun(this::setupSearchView);
    }

    private void initVariables()
    {
        recycleView_rv = findViewById(R.id.recycleView_rv);
        tv_username = findViewById(R.id.tv_username);
        tv_position = findViewById(R.id.tv_position);
        search_view_inventory_sv = findViewById(R.id.search_view_inventory_sv);
    }

    @Override
    public void onItemClick(int position) {

        Intent intent = new Intent(this, ModifyProductEdit.class);
        intent.putExtra(ModifyProductEdit.MODIFY_PRODUCT_INTENT_KEY, productArrayList.get(position).ID());
        startActivity(intent);

//        revert back to the original fetched list
        search_view_inventory_sv.setQuery("",true);
        search_view_inventory_sv.clearFocus();
    }

    @Override
    public void onItemLongClick(int position) {

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
        productsAdapterManager = new ProductsAdapterManager(this,productArrayList, this);
        recycleView_rv.setAdapter(productsAdapterManager);
    }

    private void setupObservers() {
        productViewModel.getObservableAddedItem().observe(this, addedProduct -> {
            if (productArrayList.stream().anyMatch(e -> e.ID().equals(addedProduct.ID()))) {
                return;
            }

            productArrayList.add(addedProduct);
            temp_copy_list.add(addedProduct);
            productsAdapterManager.notifyItemInserted(productArrayList.size() - 1);
        });

        productViewModel.getObservableDeletedItem().observe(this, pair -> {
            int position = pair.first;
            var deletedProduct = pair.second;

            if (productArrayList.stream().noneMatch(e -> e.ID().equals(deletedProduct.ID()))) {
                return;
            }

            productArrayList.remove(position);
            temp_copy_list.remove(position);
            productsAdapterManager.notifyItemRemoved(position);
        });

        productViewModel.getObservableUpdatedItem().observe(this, pair -> {
            var position = pair.first;
            var updatedProduct = pair.second;

            var employeeNeedsToBeUpdated = productArrayList.get(position);

            if (employeeNeedsToBeUpdated.equals(updatedProduct)) {
                return;
            }

            productArrayList.set(position, updatedProduct);
            temp_copy_list.set(position,updatedProduct);
            productsAdapterManager.notifyItemChanged(position);
        });
    }

    private  void setupSearchView()
    {

        search_view_inventory_sv.clearFocus();
        search_view_inventory_sv.setIconified(false);
//        search_view_inventory_sv.requestFocus();
        search_view_inventory_sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
//                return original data
                productArrayList.clear();
                productArrayList.addAll(temp_copy_list);
                productsAdapterManager.notifyDataSetChanged();
                search_view_inventory_sv.clearFocus();

                return true;
            }

            private void search_filter_text(String text)
            {

                ArrayList<Product> filtered_list = new ArrayList<Product>();
                for (Product i : temp_copy_list) {
                    if (i.ID().toLowerCase().contains(text.toLowerCase()) ||
                            i.Name().toLowerCase().contains(text.toLowerCase()) ||
                            String.valueOf(i.Price()).contains(text.toLowerCase()) ||
                            String.valueOf(i.Price()).contains(text.toLowerCase())
                    )
                    {
                        filtered_list.add(i);
                    }
                }

                productArrayList.clear();
                productArrayList.addAll(filtered_list);
                productsAdapterManager.notifyDataSetChanged();


            }

            @Override
            public boolean onQueryTextChange(String s) {
                search_filter_text(s);
                return true;
            }
        });


    }

    private void copyList()
    {
        temp_copy_list = new ArrayList<>();
        temp_copy_list.addAll(productArrayList);
    }
}