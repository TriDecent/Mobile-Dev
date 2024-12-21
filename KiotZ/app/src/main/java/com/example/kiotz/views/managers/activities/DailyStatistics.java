package com.example.kiotz.views.managers.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiotz.R;
import com.example.kiotz.adapters.ReceiptAdapter;
import com.example.kiotz.database.FireBaseService;
import com.example.kiotz.database.dto.ReceiptSerializer;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.Receipt;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DailyStatistics extends AppCompatActivity {
    ImageView change_sort_order_bt;
    InventoryViewModel<Receipt> receiptViewModel;
    RecyclerView recycler_view;
    List<Receipt> receiptList;
    TextView total_sum_tv, staff_count_tv_form16;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daily_statistics);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initVariable();
        setupViewModel();
        loadEmployees();
        setupRecyclerView();
        calculateTotalDailyIncome();
        displayTotalReceipt();
    }

    private void initVariable() {
        change_sort_order_bt = findViewById(R.id.iv_sort_by_employee_name);
        recycler_view = findViewById(R.id.recycler_view_rv);
        total_sum_tv = findViewById(R.id.sum_money_tv_from15);
        staff_count_tv_form16 = findViewById(R.id.staff_count_tv_form16);
    }

    private void setupRecyclerView() {
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setAdapter(new ReceiptAdapter(this, receiptList));
    }

    private void setupViewModel() {
        var employeeInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new ReceiptSerializer())));
        receiptViewModel = InventoryViewModelFactory.getInstance().getViewModel(employeeInventory, Receipt.class);
    }

    private CompletableFuture<Void> loadEmployees() {
        return receiptViewModel.getAll().thenAccept(fetched_receipts ->
                runOnUiThread(() -> receiptList = new ArrayList<>(fetched_receipts)));
    }

    private void calculateTotalDailyIncome()
    {
        double sum = 0;
        for (Receipt receipt : receiptList)
        {
            sum = sum + receipt.TotalPrice();
        }
        total_sum_tv.setText(String.format("%,d",sum));
    }

    private void displayTotalReceipt()
    {

    }
}