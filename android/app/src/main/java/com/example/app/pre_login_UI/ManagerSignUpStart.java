package com.example.app.pre_login_UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.app.R;

public class ManagerSignUpStart extends AppCompatActivity {

    String email, password;
    EditText emailInput;
    EditText passwordInput;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_sign_up_start);


        emailInput = (EditText) findViewById(R.id.managerSignUpEmailAddress);
        passwordInput = (EditText) findViewById(R.id.managerSignUpPassword);

        submitButton = (Button) findViewById(R.id.managerEmailPassSubmitButton);
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
                    openNameManager(email, password);
                }
            }
        });

    }
    private void showToast(String text)
    {
        Toast.makeText(ManagerSignUpStart.this, text, Toast.LENGTH_SHORT).show();
    }

    public void openNameManager(String email, String password) {
        Intent i = new Intent(this, ManagerName.class);
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("password", password);
        i.putExtras(bundle);
        startActivity(i);
    }
}