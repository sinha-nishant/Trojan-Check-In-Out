package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StudentEnterName extends AppCompatActivity {

    String fName, lName;
    EditText firstNameInput;
    EditText lastNameInput;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_enter_name);

        firstNameInput = (EditText) findViewById(R.id.studentFirstName);
        lastNameInput = (EditText) findViewById(R.id.studentLastName);

        //Get the bundle
        // Bundle bundle = getIntent().getExtras();

        //Extract the data…
        // String stuff = bundle.getString(“stuff”);

        submitButton = (Button) findViewById(R.id.nameButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fName = firstNameInput.getText().toString();
                lName = lastNameInput.getText().toString();

                //used for popups to user
                //showToast(email);
                //showToast(password);
                /*
                if(email.length() == 0 || password.length() == 0)
                    showToast("Email or Password is blank");
                else if(email.length() < 8 || !email.substring(email.length()-8).equals("@usc.edu"))
                    showToast("Invalid Email");
                else
                {
                    //don't put info in database yet, keep passing values until completed
                    //go to next activity
                    openNameStudent(email, password);
                }
                */
                openID("email", "pass");
            }
        });

    }
    private void showToast(String text)
    {
        Toast.makeText(StudentEnterName.this, text, Toast.LENGTH_SHORT).show();
    }
    public void openID(String email, String password) {
        Intent i = new Intent(this, StudentEnterID.class);
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("password", password);
        i.putExtras(bundle);
        startActivity(i);
    }
}