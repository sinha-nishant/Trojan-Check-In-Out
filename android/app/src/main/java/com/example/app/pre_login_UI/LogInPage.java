package com.example.app.pre_login_UI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.app.R;
import com.example.app.account_UI.ManagerHome;
import com.example.app.account_UI.StudentProfile;
import com.example.app.log_create.LogInOut;

import javax.annotation.Nullable;

public class  LogInPage extends AppCompatActivity {

    private String email, password;
    private EditText emailInput,passwordInput;
    protected Button submitLoginButton;
    private final MutableLiveData<Boolean> success=new MutableLiveData<>();
    private ProgressBar pb;
    private AlertDialog alertDialog;
    private static Long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_page);


        emailInput = (EditText) findViewById(R.id.editTextEmailLogin);
        passwordInput = (EditText) findViewById(R.id.editTextPasswordLogin);

        submitLoginButton = (Button) findViewById(R.id.btnLogin2);

        pb = (ProgressBar)findViewById(R.id.progressBar2);
        pb.setVisibility(View.GONE);

        DialogInit();
        MutableInit();


        submitLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailInput.getText().toString();
                password = passwordInput.getText().toString();
                pb.setVisibility(View.VISIBLE);
                LogInOut.LogIn(email,password,success);

            }
        });

    }

    public void openProfileStudent() {
        Intent i = new Intent(this, StudentProfile.class);
        startActivity(i);



    }

    public void openProfileManager() {
        Intent i = new Intent(this, ManagerHome.class);
        startActivity(i);



    }

    public static void setId(Long uscid){
        id=uscid;
    }

    public void DialogInit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Login");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirm",
                new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {

                        //change to different page
                        if(success.getValue()!=null){
                            if(success.getValue()){
                                if(id==0){
                                    openProfileManager();
                                }
                                else{
                                    openProfileStudent();
                                }

                            }

                        }
                    }
                });
        alertDialog = builder.create();
    }

    public void MutableInit(){
        final Observer<Boolean> obs = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean isSuccess){
                if(isSuccess==null){
                    Log.d("LoginPage","success is null");
                    pb.setVisibility(View.GONE);
                    alertDialog.setMessage("Error processing login");
                    alertDialog.show();

                    return;
                }

                if(isSuccess){
                    pb.setVisibility(View.GONE);
                    LogInOut.SaveData(LogInPage.this,email,id);
                    alertDialog.setMessage("Succeeded in Logging In");
                    alertDialog.show();



                }
                else{
                    pb.setVisibility(View.GONE);
                    alertDialog.setMessage("Invalid Credentials");
                    alertDialog.show();

                }
            }

        };
        success.observe(this, obs);
    }


}