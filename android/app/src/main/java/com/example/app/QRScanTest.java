package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import java.util.Date;

import javax.annotation.Nullable;

public class QRScanTest extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private static final int RC_PERMISSION = 10;
    private boolean mPermissionGranted;
    private MutableLiveData<Boolean> qrcode = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_scan_test);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 10);

        }
        CodeScannerView scannerView = findViewById(R.id.scanner_view);


        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setAutoFocusEnabled(true);
        mCodeScanner.setTouchFocusEnabled(true);
        final Observer<Boolean> b_ob = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean b){
                if(b){
                    Toast.makeText(QRScanTest.this, "OPERATION SUCESSS", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(QRScanTest.this, "SERVER ERROR ", Toast.LENGTH_SHORT).show();
                }
            }

        };
        qrcode.observe(this, b_ob);




            mCodeScanner.setDecodeCallback(new DecodeCallback() {

            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Long uscID, StudentActivity sa, MutableLiveData<Boolean> success
                        scannerView.removeAllViews();
                        Date checkindate = new Date();
                        StudentActivity sa = new StudentActivity(result.getText().toString(),checkindate,null );
                        FirebaseTest.checkIn(1L,sa,qrcode);
                        //can be check in or check out
                        //get student and check last index of studentactivity list
                        //if scans building that they are already checked into then check out

                        //if scans building and they are not checked into that building and not checked into another building then check in
                        //if scans building and is not checked  into that building and checked into another building, don't check in, display message to check out of last building
                       // Toast.makeText(QRScanTest.this, result.getText(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        mCodeScanner.setErrorCallback(error -> runOnUiThread(
                () -> Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = false;
                requestPermissions(new String[] {Manifest.permission.CAMERA}, RC_PERMISSION);
            } else {
                mPermissionGranted = true;
            }
        } else {
            mPermissionGranted = true;
        }
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == RC_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true;
                mCodeScanner.startPreview();
            } else {
                mPermissionGranted = false;
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }
}