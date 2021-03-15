package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class StudentProfile extends AppCompatActivity {

    Fragment profileF, menuF;
    Button profileB, menuB;
    FrameLayout fl;
    FragmentManager fm;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        Bundle bundle = getIntent().getExtras();
        email = bundle.getString("email");

        profileF = new StudentProfileFragment(email);
        menuF = new StudentProfileMenu(email);

        fl = (FrameLayout) findViewById(R.id.fl_fragment);

        fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fl_fragment, profileF);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        profileB = (Button)findViewById(R.id.buttonFragment1);
        profileB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fl_fragment, profileF);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        menuB = (Button)findViewById(R.id.buttonFragment2);
        menuB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fl_fragment, menuF);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }
}