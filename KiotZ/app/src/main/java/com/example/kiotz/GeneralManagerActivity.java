package com.example.kiotz;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.HashMap;
import java.util.Map;

public class GeneralManagerActivity extends AppCompatActivity {

    private final Map<Integer, Fragment> fragmentCache = new HashMap<>();
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_general_manager);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView=findViewById(R.id.nav_view);
        replaceFragmentv2(R.id.overview);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                if (id == R.id.overview) {
                    //replaceFragment(new OverviewFragment());
                    replaceFragmentv2(id);
                } else if (id == R.id.sale) {
                    //replaceFragment(new SaleFragment());
                    replaceFragmentv2(id);
                } else {
                    return true;
                }
                return true;
            }
        });




    }

    private void replaceFragmentv2(int itemId) {
        Fragment fragment = fragmentCache.get(itemId);
        if (fragment == null) {
            fragment = createFragmentById(itemId);
            fragmentCache.put(itemId, fragment);
            Toast.makeText(this,"Create new",Toast.LENGTH_SHORT).show();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayour, fragment)
                .commit();
    }
    private Fragment createFragmentById(int itemId) {
        if (itemId == R.id.overview) {
            return new OverviewFragment();
        } else if (itemId == R.id.sale) {
            return new SaleManagerFragment();
        } else {
            return new OverviewFragment();
        }

    }
}