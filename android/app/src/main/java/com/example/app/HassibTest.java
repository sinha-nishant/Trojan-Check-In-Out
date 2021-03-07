package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
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

public class HassibTest extends AppCompatActivity {
    private EditText eEmail;
    private EditText ePassword;
    private Button eLogin;
    private EditText buildingName;
    private Button btnQrCode;
    private ImageView qrimage;
private ProgressBar circle_thing;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hassib_test);




        eEmail = findViewById(R.id.etEmail);
        ePassword = findViewById(R.id.etPassword);
        eLogin = findViewById(R.id.btnLogin);
        buildingName = findViewById(R.id.etBuildingName);
        btnQrCode = findViewById(R.id.btnLogin);
        qrimage = findViewById(R.id.imageView);
        circle_thing= findViewById(R.id.etprogressBar);

        Building build = new Building();
        ArrayList<Long> account_ids = new ArrayList<>();

        account_ids.add(9876543210L);
        account_ids.add(2642001000L);
        account_ids.add(8588804678L);

        build.setStudents(account_ids);

        build.getStudents(buildingName, circle_thing);

//        while(build.getStudentsAcc()==null){}
//        if(build.getStudentsAcc()==null){
//            buildingName.setText("returned null");
//        }else{
//            buildingName.setText(build.getStudentsAcc().toString());
//        }

        eLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String inputEmail = eEmail.getText().toString();
                String inputPass = ePassword.getText().toString();
                if(inputEmail.isEmpty() || inputPass.isEmpty())
                {
                    Toast.makeText(HassibTest.this, "Enter all fields with text", Toast.LENGTH_SHORT).show();
                }else{
                    LogInOut login = new LogInOut();
                    boolean success_login=login.LogIn(inputEmail,inputPass);
                }

            }
        });

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