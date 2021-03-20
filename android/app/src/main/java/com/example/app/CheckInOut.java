package com.example.app;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class CheckInOut {
    public static void checkIn(MutableLiveData<Boolean> checkInMLD, String scanResult, Long uscId){
        Date checkindate = new Date();
        StudentActivity sa = new StudentActivity(scanResult,checkindate,null );
        FbCheckInOut.checkIn(uscId,sa,checkInMLD);
    }
    public static void checkOut(MutableLiveData<Boolean> checkOutMLD, StudentActivity sa,Long uscId){
        Date checkOutDate = new Date();
        FbCheckInOut.checkOut(uscId,sa,checkOutDate,checkOutMLD);
    }
}
