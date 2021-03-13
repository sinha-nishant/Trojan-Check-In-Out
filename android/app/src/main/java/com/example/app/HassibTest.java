 package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;
import java.util.Observable;

import javax.annotation.Nullable;

 public class HassibTest extends AppCompatActivity {
    private EditText eEmail;
    private EditText ePassword;
    private Button eLogin;
    private EditText buildingName;
    private Button btnQrCode;
    private ImageView qrimage;
private ProgressBar circle_thing;
public static final String shared_pref = "sharedPrefs";
public static final String emailEntry = "email";
public static final String idEntry = "uscid";
public static Long uscid;
private MutableLiveData<Boolean> login_success = new MutableLiveData<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        SharedPreferences sharedPreferences = getSharedPreferences(HassibTest.shared_pref,MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString(HassibTest.emailEntry,"");
//        editor.putLong(HassibTest.idEntry,0L);
//        editor.apply();
//        sharedPreferences = getSharedPreferences(HassibTest.shared_pref,MODE_PRIVATE);
//        Long test_retrieve_id = sharedPreferences.getLong(HassibTest.idEntry,0);
//        if(test_retrieve_id!=0L){
//            setContentView(R.layout.activity_hassib_test);
//        }else{
//            setContentView(R.layout.activity_firebase_test);
//        }
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_firebase_test);

        setContentView(R.layout.activity_hassib_test);
//        LoadData();
        eEmail = findViewById(R.id.etEmail);
        ePassword = findViewById(R.id.etPassword);
        eLogin = findViewById(R.id.btnLogin);
        buildingName = findViewById(R.id.etBuildingName);
        btnQrCode = findViewById(R.id.btnLogin);
        qrimage = findViewById(R.id.imageView);
        circle_thing= findViewById(R.id.etprogressBar);
        circle_thing.setVisibility(View.GONE);

        final Observer<Boolean> b_ob = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean b){
                if(b){
                    //store email
                    SaveData();
                    //stop progress bar
                    circle_thing.setVisibility(View.GONE);
                    //switch page

                }else{
                    //stop progress bar
                    circle_thing.setVisibility(View.GONE);
                    //Generate popupmessage
                    Log.d("Login Attempt: ", "Failed");
                }
            }

        };
        login_success.observe(this, b_ob);
        eLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputEmail = eEmail.getText().toString();
                String inputPass = ePassword.getText().toString();
                if(inputEmail.isEmpty() || inputPass.isEmpty())
                {
                    Toast.makeText(HassibTest.this, "Enter all fields with text", Toast.LENGTH_SHORT).show();
                }else{
                    LogInOut.LogIn(inputEmail,inputPass, login_success);
                }

            }
        });


    }
     public  void SaveData(){
         SharedPreferences sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
         SharedPreferences.Editor editor = sharedPreferences.edit();
         editor.putString(emailEntry,eEmail.getText().toString());
         editor.putLong(idEntry,uscid);
         editor.apply();
         LoadData();
     }
    public void LoadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
        String test_retrieve_email = sharedPreferences.getString(emailEntry,"");
        Long test_retrieve_id = sharedPreferences.getLong(idEntry,0);
        //load id
        Log.d("Saved email is : ", test_retrieve_email);
        Log.d("Saved ID is : ", test_retrieve_id.toString());
    }
    public void QRCodeBtn(View view){

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try{
            BitMatrix bitMatrix = qrCodeWriter.encode(buildingName.getText().toString(), BarcodeFormat.QR_CODE, 200,200);
            Bitmap bitmap = Bitmap.createBitmap(200,200, Bitmap.Config.RGB_565);
            for(int i =0;i<200;i++){//go through all 200x200 pixels
                for(int y =0;y<200;y++){
                    bitmap.setPixel(i,y,bitMatrix.get(i,y)? Color.BLACK : Color.WHITE);
                }
            }
            qrimage.setImageBitmap(bitmap);

            buildingName.setText(bitmap.toString());
        } catch(Exception e){

        }

    }


}