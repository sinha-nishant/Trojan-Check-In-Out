package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;

public class ManagerName extends AppCompatActivity {

    String fName, lName;
    EditText firstNameInput;
    EditText lastNameInput;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_name);


        firstNameInput = (EditText) findViewById(R.id.managerFirstName);
        lastNameInput = (EditText) findViewById(R.id.managerLastName);

        //Get the bundle
        Bundle bundle = getIntent().getExtras();

        //Extract the data…
        String email = bundle.getString("email");

        submitButton = (Button) findViewById(R.id.nameButtonM);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fName = firstNameInput.getText().toString();
                lName = lastNameInput.getText().toString();

                //used for popups to user
                //showToast(email);
                //showToast(password);
                if(fName.length() == 0 || lName.length() == 0)
                    showToast("First or last name is blank");
                else
                {
                    showToast(email);
                    //TODO Arjun: add data to S3
                    //TODO Markus: redirect to Manager Profile, sending email string in bundle
                }
            }
        });
    }

    private void showToast(String text)
    {
        Toast.makeText(ManagerName.this, text, Toast.LENGTH_SHORT).show();
    }
    public void AmplifyInit(){
        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());
        } catch (AmplifyException e) {
            Log.i("MyAmplifyApp", "could not add plugins ");
        }
    }

}