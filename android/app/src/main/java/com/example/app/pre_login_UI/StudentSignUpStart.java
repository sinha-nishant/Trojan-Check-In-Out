package com.example.app.pre_login_UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.StudentProfileNavDrawerJohn;

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
                if(email.length() == 0 && password.length() == 0)
                    showToast("Email and Password are blank");
                else if(email.equals("Secret"))
                    openProfileSecret();
                else if(email.length() == 0)
                    showToast("Email is blank");
                else if(password.length() < 4)
                    showToast("Password needs to have at least 4 characters");
                else if(email.length() < 8 || !email.substring(email.length()-8).equals("@usc.edu"))
                    showToast("Invalid Email");
                else
                {
                    //don't put info in database yet, keep passing values until completed
                    //go to next activity
                    openNameStudent(email, password);
                }
            }
    });

    }
    private void showToast(String text)
    {
        Toast.makeText(StudentSignUpStart.this, text, Toast.LENGTH_SHORT).show();
    }
    public void openNameStudent(String email, String password) {
        Intent i = new Intent(this, StudentEnterName.class);
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("password", password);
        i.putExtras(bundle);
        startActivity(i);
    }

    public void openProfileSecret() {
        Intent i = new Intent(this, StudentProfileNavDrawerJohn.class);
        startActivity(i);
    }
}