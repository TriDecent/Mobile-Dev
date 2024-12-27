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

public class EmployeeStatisticToday extends AppCompatActivity {

    TextView daily_employee_count_tv, receipt_sold_value_tv;
    RecyclerView recycler_view_rv;
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
    }

    private void initVariables()
    {
        daily_employee_count_tv = findViewById(R.id.daily_employee_count_tv);
        receipt_sold_value_tv = findViewById(R.id.receipt_sold_value_tv);
        recycler_view_rv = findViewById(R.id.recycler_view_rv);

    }
}