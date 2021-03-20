package com.example.app.pre_login_UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.app.R;

public class SignUpPage extends AppCompatActivity {
    private Button studentSignupButton;
    private Button managerSignupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        studentSignupButton = (Button) findViewById(R.id.student_sign_up);
        studentSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStudentSignUp();
            }
        });

        managerSignupButton = (Button) findViewById(R.id.manager_sign_up);
        managerSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openManagerSignUp();
            }
        });
    }

    public void openStudentSignUp() {
        Intent i = new Intent(this, StudentSignUpStart.class);
        startActivity(i);
    }

    public void openManagerSignUp() {
        Intent i = new Intent(this, ManagerSignUpStart.class);
        startActivity(i);
    }
}