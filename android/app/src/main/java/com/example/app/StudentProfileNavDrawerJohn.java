package com.example.app;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StudentProfileNavDrawerJohn extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile_nav_drawer_john);

        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
    }
}