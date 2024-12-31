package com.example.kiotz;

import android.annotation.SuppressLint;
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

import com.example.kiotz.adapters.IRecycleManagerDetail;
import com.example.kiotz.adapters.ProductInReceiptAdapter;
import com.example.kiotz.adapters.ProductsAdapterManager;
import com.example.kiotz.adapters.ReceiptAdapter;
import com.example.kiotz.database.FireBaseService;
import com.example.kiotz.database.dto.EmployeeSerializer;
import com.example.kiotz.database.dto.ProductSerializer;
import com.example.kiotz.database.dto.ReceiptSerializer;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.Employee;
import com.example.kiotz.models.ItemReceipt;
import com.example.kiotz.models.Product;
import com.example.kiotz.models.Receipt;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;
import com.example.kiotz.views.managers.data.App;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class DetailReceipt extends AppCompatActivity implements IRecycleManagerDetail {

    TextView receipt_id_tv,receipt_date_tv,employee_name_tv,receipt_total_price_tv,receipt_customer_name_tv,receipt_customer_phone_tv, tv_username, tv_position;
    InventoryViewModel<Receipt> receiptViewModel;
    InventoryViewModel<Employee> employeeViewModel;
    InventoryViewModel<Product> productViewModel;
    RecyclerView recyclerView;
    Receipt receipt;
    ArrayList<Product> productArrayList;
    ArrayList<Receipt> receiptList;
    Employee employee;
    List<Product> products;

    List<ItemReceipt> itemReceipts;

    ProductInReceiptAdapter adapter;

    List<CompletableFuture<Product>> futureProducts = new ArrayList<>();
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


        setupStatusBar();
        findReceipt()
                .thenCompose(avoid -> findEmployee())
                .thenCompose(avoid->findProducts())
                .thenRun(this::convertListProductToListItemReceipt)
                .thenRun(this::updateView)
                .thenRun(this::setupRecyclerView);










    }
        private void initVariables() {
            receipt_id_tv = findViewById(R.id.receipt_id_tv);
            receipt_date_tv = findViewById(R.id.receipt_date_tv);
            employee_name_tv = findViewById(R.id.employee_name_tv);
            receipt_total_price_tv = findViewById(R.id.receipt_total_price_tv);
            receipt_customer_name_tv = findViewById(R.id.receipt_customer_name_tv);
            receipt_customer_phone_tv = findViewById(R.id.receipt_customer_phone_tv);
            recyclerView = findViewById(R.id.recycler_view_products);
            tv_username = findViewById(R.id.tv_username);
            tv_position = findViewById(R.id.tv_position);


        }

    private CompletableFuture<Void> findReceipt() {
        String receiptId = getIntent().getStringExtra(ReceiptAdapter.RECEIPT_ID_TO_VIEW_DETAILS_KEY);
        return receiptViewModel.getById(receiptId).thenAccept(fetched_receipt -> receipt = fetched_receipt);
    }

    private CompletableFuture<Void> findProducts() {
        List<String> IdProducts = new ArrayList<>(receipt.ProductIds());
        products = Collections.synchronizedList(new ArrayList<>());


        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (String id : IdProducts) {
                CompletableFuture<Void> future = productViewModel.getById(id)
                          .thenAccept(product -> {
                              if(product!=null){
                                  products.add(product);
                              }
                          });
                futures.add(future);
        }


        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    System.out.println("All products added to the list: " + products);
                });
    }

    private CompletableFuture<Void> findEmployee()
        {
            return employeeViewModel.getById(receipt.EmployeeId()).thenAccept(fetched_receipt -> employee = fetched_receipt);
        }



        private void setupViewModel() {
            var receiptInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new ReceiptSerializer())));
            receiptViewModel = InventoryViewModelFactory.getInstance().getViewModel(receiptInventory, Receipt.class);

            var employeeInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new EmployeeSerializer())));
            employeeViewModel = InventoryViewModelFactory.getInstance().getViewModel(employeeInventory, Employee.class);

            var productInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new ProductSerializer())));
            productViewModel = InventoryViewModelFactory.getInstance().getViewModel(productInventory, Product.class);
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
        adapter=new ProductInReceiptAdapter(this,itemReceipts);
        recyclerView.setAdapter(adapter);

    }


    private void convertListProductToListItemReceipt(){
        Map<String,Integer> map=new HashMap<>();
        for(int i=0;i<products.size();i++){
            if(map.containsKey(products.get(i).ID())){
                continue;
            }
            map.put(products.get(i).ID(),1);
            if(i==products.size()-1){
                continue;
            }
            for(int j=i+1;j<products.size();j++){
                if(products.get(i).ID().equals(products.get(j).ID())){
                    int count=0;
                    if(map.get(products.get(i).ID())!=null){
                        count=map.get(products.get(i).ID());
                    }
                    count++;
                    map.put(products.get(i).ID(),count);

                }
            }
        }
        itemReceipts=new ArrayList<>();

        for(String id:map.keySet()){
            Optional<Product> productOptional = products.stream()
                    .filter(product -> product.ID().equals(id))
                    .findFirst();
            if(productOptional.isPresent()){
                Product product=productOptional.get();
                itemReceipts.add(new ItemReceipt(product.imageURL(),product.Name(),product.ID(),product.Price(),map.get(id)));

            }
        }
    }

    private void setupStatusBar(){
        App app=(App) getApplication();
        tv_username.setText(app.getName());
        tv_position.setText(app.getPosition());
    }


    @Override
    public void onItemClick(int position) {
//      do nothing because it only shows a list of products
    }

    @Override
    public void onItemLongClick(int position) {

    }
}