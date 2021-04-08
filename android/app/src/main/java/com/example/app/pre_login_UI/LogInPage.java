package com.example.app.pre_login_UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.app.account_UI.ManagerHome;
import com.example.app.R;
import com.example.app.account_UI.StudentProfile;
import com.example.app.log_create.LogInOut;

import javax.annotation.Nullable;

public class  LogInPage extends AppCompatActivity {

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
        studentProgress.setVisibility(View.GONE);

        DialogInit();
        MutableInit();


        submitLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailInput.getText().toString();
                password = passwordInput.getText().toString();
                studentProgress.setVisibility(View.VISIBLE);
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
                                if(id==0){
                                    openProfileManager();
                                }
                                else{
                                    openProfileStudent();
                                }

                                //need to check between accounts
                            }

                        }
                    }
                });
        alertDialog = builder.create();
    }

    public void MutableInit(){
        final Observer<Boolean> obs = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean b){

                if(b){
                    studentProgress.setVisibility(View.GONE);
                    LogInOut.SaveData(LogInPage.this,email,id);

                    studentProgress.setVisibility(View.GONE);
                    alertDialog.setMessage("Succeeded in Logging In");
                    Log.d("firstTest",alertDialog.toString());
                    alertDialog.show();



                }
                else{
                    studentProgress.setVisibility(View.GONE);
                    alertDialog.setMessage("Invalid Credentials");
                    Log.d("firstTest",alertDialog.toString());
                    alertDialog.show();

                }
            }

        };
        success.observe(this, obs);
    }


}