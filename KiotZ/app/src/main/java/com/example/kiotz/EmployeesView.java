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

public class EmployeesView extends AppCompatActivity {
    TextView tvEmployeeCount;
    ImageView ivSortByName;
    RecyclerView recyclerViewEmployee;
    InventoryViewModel<Employee> employeeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvEmployeeCount = findViewById(R.id.tv_employee_count);
        ivSortByName = findViewById(R.id.iv_employee_sort_by_name);
        recyclerViewEmployee = findViewById(R.id.rv_employees);

        var employeeInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new EmployeeSerializer())));
        employeeViewModel = InventoryViewModelFactory.getInstance().getViewModel(employeeInventory, Employee.class);

        employeeViewModel.getAll().thenAccept(employeesList ->
                runOnUiThread(() ->
                        tvEmployeeCount.setText(getString(R.string.employees_count, employeesList.size()))));

        var adapter = new EmployeesAdapter(this, employeeViewModel);
        recyclerViewEmployee.setAdapter(adapter);
        recyclerViewEmployee.setLayoutManager(new LinearLayoutManager(this));

        //        employeeViewModel.getObservableAddedItem().observe(this, employee -> {
        //            employeeViewModel.getAll().thenAccept(employees -> {
        //                runOnUiThread(() -> {
        //                    adapter.notifyItemInserted(employees.size() - 1);
        //                    recyclerViewEmployee.scrollToPosition(employees.size() - 1);
        //                });
        //            });
        //        });
    }
}