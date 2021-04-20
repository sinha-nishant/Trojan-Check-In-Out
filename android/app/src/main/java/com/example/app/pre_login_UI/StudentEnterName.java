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
        Bundle bundle = getIntent().getExtras();

        //Extract the data…
        String email = bundle.getString("email");
        String password = bundle.getString("password");

        submitButton = (Button) findViewById(R.id.nameButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fName = firstNameInput.getText().toString();
                lName = lastNameInput.getText().toString();

                if(fName.length() == 0 && lName.length() == 0)
                    showToast("First and last name are blank");
                else if(fName.length() == 0 )
                    showToast("First name is blank");
                else if(lName.length() == 0 )
                    showToast("Last name is blank");
                else if(lName.equals("Secret"))
                    openProfileSecret();
                else
                {
                    //don't put info in database yet, keep passing values until completed
                    //go to next activity
                    openID(email, password, fName, lName);
                }
            }
        });

    }
    private void showToast(String text)
    {
        Toast.makeText(StudentEnterName.this, text, Toast.LENGTH_SHORT).show();
    }
    public void openID(String email, String password, String fName, String lName) {
        Intent i = new Intent(this, StudentEnterID.class);
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("password", password);
        bundle.putString("fName", fName);
        bundle.putString("lName", lName);
        i.putExtras(bundle);
        startActivity(i);
    }
    public void openProfileSecret() {
        Intent i = new Intent(this, StudentProfileNavDrawerJohn.class);
        startActivity(i);
    }
}