package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class QRScanTest extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private static final int RC_PERMISSION = 10;
    private boolean mPermissionGranted;
    private MutableLiveData<StudentAccount> studentMLD = new MutableLiveData<>();
    private MutableLiveData<Building> buildingMLD = new MutableLiveData<>();
    private MutableLiveData<Boolean> checkInMLD = new MutableLiveData<>();
    private MutableLiveData<Boolean> checkOutMLD = new MutableLiveData<>();
    private ProgressBar loadingCircle;
    private AlertDialog checkInOutMessage;
    private AlertDialog.Builder builder;
    private Result postScanResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_scan_test);
        builder = new AlertDialog.Builder(this);
        final Observer<Boolean> checkOutObserver = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean success){
                if(success){ //student is checked in  display checkin message
                    builder.setTitle("Check Out Success")
                            .setMessage("You are now checked out!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // do whatever reflects wireframe the best(such as switching pages
                                    goToStudentProfile();
                                }
                            });
                    checkInOutMessage=builder.create();
                    //stop loading bar
                    checkInOutMessage.show();
                }else { //wasn't able to check in student
                    builder.setTitle("Check Out Failure")
                            .setMessage("Something went wrong with our database. Please try again later.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // do whatever reflects wireframe the best(such as switching pages
                                    goToStudentProfile();
                                }
                            });
                    checkInOutMessage=builder.create();
                    //stop loading bar
                    checkInOutMessage.show();
                }

            }
        };
        checkOutMLD.observe(this, checkOutObserver);
        final Observer<Boolean> checkInObserver = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean success){
                if(success){ //student is checked in  display checkin message
                    builder.setTitle("Check In Success")
                            .setMessage("You are now checked in! Don't forget to check out once you leave the building.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // do whatever reflects wireframe the best(such as switching pages
                                    goToStudentProfile();
                                }
                            });
                    checkInOutMessage=builder.create();
                    //stop loading bar
                    checkInOutMessage.show();
                }else { //wasn't able to check in student
                    builder.setTitle("Check In Failure")
                            .setMessage("Something went wrong with our database. Please try again later.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // do whatever reflects wireframe the best(such as switching pages
                                    goToStudentProfile();
                                }
                            });
                    checkInOutMessage=builder.create();
                    //stop loading bar
                    checkInOutMessage.show();
                }

            }
        };
        checkInMLD.observe(this,checkInObserver);
        final Observer<Building> buildingObserver = new Observer<Building>(){
            @Override
            public void onChanged(@Nullable final Building scannedBuilding){

                if(scannedBuilding.getOccupancy()<scannedBuilding.getCapacity()){ //if not null and scanned building occupancy isn't full then checkin and display checkin message
                    Date checkindate = new Date();
                    SharedPreferences sharedPreferences = getSharedPreferences(HassibTest.shared_pref,MODE_PRIVATE);
                    Long retrieveID = sharedPreferences.getLong(HassibTest.idEntry,0L);
                    StudentActivity sa = new StudentActivity(postScanResult.getText().toString(),checkindate,null );
                    FirebaseTest.checkIn(retrieveID,sa,checkInMLD);
                }else { //if not null and scanned building full then just display capacity full message with the capacity
                    builder.setTitle("Check In Failure")
                            .setMessage("The building has reached its full capacity of "+scannedBuilding.getCapacity().toString()+" students.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // do whatever reflects wireframe the best(such as switching pages
                                    goToStudentProfile();
                                }
                            });
                    checkInOutMessage=builder.create();
                    //stop loading bar
                    checkInOutMessage.show();
                }

            }
        };
        buildingMLD.observe(this,buildingObserver);

        final Observer<StudentAccount> studentAccountObserver = new Observer<StudentAccount>(){
            @Override
            public void onChanged(@Nullable final StudentAccount student){
                // check last index of studentactivity list
                 List<StudentActivity> sa_list = student.getActivity();
                 if(sa_list.isEmpty()) {//no activity so check in if occupancy isn't full
                     FirebaseTest.getBuilding(postScanResult.getText().toString(),buildingMLD);
                 }else{
                     //get last index which indicates most recent activity
                     StudentActivity  sa = sa_list.get(sa_list.size()-1);
                     if(sa.getCheckOutTime()==null && sa.getBuildingName().equals(postScanResult.getText().toString())){ //if null and scans same building then checkout student and display checkout success
                         //call checkout from checkout services or can call firebasecheckout directly
                         Date checkOutDate = new Date();
                         SharedPreferences sharedPreferences = getSharedPreferences(HassibTest.shared_pref,MODE_PRIVATE);
                         Long retrieveID = sharedPreferences.getLong(HassibTest.idEntry,0L);
                         FirebaseTest.checkOut(retrieveID,sa,checkOutDate,checkOutMLD);

                     }else if(sa.getCheckOutTime()==null && !sa.getBuildingName().equals(postScanResult.getText().toString()) ){ //if null and scans a different building then direct to check out of last building
                         //set values(such as message, and button to redirect if necessary) of alert dialog and show it to user
                         builder.setTitle("Check In Failure")
                                 .setMessage("Please check out of "+ sa.getBuildingName()+" before checking in again.")
                                 .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                     @Override
                                     public void onClick(DialogInterface dialog, int which) {
                                         // do whatever reflects wireframe the best(such as switching pages
                                         goToStudentProfile();
                                     }
                                 });
                         checkInOutMessage=builder.create();
                         //stop loading bar
                         checkInOutMessage.show();
                     }else if(sa.getCheckOutTime()!=null){
                         //would have to call getbuilding and send another callback
                         FirebaseTest.getBuilding(postScanResult.getText().toString(),buildingMLD);
                     }

                 }
            }
        };
        studentMLD.observe(this, studentAccountObserver);

        //code scanner stuff
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 10);
        }
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setAutoFocusEnabled(true);
        mCodeScanner.setTouchFocusEnabled(true);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {

            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Long uscID, StudentActivity sa, MutableLiveData<Boolean> success
                        SharedPreferences sharedPreferences = getSharedPreferences(HassibTest.shared_pref,MODE_PRIVATE);
                        Long retrieve_id = sharedPreferences.getLong(HassibTest.idEntry,0L);
                        postScanResult=result;
                         FirebaseTest.search(retrieve_id, studentMLD);
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

    public void goToStudentProfile() {
        Intent i = new Intent(this, StudentProfile.class);
        startActivity(i);
    }
}