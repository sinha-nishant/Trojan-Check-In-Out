package com.example.app;



import android.graphics.Bitmap;
import android.graphics.Color;
import android.nfc.cardemulation.HostApduService;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.lifecycle.MutableLiveData;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;
import java.util.List;
interface Test{
    public void callback(boolean didCheckIn);
}
public class Building {
    private String name;
    private Integer capacity;
    private Integer occupancy;
    private List<Long> students_ids;//list of uscId
    private String qrCodeURL;

    public Building(String building_name, Integer max_capacity, Integer current_occupancy,String image_location ,List<Long> students_in_building){
        name=building_name;
        capacity=max_capacity;
        occupancy=current_occupancy;
        qrCodeURL=image_location;
        if(students_in_building!=null){
            students_ids=students_in_building;
        }
        else{
            students_ids = new ArrayList<Long>();
        }
    }
    public Building(){}

    //getters
    public String getName(){
        return name;
    }
    public Integer getCapacity(){
        return capacity;
    }
    public Integer getOccupancy(){
        return occupancy;
    }
    public String getQrCodeURL(){
        return qrCodeURL;
    }
    public List<StudentAccount> getStudents(){

        return new ArrayList<StudentAccount>();
    }
    public List<Long> getStudents_ids(){
        return this.students_ids;
    }
    //setters

    public void setName(String name){
        this.name=name;
    }
    public void setCapacity(Integer capacity){
        this.capacity=capacity;
    }
    public void setOccupancy(Integer occupancy){
        this.occupancy=occupancy;
    }
    public void setQrCodeURL(String url){
        qrCodeURL=url;
    }
    public void setStudents_ids(List<Long> students_ids){
        this.students_ids=students_ids;
    }

//    public boolean checkIn(StudentAccount student, Test callback){
//        call firebase check in
//        maybe pass a callback that if sucess will do the following:
//            do whatever is necessary to send a pop up message with the capacity if full
//
//       Test callbackk = new Test(){
//           public void callback(Building b, StudentAccount s){
//               b.students_ids.add(123123123L);
//               b.students_accounts.add(s);
//
//
//               x= x+1;
//           }
//       };
//StudentActivity sa = new StudentActivity();
//       FirebaseTest.checkIn(student.getUscID(), sa, callbackk);
//        return true;
//    }
//    public boolean checkOut(StudentAccount student){
//        return true;
//    }
//
//    public String toString() {
//        Integer numStudents;
//        if (this.students_ids == null) {
//            numStudents = 0;
//        }
//        else {
//            numStudents = this.students_ids.size();
//        }
//        return this.name + " capacity: " + this.capacity + " current: " + numStudents;
//    }
}
