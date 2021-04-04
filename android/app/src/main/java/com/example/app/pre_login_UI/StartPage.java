package com.example.app.pre_login_UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.app.R;

public class StartPage extends AppCompatActivity {
    private Button signupButton;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_john_test);

        signupButton = (Button) findViewById(R.id.signup);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignUp();
            }
        });

        loginButton = (Button) findViewById(R.id.Login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogIn();
            }
        });
    }

    public void openSignUp() {
        Intent i = new Intent(this, SignUpPage.class);
        //Intent i = new Intent(this, StudentProfile.class);
        startActivity(i);
    }
    public void openLogIn() {
        Intent i = new Intent(this, LogInPage.class);
        //Intent i = new Intent(this, StudentHistory.class);
        startActivity(i);
    }

    public void openRestore(View v) {
        Intent i = new Intent(this, RestorePage.class);
        startActivity(i);
    }
}