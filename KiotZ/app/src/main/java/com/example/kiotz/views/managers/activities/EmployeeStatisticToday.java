package com.example.kiotz.views.managers.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiotz.R;
import com.example.kiotz.adapters.EmployeesAdapter;
import com.example.kiotz.database.FireBaseService;
import com.example.kiotz.database.dto.EmployeeSerializer;
import com.example.kiotz.database.dto.ReceiptSerializer;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.Employee;
import com.example.kiotz.models.Receipt;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;
import com.example.kiotz.views.managers.data.App;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EmployeeStatisticToday extends AppCompatActivity {

    TextView daily_employee_count_tv, receipt_sold_value_tv, tvUsername, tvPosition;
    RecyclerView recycler_view_rv;
    InventoryViewModel<Employee> employeeViewModel;
    InventoryViewModel<Receipt> receiptViewModel;
    ArrayList<Receipt> receiptList;
    ArrayList<Employee> employeeArrayList;
    ArrayList<EmployeeTotalAmountSold> employeeTotalAmountSold;
    ArrayList<Employee> employees_display = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_statistic_today);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initVariables();
        setupViewModel();
        setupStatusBar();
        loadReceipt()
                .thenCompose(aVoid -> loadEmployee())
                .thenRun(this::filterReceipt)
                .thenRun(this::filterEmployee)
                .thenRun(this::sumEmployeeSold)
                .thenCompose(aVoid  -> sortEmployeeToTotalSold());
        ;
    }

    private void initVariables()
    {
        daily_employee_count_tv = findViewById(R.id.daily_employee_count_tv);
        receipt_sold_value_tv = findViewById(R.id.receipt_sold_value_tv);
        recycler_view_rv = findViewById(R.id.recycler_view_rv);
        tvUsername = findViewById(R.id.tvUsername);
        tvPosition = findViewById(R.id.tvPosition);

    }

    private void setupViewModel() {
        var receiptInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new ReceiptSerializer())));
        receiptViewModel = InventoryViewModelFactory.getInstance().getViewModel(receiptInventory, Receipt.class);

        var employeeInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new EmployeeSerializer())));
        employeeViewModel = InventoryViewModelFactory.getInstance().getViewModel(employeeInventory, Employee.class);
    }

    private CompletableFuture<Void> loadReceipt() {
        return receiptViewModel.getAll().thenAccept(fetched_receipts ->
                runOnUiThread(() -> receiptList = new ArrayList<>(fetched_receipts)));
    }

    private CompletableFuture<Void> loadEmployee() {
        return employeeViewModel.getAll().thenAccept(fetched_employee ->
                runOnUiThread(() -> employeeArrayList = new ArrayList<>(fetched_employee)));
    }

    private void filterReceipt()
    {
        LocalDateTime current_localDateTime = LocalDateTime.now();
        ArrayList<Receipt> receipts_filter = new ArrayList<>();
        for (Receipt i: receiptList) {
            if (i.DateTime().toLocalDate().isEqual(current_localDateTime.toLocalDate()))
            {
                receipts_filter.add(i);
            }
        }

        receiptList = receipts_filter;
    }

    private void filterEmployee()
    {
        ArrayList<Employee> employees_filtered = new ArrayList<>();
        for (Receipt i: receiptList) {
            for (Employee employee: employeeArrayList) {
                if (i.EmployeeId().equals(employee.ID()) && !employees_filtered.contains(employee))
                {
                    employees_filtered.add(employee);
                }
            }
        }

        employeeArrayList = employees_filtered;
    }

    private void setupStatusBar(){
        App app=(App) getApplication();
        tvUsername.setText(app.getName());
        tvPosition.setText(app.getPosition());
    }

    static class EmployeeTotalAmountSold
    {
        private String employeeID;
        private double totalAmount;

        EmployeeTotalAmountSold(String employeeID, double totalAmount)
        {
            this.totalAmount = totalAmount;
            this.employeeID = employeeID;
        }

        public String getEmployeeID()
        {
            return employeeID;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setEmployeeID(String employeeID) {
            this.employeeID = employeeID;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }
    }

    static class EmployeeSortByTotalAmountSold implements Comparator<EmployeeStatisticToday.EmployeeTotalAmountSold> {

        // Used for sorting in ascending order of total amount
        public int compare(EmployeeStatisticToday.EmployeeTotalAmountSold a, EmployeeStatisticToday.EmployeeTotalAmountSold b){
            if (a.getTotalAmount() > b.getTotalAmount())
                return -1;
            else if (a.getTotalAmount() < b.getTotalAmount())
                return  1;
            return 0;
        }
    }

    private void sumEmployeeSold()
    {
        double sum;
        for (Employee i: employeeArrayList) {
            sum = 0;
            for (Receipt receipt: receiptList) {
                if (receipt.EmployeeId().equals(i.ID()))
                {
                    sum = sum + receipt.TotalPrice();
                }
            }
            employeeTotalAmountSold.add(new EmployeeTotalAmountSold(i.ID(),sum));
        }

        employeeTotalAmountSold.sort(new EmployeeSortByTotalAmountSold());

        }

        private CompletableFuture<Void> sortEmployeeToTotalSold() {

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (EmployeeTotalAmountSold i : employeeTotalAmountSold) {
                CompletableFuture<Void> future = employeeViewModel.getById(i.getEmployeeID())
                        .thenAccept(employee -> {
                            if (employee != null) {
                                employees_display.add(employee);
                            }
                        });
                futures.add(future);


            }
                return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        }

        private void setupRecyclerView()
        {
//            EmployeesAdapter employeesAdapter = new EmployeesAdapter(this,)
//            TODO: adapt recyclerview to its list
        }


}