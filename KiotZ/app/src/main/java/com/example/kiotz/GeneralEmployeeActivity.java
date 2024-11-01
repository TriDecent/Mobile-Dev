package com.example.kiotz;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
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

public class GeneralEmployeeActivity extends AppCompatActivity {

    private final Map<Integer,Fragment> fragmentCache=new HashMap<>();
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_general_employee);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView=findViewById(R.id.bottomNaviEmployee);

        replaceFragment(R.id.SaleEmployee);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if(id==R.id.SaleEmployee){
                    replaceFragment(id);
                    return true;
                }
                else if(id==R.id.ReceiptsEmployee){
                    return true;
                }
                else if(id==R.id.ProductsEmployee){
                    replaceFragment(id);
                    return true;
                }
                else if(id==R.id.SettingEmployee){

                    return true;
                }
                else  return false;
            }
        });


    }


    private Fragment createFragment(int itemId){
        if(itemId==R.id.ProductsEmployee){
            return new ProductEmployeeFragment();
        }
        else if(itemId==R.id.SaleEmployee){
            return new SaleEmployeeFragment();
        }

        return new ProductEmployeeFragment();
    }

    private void replaceFragment(int itemId){
        Fragment fragment=fragmentCache.get(itemId);
        if(fragment==null){
            fragment=createFragment(itemId);
            fragmentCache.put(itemId,fragment);

        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayoutEmployee,fragment)
                .commit();

    }



}