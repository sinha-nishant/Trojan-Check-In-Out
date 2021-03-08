package com.example.app;



import android.graphics.Bitmap;
import android.graphics.Color;
import android.nfc.cardemulation.HostApduService;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ProgressBar;

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
    private List<StudentAccount> students_accounts;//list of uscId
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
    public List<StudentAccount> getStudents(EditText buildingparam, ProgressBar circle){

        if(!this.students_ids.isEmpty()){//if we have list of ids then extract students from firebase
            Log.d("Inside getStudents", students_ids.toString());

            FirebaseTest.getStudents(this,students_ids,buildingparam,circle);

        }

        Log.d("Returning from getStudents", students_ids.toString());
        return this.students_accounts;
    }
    public List<StudentAccount> getStudentsAcc(){
        return this.students_accounts;
    }
    //setters
    public  void setAccounts(List<StudentAccount> new_accounts, EditText buildingparam, ProgressBar circle_thing){
        this.students_accounts=new_accounts;
        buildingparam.setText(this.students_accounts.toString());
        circle_thing.setVisibility(View.GONE);
        circle_thing.stopNestedScroll();

        Log.d("Inside SetAccount ", students_accounts.toString());

    }
    public void setName(String new_name){
        name=new_name;
    }
    public void setCapacity(int new_capacity){
        capacity=new_capacity;
    }
    public void setOccupancy(int new_occ){
        occupancy=new_occ;
    }
    public void setQrCodeURL(String url){
        qrCodeURL=url;
    }
    public void setStudents(List<Long> students){
        this.students_ids=students;
//        Log.d("Inside setStudents size is: ",((Integer) this.students.size()).toString());
    }

    public boolean checkIn(StudentAccount student, Test callback){
        //call firebase check in
        //maybe pass a callback that if sucess will do the following:
            //do whatever is necessary to send a pop up message with the capacity if full

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
        return true;
    }
    public boolean checkOut(StudentAccount student){
        return true;
    }

    public String toString() {
        Integer numStudents;
        if (this.students_ids == null) {
            numStudents = 0;
        }
        else {
            numStudents = this.students_ids.size();
        }
        return this.name + " capacity: " + this.capacity + " current: " + numStudents;
    }
}
