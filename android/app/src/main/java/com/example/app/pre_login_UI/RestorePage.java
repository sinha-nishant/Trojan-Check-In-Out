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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.app.R;
import com.example.app.account_UI.ManagerHome;
import com.example.app.account_UI.StudentProfile;
import com.example.app.firebaseDB.FbUpdate;
import com.example.app.log_create.LogInOut;

import javax.annotation.Nullable;

public class RestorePage extends AppCompatActivity {
    TextView emailTv;
    TextView pwTv;
    Button restoreBtn;
    ProgressBar pb;
    MutableLiveData<Boolean> restore_success=new MutableLiveData<>();
    AlertDialog alertDialog;
    private static Long id;
    String email;
    String pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_page);
        emailTv= findViewById(R.id.RestoreEmail);
        pwTv= findViewById(R.id.RestorePassword);
        restoreBtn= findViewById(R.id.RestoreBtn);
        pb= findViewById(R.id.progressRestore);
        pb.setVisibility(View.GONE);
        DialogInit();
        restoreMldInit();

    }

    public void RestoreAccount(View v){
        email= emailTv.getText().toString();
        if(email==null){
            email="";
        }
        pw= pwTv.getText().toString();
        if(pw==null){
            pw="";
        }
        FbUpdate.restore(email,pw,restore_success);
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
                        if(restore_success.getValue()){
                            if(id==0L){
                                openManager();
                            }
                            else{
                                openStudent();
                            }
                        }

                    }
                });
        alertDialog = builder.create();
    }

    public void restoreMldInit(){
        final Observer<Boolean> restore_obs = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean isSuccess){

                if(isSuccess){
                    pb.setVisibility(View.GONE);
                    LogInOut.SaveData(RestorePage.this,email,id);
                    alertDialog.setMessage("Succeeded in Restoring account");
                    alertDialog.show();
                }
                else{
                    pb.setVisibility(View.GONE);
                    alertDialog.setMessage("Invalid Credentials");
                    alertDialog.show();
                }
            }

        };
        restore_success.observe(this, restore_obs);
    }

    public void openStudent(){
        Intent i= new Intent(this, StudentProfile.class);
        startActivity(i);
    }
    public void openManager(){
        Intent i= new Intent(this, ManagerHome.class);
        startActivity(i);
    }
}