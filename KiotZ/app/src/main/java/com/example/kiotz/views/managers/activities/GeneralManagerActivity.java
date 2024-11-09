package com.example.kiotz.views.managers.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.kiotz.R;
import com.example.kiotz.views.general.fragments.OverviewFragment;
import com.example.kiotz.views.general.fragments.SettingsFragment;
import com.example.kiotz.views.managers.fragments.SaleManagerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class GeneralManagerActivity extends AppCompatActivity {
    private static final String TAG = "GeneralManagerActivity";

    private final Map<Integer, Fragment> fragmentCache = new HashMap<>();
    private final Map<Integer, Class<? extends Fragment>> fragmentClasses = new HashMap<>();
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_manager);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupFragmentClasses();
        setupBottomNavigationView();

        // Load the default fragment
        replaceFragment(R.id.overview);
    }

    private void setupFragmentClasses() {
        fragmentClasses.put(R.id.overview, OverviewFragment.class);
        fragmentClasses.put(R.id.sale, SaleManagerFragment.class);
        fragmentClasses.put(R.id.settings, SettingsFragment.class);
    }

    private void setupBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.nav_view);
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
                .replace(R.id.frameLayout, fragment)
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
        return new OverviewFragment(); // Default fragment
    }
}