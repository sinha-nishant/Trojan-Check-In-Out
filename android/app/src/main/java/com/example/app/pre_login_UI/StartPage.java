package com.example.app.pre_login_UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.app.R;

public class StartPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_john_test);

        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignUp();
            }
        });

        findViewById(R.id.Login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogIn();
            }
        });

        findViewById(R.id.restoreBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRestore();
            }
        });
    }

    public void openSignUp() {
        Intent i = new Intent(this, SignUpPage.class);
        startActivity(i);
    }
    public void openLogIn() {
        Intent i = new Intent(this, LogInPage.class);
        startActivity(i);
    }

    public void openRestore() {
        Intent i = new Intent(this, RestorePage.class);
        startActivity(i);
    }
}