package com.example.kiotz.views.general.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kiotz.R;
import com.example.kiotz.authentication.Authenticator;
import com.example.kiotz.database.FireBaseService;
import com.example.kiotz.database.dto.EmployeeSerializer;
import com.example.kiotz.enums.Gender;
import com.example.kiotz.inventory.Inventory;
import com.example.kiotz.models.Employee;
import com.example.kiotz.repositories.Repository;
import com.example.kiotz.viewmodels.InventoryViewModel;
import com.example.kiotz.viewmodels.InventoryViewModelFactory;
import com.example.kiotz.views.managers.data.App;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class AccountInformationView extends AppCompatActivity {
    InventoryViewModel<Employee> employeeViewModel;
    ArrayList<Employee> employees;
    Employee current_employee;
    EditText et_email, et_password, et_employee_name, et_employee_date;
    RadioButton rb_account_male, rb_account_female, rb_account_manager, rb_account_employee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_information_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initVariable();
        setupViewModel();
        loadEmployees()
                .thenRun(this::getInformationCurrentUser)
                .thenRun(this::adaptViewsToFetchedData);
    }

    private void getInformationCurrentUser() {
        var authenticator = Authenticator.getInstance();
        var userId = authenticator.getCurrentUserId();

        current_employee = employees.stream()
                .filter(e -> e.ID().equals(userId))
                .findFirst()
                .orElse(null);

        assert current_employee != null;

    }

    private void setupViewModel() {
        var employeeInventory = new Inventory<>(new Repository<>(new FireBaseService<>(new EmployeeSerializer())));
        employeeViewModel = InventoryViewModelFactory.getInstance().getViewModel(employeeInventory, Employee.class);
    }

    private CompletableFuture<Void> loadEmployees() {
        return employeeViewModel.getAll().thenAccept(fetchedEmployees ->
                runOnUiThread(() -> employees = new ArrayList<>(fetchedEmployees)));
    }

    private void initVariable()
    {
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_employee_name = findViewById(R.id.et_employee_name);
        et_employee_date = findViewById(R.id.et_employee_date);
        rb_account_male = findViewById(R.id.rb_account_male);
        rb_account_female = findViewById(R.id.rb_account_female);
        rb_account_manager = findViewById(R.id.rb_account_manager);
        rb_account_employee = findViewById(R.id.rb_account_employee);
    }

    private void adaptViewsToFetchedData()
    {
        et_email.setText(current_employee.Email());
        et_employee_name.setText(current_employee.Name());
        et_employee_date.setText(current_employee.Date());

        if (current_employee.Gender() == Gender.FEMALE)
        {
            rb_account_female.setChecked(true);
            rb_account_male.setChecked(false);
        }
        else
        {
            rb_account_male.setChecked(true);
            rb_account_female.setChecked(false);
        }

        if (current_employee.IsAdmin())
        {
            rb_account_manager.setChecked(true);
            rb_account_employee.setChecked(false);
        }
        else
        {
            rb_account_employee.setChecked(true);
            rb_account_manager.setChecked(false);
        }
    }
}