package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.app.account_UI.StudentHistory;
import com.example.app.account_UI.StudentProfileFragment;
import com.example.app.account_UI.StudentProfileMenu;
import com.google.android.material.navigation.NavigationView;

public class StudentProfileNavDrawerJohn extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView mTextView;
    private DrawerLayout drawer;
    Fragment profileF;
    FrameLayout fl;
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile_nav_drawer_john);

        Toolbar tb = findViewById(R.id.toolbar2);
        setSupportActionBar(tb);

        NavigationView nv = findViewById(R.id.nav_view);
        nv.setNavigationItemSelectedListener(this);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, tb,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.layoutContainer,
                new FragmentQROptions()).commit();






    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_building_history:
                Intent i = new Intent(this, StudentHistory.class);
                startActivity(i);
                break;
            case R.id.nav_pw_change:
                //Do work here, Arjun
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.layoutContainer,
                        new StudentProfileFragment()).commit();
                break;
            case R.id.nav_qr_frag:
                getSupportFragmentManager().beginTransaction().replace(R.id.layoutContainer,
                        new FragmentQROptions()).commit();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed()
    {
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
}