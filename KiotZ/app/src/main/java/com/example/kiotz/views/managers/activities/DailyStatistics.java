package com.example.kiotz.views.managers.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;
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
import com.example.kiotz.views.managers.data.App;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public class DailyStatistics extends AppCompatActivity {
    ImageView change_sort_order_bt;
    InventoryViewModel<Receipt> receiptViewModel;
    RecyclerView recycler_view;
    ArrayList<Receipt> receiptList;
    ArrayList<Receipt> temp_copy_list;
    TextView sum_money_tv_from15, daily_receipt_count, sort_by_tv_form16,statistic_title_tv,daily_sum_money_tv_from15;
    TextView tvUsername,tvPosition;
    SearchView search_sv_form16;
    int sort_type;
    ReceiptAdapter receiptAdapter;
    public final static String STATISTIC_RANGE_KEY = "STATISTIC_RANGE_KEY_INTENT";
    public static final int  Daily_int_value = 231;
    public final static int weekly_int_value = 2231;
    public final static int monthly_int_value = 3122;
    int Time_range_from_extras = 0;

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
        getExtras();
        setupViewModel();
        setupSortButton();
        setupStatusBar();
        loadReceipt()
                .thenRun(this::AdaptViewToTimeRange)
                .thenRun(this::copyReceiptList)
                .thenRun(this::setupRecyclerView)
                .thenRun(this::calculateTotalIncome)
                .thenRun(this::displayTotalReceipt)
                .thenRun(this::setupSearchView);
//                .thenRun(this::setupObservers);
    }

    private void getExtras()
    {
        Bundle extras = getIntent().getExtras();
        if (extras != null);
        assert extras != null;
        Time_range_from_extras = extras.getInt(STATISTIC_RANGE_KEY);
    }

    private void AdaptViewToTimeRange()
    {
        if (Time_range_from_extras == 0)
            return;
        ArrayList<Receipt> receipts_filter = new ArrayList<Receipt>();
        LocalDateTime current_localDateTime = LocalDateTime.now();
        switch (Time_range_from_extras)
        {
            case Daily_int_value:
            {
                for (Receipt i: receiptList) {
                    if (i.DateTime().toLocalDate().isEqual(current_localDateTime.toLocalDate()))
                    {
                        receipts_filter.add(i);
                    }

                }

                // replace the original receipt list
                receiptList = receipts_filter;
            }
            break;

            case weekly_int_value:
            {
                statistic_title_tv.setText("Weekly Statistics");
                daily_sum_money_tv_from15.setText("Weekly total income");
//              TODO: filter current week

                //find start of week
                LocalDate first_day_of_week = current_localDateTime.toLocalDate();
                while (first_day_of_week.getDayOfWeek() != DayOfWeek.MONDAY)
                    first_day_of_week = first_day_of_week.minusDays(1);



              for (Receipt i: receiptList) {
                    if ((!i.DateTime().toLocalDate().isBefore(first_day_of_week)) &&
                            (!i.DateTime().toLocalDate().isAfter(current_localDateTime.toLocalDate())))
                    {
                        receipts_filter.add(i);
                    }
                }

                // replace the original receipt list
                receiptList = receipts_filter;
            }
            break;

            case monthly_int_value:
            {
                statistic_title_tv.setText("Monthly Statistics");
                daily_sum_money_tv_from15.setText("Monthly total income");

                for (Receipt i: receiptList) {
                    if (
                            i.DateTime().getMonthValue() == current_localDateTime.getMonthValue() &&
                            i.DateTime().getYear() == current_localDateTime.getYear())
                    {
                        receipts_filter.add(i);
                    }

                }

                // replace the original receipt list
                receiptList = receipts_filter;
            }
            break;
        }
    }

    private void setupStatusBar(){
        App app=(App) getApplication();
        tvUsername.setText(app.getName());
        tvPosition.setText(app.getPosition());
    }

    private void initVariable() {
        change_sort_order_bt = findViewById(R.id.iv_sort_by_employee_name);
        recycler_view = findViewById(R.id.recycler_view_rv);
        sum_money_tv_from15 = findViewById(R.id.receipt_sold_value_tv);
        daily_receipt_count = findViewById(R.id.daily_employee_count_tv);
        sort_by_tv_form16 = findViewById(R.id.sort_by_tv_form16);
        statistic_title_tv = findViewById(R.id.daily_employee_statistic_title_tv);
        daily_sum_money_tv_from15 = findViewById(R.id.product_sold_tv_header);
        search_sv_form16 = findViewById(R.id.search_sv_form16);
        temp_copy_list = new ArrayList<Receipt>();
        tvUsername=findViewById(R.id.tvUsername);
        tvPosition=findViewById(R.id.tvPosition);

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
                        sort_by_tv_form16.setText("oldest");
                        receiptList.sort(new ReceiptSortByDateOld());
                        receiptAdapter.notifyDataSetChanged();
                    }
                        break;
                    case 1:
                    {
                        sort_by_tv_form16.setText("newest");
                        receiptList.sort(new ReceiptSortByDateNew());
                        receiptAdapter.notifyDataSetChanged();
                    }
                        break;
                    case 2:
                    {
                        sort_by_tv_form16.setText("total");
                        receiptList.sort(new ReceiptSortByPrice());
                        receiptAdapter.notifyDataSetChanged();

                    }
                        break;
                }
            }
        });
    }




    static class ReceiptSortByPrice implements Comparator<Receipt> {

        // Used for sorting in ascending order of total amount
        public int compare(Receipt a, Receipt b){
            if (a.TotalPrice() > b.TotalPrice())
                return -1;
            else if (a.TotalPrice() < b.TotalPrice())
                return  1;
            return 0;
        }
    }

    static class ReceiptSortByDateNew implements Comparator<Receipt> {

        // Used for sorting in ascending order of total amount
        public int compare(Receipt a, Receipt b){
            return -a.DateTime().compareTo(b.DateTime());
        }
    }

    static class ReceiptSortByDateOld implements Comparator<Receipt> {

        // Used for sorting in ascending order of total amount
        public int compare(Receipt a, Receipt b){
            return a.DateTime().compareTo(b.DateTime());
        }
    }

    private void setupViewModel() {
        var receiptInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new ReceiptSerializer())));
        receiptViewModel = InventoryViewModelFactory.getInstance().getViewModel(receiptInventory, Receipt.class);
    }

    private CompletableFuture<Void> loadReceipt() {
        return receiptViewModel.getAll().thenAccept(fetched_receipts ->
                runOnUiThread(() -> receiptList = new ArrayList<>(fetched_receipts)));
    }

    private void copyReceiptList()
    {
        temp_copy_list.addAll(receiptList);
    }

    private void calculateTotalIncome() {
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

    private  void setupSearchView()
    {

        search_sv_form16.clearFocus();
        search_sv_form16.setIconified(false);
//        search_sv_form16.requestFocus();
        search_sv_form16.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
//                return original data
                receiptList.clear();
                receiptList.addAll(temp_copy_list);;
                receiptAdapter.notifyDataSetChanged();
                search_sv_form16.clearFocus();

                return true;
            }

            private void search_filter_text(String text)
            {

                ArrayList<Receipt> filtered_list = new ArrayList<Receipt>();
                for (Receipt i : temp_copy_list) {
                    if (i.CustomerName().toLowerCase().contains(text.toLowerCase()) ||
                            i.EmployeeId().toLowerCase().contains(text.toLowerCase()) ||
                            (String.valueOf( i.DateTime().getDayOfMonth()) + "/" +
                                    String.valueOf(i.DateTime().getMonthValue()) + "/" +
                                    String.valueOf(i.DateTime().getYear())).contains(text)
                    )
                    {
                        filtered_list.add(i);
                    }
                }

                receiptList.clear();
                receiptList.addAll(filtered_list);
                receiptAdapter.notifyDataSetChanged();


            }

            @Override
            public boolean onQueryTextChange(String s) {
                search_filter_text(s);
                return true;
            }
        });


    }


}