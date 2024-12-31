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
import com.example.kiotz.models.Receipt;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;
import com.example.kiotz.views.managers.data.App;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public  class ViewInventoryActivity extends AppCompatActivity implements IRecycleManagerDetail {

    RecyclerView recyclerViewProduct;
    ProductsAdapterManager adapterManager;
    List<Product> products;
    List<Product> temp_copy_list;
    TextView tvUserName,tvPosition;
    SearchView search_view_inventory_sv;
    InventoryViewModel<Product> productViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_inventory);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        bindingView();
        setupProductViewModel();
        setupStatusBar();
        fetchDataProduct()
                .thenRun(this::setupObservers)
                .thenRun(this::setupAdapter)
                .thenRun(this::copyList)
                .thenRun(this::setupSearchView);


    }

    private void copyList()
    {
        temp_copy_list = new ArrayList<>();
        temp_copy_list.addAll(products);
    }

    private void bindingView(){
        recyclerViewProduct=findViewById(R.id.recycleView_rv);
        tvUserName=findViewById(R.id.tv_username);
        tvPosition=findViewById(R.id.tv_position);
        search_view_inventory_sv = findViewById(R.id.search_view_inventory_sv);
        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(this));
    }


    private void setupProductViewModel(){
        var productInventory=new Inventory<>(new Repository<>(new FireBaseService<>(new ProductSerializer())));
        productViewModel= InventoryViewModelFactory.getInstance().getViewModel(productInventory,Product.class);

    }

    private void setupObservers(){
        productViewModel.getObservableAddedItem().observe(this,addedProduct->{
            if(products.stream().anyMatch(p ->p.ID().equals(addedProduct.ID()))){
                return;
            }
            products.add(addedProduct);
            adapterManager.notifyItemChanged(products.size()-1);
        });

        productViewModel.getObservableDeletedItem().observe(this,pair->{
            int position=pair.first;
            var deletedProduct=pair.second;

            if(products.stream().noneMatch(p-> p.ID().equals(deletedProduct.ID()))){
                return;
            }

            products.remove(position);
            adapterManager.notifyItemRemoved(position);

        });

        productViewModel.getObservableUpdatedItem().observe(this,pair->{
            var position=pair.first;
            var updatedProduct=pair.second;

            var productNeedsToBeUpdated=products.get(position);
            if(productNeedsToBeUpdated.equals(updatedProduct)){
                return;
            }
            products.set(position,updatedProduct);
            adapterManager.notifyItemChanged(position);
        });
    }

    private void setupAdapter(){
        adapterManager=new ProductsAdapterManager(this,products,this);
        recyclerViewProduct.setAdapter(adapterManager);
    }

    private CompletableFuture<Void> fetchDataProduct(){
        return productViewModel.getAll().thenAccept(data->{
            runOnUiThread(()->products=new ArrayList<>(data));
        });
    }

    private void setupStatusBar() {
        App app = (App) getApplication();
        tvUserName.setText(app.getName());
        tvPosition.setText(app.getPosition());
    }
    @Override
    public void onItemClick(int position) {
        Product selectedProduct=products.get(position);
        Intent intent = new Intent(this, DetailProductActivity.class);
        intent.putExtra("selected_product", selectedProduct);
        startActivity(intent);


    }

    @Override
    public void onItemLongClick(int position) {

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
                products.clear();
                products.addAll(temp_copy_list);
                adapterManager.notifyDataSetChanged();
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

                products.clear();
                products.addAll(filtered_list);
                adapterManager.notifyDataSetChanged();


            }

            @Override
            public boolean onQueryTextChange(String s) {
                search_filter_text(s);
                return true;
            }
        });


    }

}

