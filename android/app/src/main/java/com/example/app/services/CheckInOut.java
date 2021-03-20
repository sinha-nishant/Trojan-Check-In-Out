package com.example.app.services;

import androidx.lifecycle.MutableLiveData;

import com.example.app.firebaseDB.FbCheckInOut;
import com.example.app.users.StudentActivity;

import java.util.Date;

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
