package com.example.kiotz;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiotz.adapters.IRecycleManagerDetail;
import com.example.kiotz.adapters.ProductsAdapterManager;
import com.example.kiotz.database.FireBaseService;
import com.example.kiotz.database.dto.ProductSerializer;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.CustomProduct;
import com.example.kiotz.models.DetailProduct;
import com.example.kiotz.models.IIdentifiable;
import com.example.kiotz.models.Product;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ViewInventoryActivity extends AppCompatActivity implements IRecycleManagerDetail {

    RecyclerView recyclerViewProduct;
    ProductsAdapterManager adapterManager;
    List<Product> products;

    InventoryViewModel<Product> productViewModel;

    //ArrayList<DetailProduct> dataDetail;


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

        //createDataDetailTest();

        bindingView();
        setupProductViewModel();
        fetchDataProduct()
                .thenRun(this::setupObservers)
                .thenRun(this::setupAdapter);



    }



    private void bindingView(){
        recyclerViewProduct=findViewById(R.id.recycleViewProductManager);
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

//    private void test(){
//        products=new ArrayList<>();
//        products.add(new Product("MH001","Hao Hao noodles","Food",2500,"package",3000,"/path/to/img1"));
//        products.add(new Product("MH002","Coca Cola","Beverage",9000,"bottle",5000,"/path/to/img2"));
//        products.add(new Product("MH003","Pepsi","Beverage",9000,"bottle",10000,"/path/to/img3"));
//        products.add(new Product("MH004","Milo","Milk",8000,"bottle",10000,"/path/to/img4"));
//
//
//
//    }

    @Override
    public void onItemClick(int position) {
//       Intent intent=new Intent(ViewInventoryActivity.this,DetailProductActivity.class);
//       DetailProduct detailProduct=dataDetail.get(position);
//       intent.putExtra("data",detailProduct);
//       startActivity(intent);

    }

//    private void createDataDetailTest(){
//        dataDetail=new ArrayList<>();
//        dataDetail.add(new DetailProduct("MH001","Hao Hao noodles",2500,3000,"Pack","Food",300,120,180,360000,-390000,R.drawable.img_test1,R.drawable.barcode));
//        dataDetail.add(new DetailProduct("NG001","Coca cola",9000,10000,"Bottle","Beverage",200,190,10,1900000,100000,R.drawable.img_test2,R.drawable.barcode));
//        dataDetail.add(new DetailProduct("NG002","Pepsi",9500,10000,"Bottle","Beverage",100,60,40,600000,-350000,R.drawable.img_test3,R.drawable.barcode));
//        dataDetail.add(new DetailProduct("MK001","Milo",8500,9000,"Bottle","Milk",180,150,30,1350000,-180000,R.drawable.img_test4,R.drawable.barcode));
//
//    }
}