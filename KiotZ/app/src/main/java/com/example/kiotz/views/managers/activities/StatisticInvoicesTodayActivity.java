package com.example.kiotz.views.managers.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiotz.R;
import com.example.kiotz.adapters.ProductInReceiptAdapter;
import com.example.kiotz.database.FireBaseService;
import com.example.kiotz.database.dto.ReceiptSerializer;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.ItemReceipt;
import com.example.kiotz.models.Product;
import com.example.kiotz.models.Receipt;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class StatisticInvoicesTodayActivity extends AppCompatActivity {

    private InventoryViewModel<Receipt> receiptViewModel;
    private RecyclerView recyclerView;

    private ProductInReceiptAdapter adapter;
    private List<Receipt> receiptList;
    private List<Product> products;
    private List<Receipt> receiptListToday;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statistic_invoices_today);
        setupWindowInsets();
        bindingViews();
        setupViewModel();

        loadReceipt()
                .thenRun(this::convertListReceiptToListItemReceipt);


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

    }

    private void setupViewModel(){
        var receiptInventory=new Inventory<>(new Repository<>(new FireBaseService<>(new ReceiptSerializer())));
        receiptViewModel=InventoryViewModelFactory.getInstance().getViewModel(receiptInventory,Receipt.class);

    }

    private CompletableFuture<Void> loadReceipt(){
        return receiptViewModel.getAll().thenAccept(fetchReceipt->
                runOnUiThread(()->receiptList=new ArrayList<>(fetchReceipt)));
    }

    private void convertListReceiptToListItemReceipt(){
        receiptListToday=new ArrayList<>();
        LocalDate currentDay=LocalDate.now();
        String currentDayString=currentDay.getYear()+"-"+currentDay.getMonthValue()+"-"+currentDay.getDayOfMonth();
        for(Receipt receipt:receiptList){

        }
//        Map<String,Integer> map=new HashMap<>();
//        for(int i=0;i<products.size();i++){
//            if(map.containsKey(products.get(i).ID())){
//                continue;
//            }
//            map.put(products.get(i).ID(),1);
//            if(i==products.size()-1){
//                continue;
//            }
//            for(int j=i+1;j<products.size();j++){
//                if(products.get(i).ID().equals(products.get(j).ID())){
//                    int count=0;
//                    if(map.get(products.get(i).ID())!=null){
//                        count=map.get(products.get(i).ID());
//                    }
//                    count++;
//                    map.put(products.get(i).ID(),count);
//
//                }
//            }
//        }
//        itemReceipts=new ArrayList<>();
//
//        for(String id:map.keySet()){
//            Optional<Product> productOptional = products.stream()
//                    .filter(product -> product.ID().equals(id))
//                    .findFirst();
//            if(productOptional.isPresent()){
//                Product product=productOptional.get();
//                itemReceipts.add(new ItemReceipt(product.imageURL(),product.Name(),product.ID(),product.Price(),map.get(id)));
//
//            }
//        }
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


    }




}