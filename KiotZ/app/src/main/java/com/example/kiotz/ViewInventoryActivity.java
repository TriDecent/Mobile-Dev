package com.example.kiotz;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiotz.adapters.ProductsAdapterManager;
import com.example.kiotz.models.CustomProduct;

import java.util.ArrayList;

public class ViewInventoryActivity extends AppCompatActivity {

    RecyclerView recyclerViewProduct;
    ProductsAdapterManager adapterManager;
    ArrayList<CustomProduct> products;
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

        test();



    }

    private void test(){
        products=new ArrayList<>();
        products.add(new CustomProduct("MH001","Hao Hao noodles","Food",2500,3000,"package","path/QRCode",R.drawable.img_test1));
        products.add(new CustomProduct("MH002","Coca Cola","Beverage",9000,10000,"bottle","path/QRCode",R.drawable.img_test2));
        products.add(new CustomProduct("MH003","Pesi","Beverage",9000,10000,"bottle","path/QRCode",R.drawable.img_test3));
        products.add(new CustomProduct("MH004","Milo","milk",8000,10000,"bottle","path/QRCode",R.drawable.img_test4));


        recyclerViewProduct=findViewById(R.id.recycleViewProductManager);
        adapterManager=new ProductsAdapterManager(this,products);

        recyclerViewProduct.setAdapter(adapterManager);
        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(this));

    }
}