package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import javax.annotation.Nullable;

public class LogInPage extends AppCompatActivity {

    String email, password;
    EditText emailInput;
    EditText passwordInput;
    Button submitLoginButton;
    MutableLiveData<Boolean> success=new MutableLiveData<>();
    ProgressBar studentProgress;
    AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_page);


        emailInput = (EditText) findViewById(R.id.editTextEmailLogin);
        passwordInput = (EditText) findViewById(R.id.editTextPasswordLogin);

        submitLoginButton = (Button) findViewById(R.id.btnLogin2);

        studentProgress = (ProgressBar)findViewById(R.id.progressBar2);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Status of Action");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes",
                new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {

                        //change to different page
                    }
                });
        alertDialog = builder.create();
        final Observer<Boolean> obs = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean b){

                if(b){
                    studentProgress.setVisibility(View.GONE);
                    alertDialog.setMessage("Succeeded in Logging In");
                    alertDialog.show();




                }
                else{
                    studentProgress.setVisibility(View.GONE);
                    alertDialog.setMessage("Invalid Credentials");
                    alertDialog.show();

                }
            }

        };
        success.observe(this, obs);
        submitLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailInput.getText().toString();
                password = passwordInput.getText().toString();
                LogInOut.LogIn(email,password,success);
//                if(true) //do some backend checking here
//                {
//                    //go to next activity
//                    showToast("Login verified and suceeded");
//                }
//                else if(false)
//                {
//                    showToast("You are not a registered user. Please sign up first");
//                }

            }
        });

    }
    private void showToast(String text)
    {
        Toast.makeText(LogInPage.this, text, Toast.LENGTH_SHORT).show();
    }
}