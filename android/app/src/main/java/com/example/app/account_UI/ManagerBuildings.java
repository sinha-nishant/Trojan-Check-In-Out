package com.example.app.account_UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ManagerBuildings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_buildings);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this,  R.id.fragment2);

        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.manager_building:

                        break;

                    case R.id.manager_search:
                        Intent intent1 = new Intent(ManagerBuildings.this, ManagerSearch.class);
                        startActivity(intent1);
                        break;

                    case R.id.manager_home:
                        Intent intent2 = new Intent(ManagerBuildings.this, ManagerHome.class);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });

    }
}