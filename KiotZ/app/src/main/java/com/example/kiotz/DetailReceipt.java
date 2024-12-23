package com.example.kiotz;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiotz.adapters.ProductsAdapterEmployee;
import com.example.kiotz.adapters.ReceiptAdapter;
import com.example.kiotz.database.FireBaseService;
import com.example.kiotz.database.dto.EmployeeSerializer;
import com.example.kiotz.database.dto.ProductSerializer;
import com.example.kiotz.database.dto.ReceiptSerializer;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.Employee;
import com.example.kiotz.models.Product;
import com.example.kiotz.models.Receipt;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class DetailReceipt extends AppCompatActivity {

    TextView receipt_id_tv,receipt_date_tv,employee_name_tv,receipt_total_price_tv,receipt_customer_name_tv,receipt_customer_phone_tv;
    InventoryViewModel<Receipt> receiptViewModel;
    InventoryViewModel<Employee> employeeViewModel;
    InventoryViewModel<Product> productViewModel;
    RecyclerView recyclerView;
    Receipt receipt;
    ProductsAdapterEmployee productAdapter;
    ArrayList<Employee> employeeArrayList;
    ArrayList<Product> productArrayList;
    ArrayList<Receipt> receiptList;
    Employee employee;
//    ArrayList<Product> tempProductList = new ArrayList<Product>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_receipt);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initVariables();
        setupViewModel();
        loadReceipt()
                .thenCompose(aVoid -> loadEmployee())
                .thenCompose(aVoid -> loadProduct())
                .thenRun(this::findReceipt)
                .thenRun(this::findEmployee)
                .thenRun(this::updateView)
                .thenRun(this::setupRecyclerView);
    }
        private void initVariables()
        {
            receipt_id_tv = findViewById(R.id.receipt_id_tv);
            receipt_date_tv = findViewById(R.id.receipt_date_tv);
            employee_name_tv = findViewById(R.id.employee_name_tv);
            receipt_total_price_tv = findViewById(R.id.receipt_total_price_tv);
            receipt_customer_name_tv = findViewById(R.id.receipt_customer_name_tv);
            receipt_customer_phone_tv = findViewById(R.id.receipt_customer_phone_tv);
            recyclerView = findViewById(R.id.recycler_view_products);

            //placeholder product list


        }

        private void findReceipt()
        {
            String Receipt_id = getIntent().getStringExtra(ReceiptAdapter.RECEIPT_ID_TO_VIEW_DETAILS_KEY);
            for (Receipt item : receiptList) {
                if (item.ID().equals(Receipt_id))
                {
                    receipt = item;
                    return;
                }
            }
        }

        private void findEmployee()
        {
            for (Employee item : employeeArrayList) {
                if (item.ID().equals(receipt.EmployeeId()))
                {
                    employee = item;
                    return;
                }
            }
        }

        private void setupViewModel() {
            var receiptInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new ReceiptSerializer())));
            receiptViewModel = InventoryViewModelFactory.getInstance().getViewModel(receiptInventory, Receipt.class);

            var employeeInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new EmployeeSerializer())));
            employeeViewModel = InventoryViewModelFactory.getInstance().getViewModel(employeeInventory, Employee.class);

            var productInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new ProductSerializer())));
            productViewModel = InventoryViewModelFactory.getInstance().getViewModel(productInventory, Product.class);
        }

        private CompletableFuture<Void> loadReceipt() {
            return receiptViewModel.getAll().thenAccept(fetched_receipts ->
                    runOnUiThread(() -> receiptList = new ArrayList<>(fetched_receipts)));
        }

        private CompletableFuture<Void> loadEmployee() {
            return employeeViewModel.getAll().thenAccept(fetched_employee ->
                    runOnUiThread(() -> employeeArrayList = new ArrayList<>(fetched_employee)));
        }

        private CompletableFuture<Void> loadProduct()
        {
            return productViewModel.getAll().thenAccept(fetched_employee ->
                    runOnUiThread(() -> productArrayList = new ArrayList<>(fetched_employee)));
        }

        @SuppressLint("SetTextI18n")
        private void updateView()
        {
            receipt_id_tv.setText(receipt.ID());
            LocalDateTime dateTime = receipt.DateTime();
            receipt_date_tv.setText(dateTime.getDayOfMonth() + "/" + dateTime.getMonthValue() + "/" + dateTime.getYear() + " "
            + dateTime.getHour() + ":" + dateTime.getMinute()
            );

            employee_name_tv.setText(employee.Name());
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###");
            receipt_total_price_tv.setText(decimalFormat.format(receipt.TotalPrice()));
            receipt_customer_name_tv.setText(receipt.CustomerName());
            receipt_customer_phone_tv.setText(receipt.CustomerPhone());
        }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductsAdapterEmployee(this);
        recyclerView.setAdapter(productAdapter);
//        TODO: pass in real product list after sample product data is inserted
    }

}