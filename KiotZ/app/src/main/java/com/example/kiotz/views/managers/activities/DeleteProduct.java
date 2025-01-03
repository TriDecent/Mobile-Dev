package com.example.kiotz.views.managers.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

public class DeleteProduct extends AppCompatActivity implements IRecycleManagerDetail {
    RecyclerView recyclerViewProduct;
    ProductsAdapterManager adapterManager;
    List<Product> products;
    ViewGroup progressView;

    TextView tvUserName,tvPosition;
    boolean isProgressShowing = false;
    InventoryViewModel<Product> productViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_product);
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
                .thenRun(this::setupAdapter);
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onItemLongClick(int position) {
//        TODO: show dialog before delete
        AlertDialog alertDialog = new AlertDialog.Builder(DeleteProduct.this).create();
        alertDialog.setTitle("Comfirm delete");
        alertDialog.setMessage("Are you sure you want to delete this product?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        showProgressingView();
                        productViewModel.delete(products.get(position)).thenRun(DeleteProduct.this::hideProgressingView);
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();

    }

    private void bindingView(){
        recyclerViewProduct=findViewById(R.id.recycleView_rv);
        tvUserName=findViewById(R.id.tv_username);
        tvPosition=findViewById(R.id.tv_position);
        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(this));
    }


    private void setupProductViewModel(){
        var productInventory=new Inventory<>(new Repository<>(new FireBaseService<>(new ProductSerializer())));
        productViewModel= InventoryViewModelFactory.getInstance().getViewModel(productInventory, Product.class);

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