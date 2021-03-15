package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

    private static Long id;

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
                        if(success!=null){
                            if(success.getValue()==true){
                                openProfileStudent();
                            }
                            
                        }
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
                    LogInOut.SaveData(LogInPage.this,email,id);






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

                //BACKEND: IF THIS EMAIL IS A STUDENT ACCOUNT,
//                openProfileStudent();
                //MARKUS: Manager Profile here
            }
        });

    }
    private void showToast(String text)
    {
        Toast.makeText(LogInPage.this, text, Toast.LENGTH_SHORT).show();
    }

    public void openProfileStudent() {
        Intent i = new Intent(this, StudentProfile.class);
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        i.putExtras(bundle);
        startActivity(i);
    }

    public static void setId(Long uscid){
        id=uscid;
    }


}