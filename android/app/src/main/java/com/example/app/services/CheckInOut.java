package com.example.app.services;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.app.building.Building;
import com.example.app.firebaseDB.FbCheckInOut;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.firebaseDB.FbUpdate;
import com.example.app.users.StudentAccount;
import com.example.app.users.StudentActivity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

public class CheckInOut {
    public static void checkIn(LifecycleOwner owner,MutableLiveData<Boolean> checkInMLD, String scanResult, Long uscId){
        Date checkindate = new Date();
        StudentActivity sa = new StudentActivity(scanResult,checkindate,null );
        //get building String buildingName, MutableLiveData<Building> buildingMLD)
        MutableLiveData<Building> buildingsMLD = new MutableLiveData<>();
        final Observer< Building> buildingsObserver = new Observer<Building>(){
            @Override
            public void onChanged(@Nullable final Building building){
                if(building.getCapacity()>building.getOccupancy()){
                    FbCheckInOut.checkIn(uscId,sa,checkInMLD);
                }else{
                    checkInMLD.setValue(false);
                }

            }
        };

        buildingsMLD.observe(owner,buildingsObserver);
        FbQuery.getBuilding(scanResult,buildingsMLD);
    }
    public static void checkOut(MutableLiveData<Boolean> checkOutMLD, StudentActivity sa,Long uscId){
        Date checkOutDate = new Date();
        FbCheckInOut.checkOut(uscId,sa,checkOutDate,checkOutMLD);
    }
    public static void canCheckIn(LifecycleOwner owner, MutableLiveData<Boolean> checkInMLD, String scanResult, Long uscId, Building b, StudentAccount student) {
        if (student == null) {
            if (b.getAvailability()) {
                checkIn(owner,checkInMLD, scanResult, uscId);
            }
        } else {
            List<StudentActivity> sa_list = student.getActivity();
            if (!sa_list.isEmpty()) {//no activity so check in if occupancy isn't full
                //get last index which indicates most recent activity
                StudentActivity sa = sa_list.get(sa_list.size() - 1);
                Boolean needsToCheckOut = sa.getCheckOutTime() == null && !sa.getBuildingName().equals(scanResult);
                if (b.getAvailability() && !needsToCheckOut) {
                    checkIn(owner,checkInMLD, scanResult, uscId);
                }
            }

        }
    }
}
