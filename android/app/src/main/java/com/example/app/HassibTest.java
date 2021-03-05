package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class HassibTest extends AppCompatActivity {
    private EditText eEmail;
    private EditText ePassword;
    private Button eLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hassib_test);

        eEmail = findViewById(R.id.etEmail);
        ePassword = findViewById(R.id.etPassword);
        eLogin = findViewById(R.id.btnLogin);
        eLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String inputEmail = eEmail.getText().toString();
                String inputPass = ePassword.getText().toString();
                if(inputEmail.isEmpty() || inputPass.isEmpty())
                {
                    Toast.makeText(HassibTest.this, "Enter all fields with text", Toast.LENGTH_SHORT).show();
                }else{
                    LogInOut login = new LogInOut();
                    boolean success_login=login.LogIn(inputEmail,inputPass);
                }

            }
        });

    }
}