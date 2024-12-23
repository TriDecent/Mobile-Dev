package com.example.kiotz.views.managers.activities;

import android.content.Intent;
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
import com.example.kiotz.models.Employee;
import com.example.kiotz.viewmodels.InventoryViewModel;

import java.util.ArrayList;
import java.util.List;

public class ViewInformationEmployeeActivity extends AppCompatActivity {

    private TextView tvEmployeeCount,tvEmployeeName,tvEmployeePosition;
    private ImageView imgSortByName;
    private RecyclerView recyclerViewEmployee;
    private InventoryViewModel<Employee> employeeViewModel;
    private List<Employee> listEmployee;
    private EmployeesAdapter adapter;
    private boolean isAscendingSort=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_information_employee);
        setupWindowInsets();
        initializeViews();
        setupStatusBar();


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
        listEmployee=new ArrayList<>();
    }

    private void setupStatusBar() {
        Intent intent=getIntent();
        String name=intent.getStringExtra("Name");
        String position=intent.getStringExtra("Position");
        tvEmployeeName.setText(name);
        tvEmployeePosition.setText(position);
    }

}