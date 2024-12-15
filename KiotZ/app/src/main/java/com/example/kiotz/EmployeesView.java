package com.example.kiotz;

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

import com.example.kiotz.adapters.EmployeesAdapter;
import com.example.kiotz.database.FireBaseService;
import com.example.kiotz.database.dto.EmployeeSerializer;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.Employee;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EmployeesView extends AppCompatActivity {
    private TextView tvEmployeeCount;
    private ImageView ivSortByName;
    private RecyclerView recyclerViewEmployee;
    private InventoryViewModel<Employee> employeeViewModel;
    private List<Employee> employees;
    private EmployeesAdapter adapter;
    private boolean isAscendingSort = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_account);
        setupWindowInsets();

        initializeViews();
        setupViewModel();
        loadEmployees();
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeViews() {
        tvEmployeeCount = findViewById(R.id.tv_employee_count);
        ivSortByName = findViewById(R.id.iv_employee_sort_by_name);
        recyclerViewEmployee = findViewById(R.id.rv_employees);
        recyclerViewEmployee.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupViewModel() {
        var employeeInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new EmployeeSerializer())));
        employeeViewModel = InventoryViewModelFactory.getInstance().getViewModel(employeeInventory, Employee.class);
    }

    private void loadEmployees() {
        employeeViewModel.getAll().thenAccept(fetchedEmployees -> {
            runOnUiThread(() -> {
                employees = new ArrayList<>(fetchedEmployees);

                setupAdapter();
                setupSortButton();
                updateEmployeeCount();
            });
        });
    }

    private void setupAdapter() {
        adapter = new EmployeesAdapter(this, employees);
        recyclerViewEmployee.setAdapter(adapter);
    }

    private void setupSortButton() {
        ivSortByName.setOnClickListener(v -> sortEmployees());
    }

    private void sortEmployees() {
        var comparator = Comparator.comparing(
                Employee::Name,
                Comparator.nullsFirst(String::compareTo)
        );

        employees.sort(isAscendingSort ? comparator : comparator.reversed());
        isAscendingSort = !isAscendingSort;

        adapter.notifyDataSetChanged();
    }

    private void updateEmployeeCount() {
        tvEmployeeCount.setText(getString(R.string.employees_count, employees.size()));
    }
}