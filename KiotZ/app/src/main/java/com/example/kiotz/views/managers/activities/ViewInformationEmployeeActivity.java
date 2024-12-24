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
import com.example.kiotz.adapters.EmployeesAdapter;
import com.example.kiotz.authentication.Authenticator;
import com.example.kiotz.database.FireBaseService;
import com.example.kiotz.database.dto.EmployeeSerializer;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.Employee;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;
import com.example.kiotz.views.managers.data.App;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ViewInformationEmployeeActivity extends AppCompatActivity {

    private TextView tvEmployeeCount, tvEmployeeName, tvEmployeePosition;
    private ImageView imgSortByName;
    private RecyclerView recyclerViewEmployee;
    private InventoryViewModel<Employee> employeeViewModel;
    private List<Employee> listEmployee;
    private EmployeesAdapter adapter;
    private boolean isAscendingSort = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_information_employee);
        setupWindowInsets();
        initializeViews();
        setupStatusBar();

        setupViewModel();
        loadEmployees()
                .thenRun(this::updateEmployeeCount)
                .thenRun(this::setupAdapter)
                .thenRun(this::setupObservers)
                .thenRun(this::setupSortButton);
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
        tvEmployeeName = findViewById(R.id.tv_status_bar_employee_name);
        tvEmployeePosition = findViewById(R.id.tv_status_bar_position);
        imgSortByName = findViewById(R.id.iv_employee_sort_by_name);
        recyclerViewEmployee = findViewById(R.id.rv_employees);
        recyclerViewEmployee.setLayoutManager(new LinearLayoutManager(this));
        listEmployee = new ArrayList<>();
    }

    private void setupStatusBar() {
        App app = (App) getApplication();
        tvEmployeeName.setText(app.getName());
        tvEmployeePosition.setText(app.getPosition());
    }

    private void setupViewModel() {
        var employeeInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new EmployeeSerializer())));
        employeeViewModel = InventoryViewModelFactory.getInstance().getViewModel(employeeInventory, Employee.class);
    }

    private CompletableFuture<Void> loadEmployees() {
        return employeeViewModel.getAll().thenAccept(fetchedEmployees ->
                runOnUiThread(() -> listEmployee = new ArrayList<>(fetchedEmployees)));
    }

    private void setupObservers() {
        employeeViewModel.getObservableAddedItem().observe(this, addEmployee -> {
            if (listEmployee.stream().anyMatch(e -> e.ID().equals(addEmployee.ID()))) {
                return;
            }

            listEmployee.add(addEmployee);
            adapter.notifyItemInserted(listEmployee.size() - 1);
            updateEmployeeCount();
        });

        employeeViewModel.getObservableDeletedItem().observe(this, pair -> {
            int position = pair.first;
            var deletedEmployee = pair.second;

            if (listEmployee.stream().noneMatch(e -> e.ID().equals(deletedEmployee.ID()))) {
                return;
            }
            listEmployee.remove(position);
            adapter.notifyItemRemoved(position);
            updateEmployeeCount();

        });

        employeeViewModel.getObservableUpdatedItem().observe(this, pair -> {
            int position = pair.first;
            var updatedEmployee = pair.second;

            var employeeNeedToBeUpdated = listEmployee.get(position);
            if (employeeNeedToBeUpdated.equals(updatedEmployee)) {
                return;
            }
            listEmployee.set(position, updatedEmployee);
            adapter.notifyItemChanged(position);
        });
    }

    private void updateEmployeeCount() {
        tvEmployeeCount.setText(getString(R.string.employees_count, listEmployee.size()));
    }

    private void setupAdapter() {

        var authenticator = Authenticator.getInstance();
        var userId = authenticator.getCurrentUserId();

        var sessionEmployee = listEmployee.stream()
                .filter(e -> e.ID().equals(userId))
                .findFirst()
                .orElse(null);

        adapter = new EmployeesAdapter(this, sessionEmployee, listEmployee, null);
        recyclerViewEmployee.setAdapter(adapter);
    }

    private void sortEmployeeByName() {
        var comparator = Comparator.comparing(
                Employee::Name,
                Comparator.nullsFirst(String::compareTo)
        );
        listEmployee.sort(isAscendingSort ? comparator : comparator.reversed());
        isAscendingSort = !isAscendingSort;
        adapter.notifyDataSetChanged();
    }

    private void setupSortButton() {
        imgSortByName.setOnClickListener(v -> sortEmployeeByName());
    }
}