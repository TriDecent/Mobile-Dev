package com.example.kiotz.views.managers.activities;

import android.os.Bundle;
import android.view.View;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DailyStatistics extends AppCompatActivity {
    ImageView change_sort_order_bt;
    InventoryViewModel<Receipt> receiptViewModel;
    RecyclerView recycler_view;
    List<Receipt> receiptList;
    TextView sum_money_tv_from15, daily_receipt_count, sort_by_tv_form16;
    int sort_type;
    ReceiptAdapter receiptAdapter;

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
        setupSortButton();
        loadEmployees()
                .thenRun(this::setupRecyclerView)
                .thenRun(this::calculateTotalDailyIncome)
                .thenRun(this::displayTotalReceipt);
    }

    private void initVariable() {
        change_sort_order_bt = findViewById(R.id.iv_sort_by_employee_name);
        recycler_view = findViewById(R.id.recycler_view_rv);
        sum_money_tv_from15 = findViewById(R.id.sum_money_tv_from15);
        daily_receipt_count = findViewById(R.id.daily_receipt_count_tv_form16);
        sort_by_tv_form16 = findViewById(R.id.sort_by_tv_form16);
    }

    private void setupRecyclerView() {
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        receiptAdapter = new ReceiptAdapter(this, receiptList);
        recycler_view.setAdapter(receiptAdapter);
    }

    private void setupSortButton()
    {
//        default sort type is newest (no need to sort again)
        sort_type = 0;
        change_sort_order_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ++sort_type;

//                    reset
                if (sort_type > 2)
                    sort_type = 0;

                switch (sort_type)
                {
                    case 0:
                    {
                        sort_by_tv_form16.setText("mới nhất");
//                        TODO: add sort
                    }
                        break;
                    case 1:
                    {
                        sort_by_tv_form16.setText("số tiền");
//                      TODO: handle sort
                        receiptList.sort(new ReceiptSortByPrice());
                        receiptAdapter.notifyDataSetChanged();

                    }
                        break;
                    case 2:
                    {
                        sort_by_tv_form16.setText("case thứ 3 (case index 2)");
                        //do sort TODO
                    }
                        break;
                }
            }
        });
    }


    class ReceiptSortByPrice implements Comparator<Receipt> {

        // Used for sorting in ascending order of total amount
        public int compare(Receipt a, Receipt b){
            if (a.TotalPrice() > b.TotalPrice())
                return -1;
            else if (a.TotalPrice() < b.TotalPrice())
                return  1;
            return 0;
        }
    }

    private void setupViewModel() {
        var receiptInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new ReceiptSerializer())));
        receiptViewModel = InventoryViewModelFactory.getInstance().getViewModel(receiptInventory, Receipt.class);
    }

    private CompletableFuture<Void> loadEmployees() {
        return receiptViewModel.getAll().thenAccept(fetched_receipts ->
                runOnUiThread(() -> receiptList = new ArrayList<>(fetched_receipts)));
    }

    private void calculateTotalDailyIncome() {
        double sum = 0;
        for (Receipt receipt : receiptList) {
            sum = sum + receipt.TotalPrice();
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###");

        sum_money_tv_from15.setText(decimalFormat.format(sum));
    }

    private void displayTotalReceipt() {
        daily_receipt_count.setText(String.valueOf(receiptList.size()));
    }
}