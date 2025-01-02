package com.example.kiotz.views.employees.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiotz.R;
import com.example.kiotz.adapters.AdapterDetailSaleInvoiceActivity;
import com.example.kiotz.adapters.IItemFragment;
import com.example.kiotz.database.FireBaseService;
import com.example.kiotz.database.dto.ProductSerializer;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.IIdentifiable;
import com.example.kiotz.models.Product;
import com.example.kiotz.models.ProductInvoice;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;
import com.example.kiotz.views.dialogs.SaleDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DetailSaleInvoiceActivity extends AppCompatActivity implements IItemFragment {

    private Button buttonConfirm;
    private EditText editTextNameCustomer,editTextPhoneCustomer;
    private List<ProductInvoice> productInvoiceList;

    private InventoryViewModel<Product> productViewModel;

    private List<Product> productList;
    private RecyclerView recyclerView;

    private AdapterDetailSaleInvoiceActivity adapter;

    private boolean isChange=false;

    private List<ProductInvoice> backupProductInvoiceList;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_sale_invoice);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindingViews();
        setupViewModel();

        setOnClickForButtonConfirm();

        getData()
                .thenRun(this::setupAdapter);


    }


    private void bindingViews(){
        buttonConfirm=findViewById(R.id.buttonConfirmInvoiceDetailSale);
        editTextNameCustomer=findViewById(R.id.editTextCustomerName);
        editTextPhoneCustomer=findViewById(R.id.editTextCustomerPhone);
        recyclerView=findViewById(R.id.recycleViewInvoice);
    }

    private void setOnClickForButtonConfirm(){
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameCustomer=String.valueOf(editTextNameCustomer.getText());
                String phoneCustomer=String.valueOf(editTextPhoneCustomer.getText());
                Intent intentResult=new Intent();
                intentResult.putExtra("name_customer",nameCustomer);
                intentResult.putExtra("phone_customer",phoneCustomer);

                if(backupProductInvoiceList.size()!=productInvoiceList.size()){
                    isChange=true;
                }
                else{
                    for(int i=0;i<productInvoiceList.size();i++){
                        if(productInvoiceList.get(i).getQuantity()!=backupProductInvoiceList.get(i).getQuantity()){
                            isChange=true;
                        }
                    }
                }

                if(isChange){
                    intentResult.putParcelableArrayListExtra("list_product_invoice",(ArrayList<ProductInvoice>) productInvoiceList);
                }

                setResult(200,intentResult);
                finish();
            }
        });
    }

    private CompletableFuture<Void> getData(){
        backupProductInvoiceList=new ArrayList<>();
        Intent intent = getIntent();
        productInvoiceList = intent.getParcelableArrayListExtra("productList");
        String name=intent.getStringExtra("nameCustomer");
        String phone=intent.getStringExtra("phoneCustomer");
        if(name!=null){
            editTextNameCustomer.setText(name);
        }
        if(phone!=null){
            editTextPhoneCustomer.setText(phone);
        }
        if(productInvoiceList!=null){
            for(ProductInvoice p: productInvoiceList){
                backupProductInvoiceList.add(new ProductInvoice(p.getId(),p.getQuantity(),p.getTotalPrice()));
            }
        }

        productList=new ArrayList<>();
        List<CompletableFuture<Void>> futures=new ArrayList<>();
        if(productInvoiceList!=null){
            for(ProductInvoice productInvoice:productInvoiceList){
                CompletableFuture<Void> future=productViewModel.getById(productInvoice.getId())
                        .thenAccept(product -> {
                            productList.add(product);

                        });

                futures.add(future);
            }
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(()->{
                });

    }

    private void setupViewModel(){
        var productInventory=new Inventory<>(new Repository<>(new FireBaseService<>(new ProductSerializer())));
        productViewModel= InventoryViewModelFactory.getInstance().getViewModel(productInventory, Product.class);
    }

    private void setupAdapter(){
        Toast.makeText(this,String.valueOf(productList.size()),Toast.LENGTH_SHORT).show();
        adapter=new AdapterDetailSaleInvoiceActivity(productInvoiceList,this,productList,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onItemClick(int position) {
        ProductInvoice productInvoiceClicked=productInvoiceList.get(position);
        Product currentProduct=null;
        for(Product p:productList){
            if(p.ID().equals(productInvoiceClicked.getId())){
                currentProduct=p;
            }
        }
        if(currentProduct!=null){
            SaleDialog saleDialog=new SaleDialog(this,currentProduct,productInvoice -> {
                for(ProductInvoice p1: productInvoiceList){
                    if(p1.getId().equals(productInvoice.getId())){
                        p1.setQuantity(productInvoice.getQuantity());
                        p1.setTotalPrice(productInvoice.getTotalPrice());
                        adapter.notifyItemChanged(position);

                    }
                }
            });
            saleDialog.show();
        }
        else{
            Log.d("NULL","currentProduct null");
        }



    }

    @Override
    public void onLongItemClick(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    productInvoiceList.remove(position);
                    adapter.notifyItemRemoved(position);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
}