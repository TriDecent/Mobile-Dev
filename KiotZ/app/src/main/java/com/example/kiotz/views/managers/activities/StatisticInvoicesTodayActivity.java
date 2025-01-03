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
import com.example.kiotz.adapters.ProductInReceiptAdapter;
import com.example.kiotz.adapters.ReceiptAdapter;
import com.example.kiotz.database.FireBaseService;
import com.example.kiotz.database.dto.ReceiptSerializer;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.ItemReceipt;
import com.example.kiotz.models.Product;
import com.example.kiotz.models.Receipt;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;
import com.example.kiotz.views.managers.data.App;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class StatisticInvoicesTodayActivity extends AppCompatActivity {

    private InventoryViewModel<Receipt> receiptViewModel;
    private RecyclerView recyclerView;

    private ReceiptAdapter adapter;
    private List<Receipt> receiptList;
    private List<Receipt> receiptListToday;

    private List<Receipt> receiptListTodayBackUp;
    private TextView tvDate,tvUsername,tvPosition,tvFilter,tvInvoices,tvRevenue;

    private ImageView imageViewChangeFilter;

    private SearchView searchView;

    private  String[] nameFilter={"most revenue","oldest","latest"};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statistic_invoices_today);
        setupWindowInsets();
        bindingViews();
        setupViewModel();
        setupStatusBar();

        loadReceipt()
                .thenRun(this::prepareReceiptListToday)
//                .thenRun(this::setupObservers)
                .thenRun(this::setupAdapter)
                .thenRun(this::setContentForTextView)
                .thenRun(this::copyListReceiptToday)
                .thenRun(this::setOnClickChangeFilter)
                .thenRun(this::setupSearchView);

    }


    private void setupWindowInsets(){
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void bindingViews(){
        recyclerView=findViewById(R.id.recycleViewStatisticInvoiceToday);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tvUsername=findViewById(R.id.tv_username_invoice_statistic_today);
        tvPosition=findViewById(R.id.tv_position_invoice_statistic_today);
        tvDate=findViewById(R.id.tvDate);
        tvFilter=findViewById(R.id.tvFilterContent);
        tvInvoices=findViewById(R.id.tvNumberInvoicesContent);
        tvRevenue=findViewById(R.id.tvRevenueFromInvoiceContent);
        imageViewChangeFilter=findViewById(R.id.imageViewChangeFilter);
        searchView=findViewById(R.id.searchInvoiceStatisticToday);
    }

    private void setupStatusBar(){
        App app=(App) getApplication();
        tvUsername.setText(app.getName());
        tvPosition.setText(app.getPosition());
    }

    private void copyListReceiptToday(){
        receiptListTodayBackUp=new ArrayList<>();

        for(Receipt receipt: receiptListToday){
            receiptListTodayBackUp.add(new Receipt(receipt.ID(),receipt.DateTime(),receipt.EmployeeId(),receipt.CustomerName(),receipt.CustomerPhone(),receipt.ProductIds(),receipt.TotalPrice()));
        }

    }

    private void setOnClickChangeFilter(){
        imageViewChangeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentFilter=tvFilter.getText().toString();
                if(currentFilter.equals(nameFilter[0])){
                    tvFilter.setText(nameFilter[1]);
                    receiptListToday.sort(new ReceiptSortByDateOld());
                    adapter.notifyDataSetChanged();


                }
                else if(currentFilter.equals(nameFilter[1])){
                    tvFilter.setText(nameFilter[2]);
                    receiptListToday.sort(new ReceiptSortByDateNew());
                    adapter.notifyDataSetChanged();
                }
                else  {
                    tvFilter.setText(nameFilter[0]);
                    receiptListToday.sort(new ReceiptSortByPrice());
                    adapter.notifyDataSetChanged();
                }




            }
        });
    }


    private void setContentForTextView(){
        LocalDate currentDay=LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = currentDay.format(formatter);
        tvDate.setText(formattedDate);

        tvInvoices.setText(String.valueOf(receiptListToday.size()));

        double totalRevenue=0;
        for(Receipt receipt: receiptListToday){
            totalRevenue+=receipt.TotalPrice();
        }

        tvRevenue.setText(String.valueOf(totalRevenue));



    }

    private void setupViewModel(){
        var receiptInventory=new Inventory<>(new Repository<>(new FireBaseService<>(new ReceiptSerializer())));
        receiptViewModel=InventoryViewModelFactory.getInstance().getViewModel(receiptInventory,Receipt.class);

    }

    private CompletableFuture<Void> loadReceipt(){
        return receiptViewModel.getAll().thenAccept(fetchReceipt->
                runOnUiThread(()->receiptList=new ArrayList<>(fetchReceipt)));
    }

    private void prepareReceiptListToday(){
        receiptListToday=new ArrayList<>();
        LocalDate currentDay=LocalDate.now();
        //String currentDayString=currentDay.getYear()+"-"+currentDay.getMonthValue()+"-"+currentDay.getDayOfMonth();
        for(Receipt receipt:receiptList){
            if(receipt.DateTime().toLocalDate().equals(currentDay)){
                receiptListToday.add(new Receipt(receipt.ID(),receipt.DateTime(),receipt.EmployeeId(),receipt.CustomerName(),receipt.CustomerPhone(),receipt.ProductIds(),receipt.TotalPrice()));
            }
        }

    }



    private void setupObservers(){
        receiptViewModel.getObservableAddedItem().observe(this,addedReceipt->{
            if(receiptList.stream().anyMatch(r -> r.ID().equals(addedReceipt.ID()))){
                return;
            }
            receiptList.add(addedReceipt);
            adapter.notifyItemChanged(receiptList.size()-1);

        });

        receiptViewModel.getObservableUpdatedItem().observe(this,pair->{
            int position =pair.first;
            var updatedReceipt=pair.second;
            var receiptNeedsToBeUpdated=receiptList.get(position);
            if(updatedReceipt.equals(receiptNeedsToBeUpdated)){
                return;
            }

            receiptList.set(position,updatedReceipt);
            adapter.notifyItemChanged(position);

        });

        receiptViewModel.getObservableDeletedItem().observe(this,pair->{
            int position=pair.first;
            var deletedReceipt=pair.second;
            if(receiptList.stream().noneMatch(r ->r.ID().equals(deletedReceipt.ID()))){
                return;
            }

            receiptList.remove(position);
            adapter.notifyItemRemoved(position);
        });
    }


    private void setupAdapter(){
        adapter=new ReceiptAdapter(this,receiptListToday);
        recyclerView.setAdapter(adapter);

        receiptListToday.sort(new ReceiptSortByPrice());
        adapter.notifyDataSetChanged();


    }


    static class ReceiptSortByPrice implements Comparator<Receipt>{

        @Override
        public int compare(Receipt a, Receipt b) {
            if(a.TotalPrice()>b.TotalPrice()){
                return -1;
            }
            else if(a.TotalPrice()<b.TotalPrice()){
                return 1;
            }
            return 0;
        }
    }

    static class ReceiptSortByDateNew implements Comparator<Receipt>{

        @Override
        public int compare(Receipt o1, Receipt o2) {
            return -o1.DateTime().compareTo(o2.DateTime());
        }
    }

    static class ReceiptSortByDateOld implements Comparator<Receipt>{

        @Override
        public int compare(Receipt o1, Receipt o2) {
            return o1.DateTime().compareTo(o2.DateTime());
        }
    }

    private void setupSearchView(){

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Receipt> filter_list=search_filter_text(newText);
                receiptListToday.clear();
                for(Receipt receipt:filter_list){
                    receiptListToday.add(new Receipt(receipt.ID(),receipt.DateTime(),receipt.EmployeeId(),receipt.CustomerName(),receipt.CustomerPhone(),receipt.ProductIds(),receipt.TotalPrice()));
                }

                adapter.notifyDataSetChanged();



                return true;
            }
        });

    }

    private List<Receipt> search_filter_text(String text){
        List<Receipt> filter_list=new ArrayList<>();
        for(Receipt r:receiptListTodayBackUp){
            if (r.CustomerName().toLowerCase().contains(text.toLowerCase()) ||
                    r.EmployeeId().toLowerCase().contains(text.toLowerCase()) ||
                    (String.valueOf( r.DateTime().getDayOfMonth()) + "/" +
                            String.valueOf(r.DateTime().getMonthValue()) + "/" +
                            String.valueOf(r.DateTime().getYear())).contains(text)
            )
            {
                filter_list.add(new Receipt(r.ID(),r.DateTime(),r.EmployeeId(),r.CustomerName(),r.CustomerPhone(),r.ProductIds(),r.TotalPrice()));
            }
        }
        return filter_list;


    }



}