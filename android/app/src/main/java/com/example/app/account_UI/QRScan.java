package com.example.app.account_UI;

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
import com.example.app.R;
import com.example.app.building.Building;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.services.CheckInOut;
import com.example.app.users.StudentAccount;
import com.example.app.users.StudentActivity;
import com.google.zxing.Result;

import java.util.List;

import javax.annotation.Nullable;

public class QRScan extends AppCompatActivity {
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

    private String email, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_scan_test);
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs",MODE_PRIVATE);
        Long retrieveID = sharedPreferences.getLong("uscid",0L);
        email = sharedPreferences.getString("email","");
        id = retrieveID.toString();
        loadingCircle = findViewById(R.id.progresBar);
        loadingCircle.setVisibility(View.INVISIBLE);
        builder = new AlertDialog.Builder(this);
        final Observer<Boolean> checkOutObserver = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean success){
                if(success){ //student is checked in  display checkin message
                    setBuilder("Check Out Success", "You are now checked out!");
                }else { //wasn't able to check in student
                    setBuilder("Check Out Failure", "Something went wrong with our database. Please try again later.");
                }

            }
        };
        checkOutMLD.observe(this, checkOutObserver);
        final Observer<Boolean> checkInObserver = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean success){
                if(success){ //student is checked in  display checkin message
                    setBuilder("Check In Success", "You are now checked in! Don't forget to check out once you leave the building.");
                }else { //wasn't able to check in student
                    setBuilder("Check In Failure","Something went wrong with our database. Please try again later.");
                }

            }
        };
        checkInMLD.observe(this,checkInObserver);
        final Observer<Building> buildingObserver = new Observer<Building>(){
            @Override
            public void onChanged(@Nullable final Building scannedBuilding){
                if(scannedBuilding.getOccupancy()<scannedBuilding.getCapacity()){ //if not null and scanned building occupancy isn't full then checkin and display checkin message
                    CheckInOut.checkIn(checkInMLD,postScanResult.getText().toString(),retrieveID);
                }else { //if not null and scanned building full then just display capacity full message with the capacity
                   setBuilder("Check In Failure","The building has reached its full capacity of "+scannedBuilding.getCapacity().toString()+" students.");
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
                     FbQuery.getBuilding(postScanResult.getText().toString(),buildingMLD);
                 }else{
                     //get last index which indicates most recent activity
                     StudentActivity  sa = sa_list.get(sa_list.size()-1);
                     if(sa.getCheckOutTime()==null && sa.getBuildingName().equals(postScanResult.getText().toString())){ //if null and scans same building then checkout student and display checkout success
                         //call checkout from checkout services or can call firebasecheckout directly
                         CheckInOut.checkOut(checkOutMLD,sa,retrieveID);
                     }else if(sa.getCheckOutTime()==null && !sa.getBuildingName().equals(postScanResult.getText().toString()) ){ //if null and scans a different building then direct to check out of last building
                         //set values(such as message, and button to redirect if necessary) of alert dialog and show it to user
                         setBuilder("Check In Failure","Please check out of "+ sa.getBuildingName()+" before checking in again.");
                     }else if(sa.getCheckOutTime()!=null){
                         //would have to call getbuilding and send another callback
                         FbQuery.getBuilding(postScanResult.getText().toString(),buildingMLD);
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
                Log.d("IT WORKSSSSSSS","DECODEDD");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Long uscID, StudentActivity sa, MutableLiveData<Boolean> success
                        Log.d("IT WORKSSSSSSS","DECODEDD");

                        loadingCircle.setVisibility(View.VISIBLE);
                        postScanResult=result;
                        FbQuery.search(retrieveID, studentMLD);
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

    public void setBuilder(String title, String message){
        loadingCircle.setVisibility(View.GONE);
        builder.setTitle(title)
                .setMessage(message)
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

    public void goToStudentProfile() {
        Intent i = new Intent(this, StudentProfile.class);
        startActivity(i);
    }
    public CodeScanner getmCodeScanner(){
        return mCodeScanner;
    }
}