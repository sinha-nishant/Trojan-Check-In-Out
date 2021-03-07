package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StudentSignUpStart extends AppCompatActivity {

    String email, password;
    EditText emailInput;
    EditText passwordInput;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_sign_up_start);


        emailInput = (EditText) findViewById(R.id.studentSignUpEmailAddress);
        passwordInput = (EditText) findViewById(R.id.studentSignUpPassword);

        submitButton = (Button) findViewById(R.id.studentEmailPassSubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailInput.getText().toString();
                password = passwordInput.getText().toString();

                //used for popups to user
                //showToast(email);
                //showToast(password);
                if(email.length() == 0 || password.length() == 0)
                    showToast("Email or Password is blank");
                else if(email.length() < 8 || !email.substring(email.length()-8).equals("@usc.edu"))
                    showToast("Invalid Email");
                else
                {
                    //go to next activity
                }
            }
    });

    }
    private void showToast(String text)
    {
        Toast.makeText(StudentSignUpStart.this, text, Toast.LENGTH_SHORT).show();
    }
}