package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogInPage extends AppCompatActivity {

    String email, password;
    EditText emailInput;
    EditText passwordInput;
    Button submitLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_page);


        emailInput = (EditText) findViewById(R.id.editTextEmailLogin);
        passwordInput = (EditText) findViewById(R.id.editTextPasswordLogin);

        submitLoginButton = (Button) findViewById(R.id.btnLogin2);
        submitLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailInput.getText().toString();
                password = passwordInput.getText().toString();

                if(true) //do some backend checking here
                {
                    //go to next activity
                    showToast("Login verified and suceeded");
                }
                else if(false)
                {
                    showToast("You are not a registered user. Please sign up first");
                }
            }
        });

    }
    private void showToast(String text)
    {
        Toast.makeText(LogInPage.this, text, Toast.LENGTH_SHORT).show();
    }
}