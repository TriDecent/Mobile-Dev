package com.example.kiotz.views.managers.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.example.kiotz.database.dto.ReceiptSerializer;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.Product;
import com.example.kiotz.models.Receipt;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;
import com.example.kiotz.views.managers.data.App;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public class ProductSold extends AppCompatActivity implements IRecycleManagerDetail {

    TextView daily_receipt_count_tv, product_sold_tv, tvUsername, tvPosition;
    RecyclerView recycler_view;
    InventoryViewModel<Receipt> receiptViewModel;
    InventoryViewModel<Product> productViewModel;
    ArrayList<Receipt> receiptList;
    ArrayList<Product> productArrayList;
    ArrayList<Product_TimeSold> productTimeSoldArrayList;
    ArrayList<Product> productArrayList_display;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_sold);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initVariables();
        setupStatusBar();
        setupViewModel();
        loadReceipt()
                .thenCompose(aVoid -> loadProduct())
                .thenRun(this::filterReceipt)
                .thenRun(this::filterProduct)
                .thenRun(this::CalculateProductSoldCount)
                .thenRun(this::adaptViewsToData);
    }

    private void setupStatusBar(){
        App app=(App) getApplication();
        tvUsername.setText(app.getName());
        tvPosition.setText(app.getPosition());
    }

    private void initVariables()
    {
        daily_receipt_count_tv = findViewById(R.id.daily_employee_count_tv);
        product_sold_tv = findViewById(R.id.receipt_sold_value_tv);
        recycler_view = findViewById(R.id.recycler_view_rv);
        tvUsername=findViewById(R.id.tvUsername);
        tvPosition=findViewById(R.id.tvPosition);
    }

    private void setupViewModel() {
        var receiptInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new ReceiptSerializer())));
        receiptViewModel = InventoryViewModelFactory.getInstance().getViewModel(receiptInventory, Receipt.class);

        var productInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new ProductSerializer())));
        productViewModel = InventoryViewModelFactory.getInstance().getViewModel(productInventory, Product.class);
    }

    private CompletableFuture<Void> loadReceipt() {
        return receiptViewModel.getAll().thenAccept(fetched_receipts ->
                runOnUiThread(() -> receiptList = new ArrayList<>(fetched_receipts)));
    }

    private CompletableFuture<Void> loadProduct() {
        return productViewModel.getAll().thenAccept(fetched_product ->
                runOnUiThread(() -> productArrayList = new ArrayList<>(fetched_product)));
    }

    private void filterReceipt()
    {
        LocalDateTime current_localDateTime = LocalDateTime.now();
        ArrayList<Receipt> receipts_filter = new ArrayList<Receipt>();
        for (Receipt i: receiptList) {
            if (i.DateTime().toLocalDate().isEqual(current_localDateTime.toLocalDate()))
            {
                receipts_filter.add(i);
            }

        }

        // replace the original receipt list
        receiptList = receipts_filter;
    }

    private void filterProduct()
    {
        ArrayList<Product> product_filter = new ArrayList<Product>();

            for (Receipt e : receiptList)
            {
                for (String product : e.ProductIds())
                {
                    for (Product i: productArrayList)
                        if (product.equals(i.ID()) && !product_filter.contains(i))
                        {
                            product_filter.add(i);
                        }
                }
            }


        // replace the original receipt list
        productArrayList = product_filter;
    }

    @Override
    public void onItemClick(int position) {
//      do nothing
        if (productTimeSoldArrayList != null)
            for (Product_TimeSold i: productTimeSoldArrayList) {
                if (i.productID.equals(productArrayList_display.get(position).ID()))
                {
                    Toast.makeText(this, "Product was sold " + i.productCount + " times", Toast.LENGTH_SHORT).show();
                }
            }
    }

    static class Product_TimeSold
    {
        private String productID;
        private Integer productCount;

        Product_TimeSold(String productID, Integer productCount)
        {
            this.productCount = productCount;
            this.productID = productID;
        }

        public String getProductID()
        {
            return productID;
        }

        public Integer getProductCount() {
            return productCount;
        }

        public void setProductID(String productID) {
            this.productID = productID;
        }

        public void setProductCount(Integer productCount) {
            this.productCount = productCount;
        }
    }

    private void CalculateProductSoldCount()
    {
        productTimeSoldArrayList = new ArrayList<>();
        for (Product i: productArrayList) {
//            init with count of zero
            Product_TimeSold productTimeSold = new Product_TimeSold(i.ID(), 0);
            productTimeSoldArrayList.add(productTimeSold);
        }

//      count num of product sold
        for (Receipt receipt: receiptList) {
            for (String product_id_inside_receipt: receipt.ProductIds()) {
                for (Product_TimeSold product_time_sold: productTimeSoldArrayList) {
                    if (product_time_sold.getProductID().equals(product_id_inside_receipt))
                    {
                        product_time_sold.setProductCount(product_time_sold.getProductCount() + 1);
                    }
                }
            }
        }

//        sort
        productTimeSoldArrayList.sort(new ProductSoldSortByPrice());
    }


    static class ProductSoldSortByPrice implements Comparator<Product_TimeSold> {

        // Used for sorting in ascending order of total amount
        public int compare(Product_TimeSold a, Product_TimeSold b){
            if (a.getProductCount() > b.getProductCount())
                return -1;
            else if (a.getProductCount() < b.getProductCount())
                return  1;
            return 0;
        }
    }

    private void adaptViewsToData()
    {
        productArrayList_display = new ArrayList<>();
        for (Product_TimeSold i : productTimeSoldArrayList) {
            for (Product product : productArrayList) {
                if (i.getProductID().equals(product.ID()))
                {
                    productArrayList_display.add(product);
                }
            }
        }
        int total_sold = 0;
        for (Product_TimeSold i : productTimeSoldArrayList) {
            total_sold = total_sold + i.productCount;
        }

        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        ProductsAdapterManager adapterManager = new ProductsAdapterManager(this,productArrayList_display,this);
        recycler_view.setAdapter(adapterManager);

        daily_receipt_count_tv.setText(String.valueOf(receiptList.size()));
        product_sold_tv.setText(String.valueOf(total_sold));

    }
}