package com.example.app.account_UI;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.app.R;

public class StudentProfile extends AppCompatActivity {

    Fragment profileF, menuF;
    Button profileB, menuB;
    FrameLayout fl;
    FragmentManager fm;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        profileF = new StudentProfileFragment();
        menuF = new StudentProfileMenu();

        tv = findViewById(R.id.textViewCurrBuilding);
        tv.setText("You are checked into: ");
        fl = findViewById(R.id.fl_fragment);

        fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fl_fragment, profileF);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        profileB = findViewById(R.id.buttonFragment1);
        profileB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fl_fragment, profileF);
                fragmentTransaction.commit();
            }
        });

        menuB = findViewById(R.id.buttonFragment2);
        menuB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fl_fragment, menuF);
                fragmentTransaction.commit();
            }
        });
    }
}