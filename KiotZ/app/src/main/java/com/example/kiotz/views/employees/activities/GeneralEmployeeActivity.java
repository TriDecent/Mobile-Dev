package com.example.kiotz.views.employees.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.kiotz.R;
import com.example.kiotz.views.employees.fragments.ProductEmployeeFragment;
import com.example.kiotz.views.employees.fragments.SaleEmployeeFragment;
import com.example.kiotz.views.general.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class GeneralEmployeeActivity extends AppCompatActivity {

    private static final String TAG = "GeneralEmployeeActivity";
    private final Map<Integer, Fragment> fragmentCache = new HashMap<>();
    private final Map<Integer, Class<? extends Fragment>> fragmentClasses = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_employee);

        setupEdgeToEdge();
        setupFragmentClasses();
        setupBottomNavigationView();

        // Load the default fragment
        replaceFragment(R.id.SaleEmployee);
    }

    private void setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            int left = insets.getInsets(WindowInsetsCompat.Type.systemBars()).left;
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            int right = insets.getInsets(WindowInsetsCompat.Type.systemBars()).right;
            int bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(left, top, right, bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void setupFragmentClasses() {
        fragmentClasses.put(R.id.SaleEmployee, SaleEmployeeFragment.class);
        fragmentClasses.put(R.id.ProductsEmployee, ProductEmployeeFragment.class);
        fragmentClasses.put(R.id.SettingsEmployee, SettingsFragment.class);
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNaviEmployee);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        replaceFragment(item.getItemId());
        return true;
    }

    private void replaceFragment(int itemId) {
        Fragment fragment = fragmentCache.get(itemId);
        if (fragment == null) {
            fragment = createFragmentById(itemId);
            fragmentCache.put(itemId, fragment);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayoutEmployee, fragment)
                .commit();
    }

    private Fragment createFragmentById(int itemId) {
        Class<? extends Fragment> fragmentClass = fragmentClasses.get(itemId);
        if (fragmentClass != null) {
            try {
                return fragmentClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                Log.e(TAG, "Error creating fragment instance for itemId: " + itemId, e);
            }
        }
        return new ProductEmployeeFragment(); // Default fragment
    }
}