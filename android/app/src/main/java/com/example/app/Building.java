package com.example.app;



import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;
import java.util.List;

public class Building {
    private String name;
    private Integer capacity;
    private Integer occupancy;
    private List<Integer> students;//list of uscId
    private String qrCodeURL;

    public Building(String building_name, Integer max_capacity, Integer current_occupancy,String image_location ,List<Integer> students_in_building){
        name=building_name;
        capacity=max_capacity;
        occupancy=current_occupancy;
        qrCodeURL=image_location;
        if(students_in_building!=null){
            students=students_in_building;
        }
        else{
            students = new ArrayList<Integer>();
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
        List<StudentAccount> students_from_firebase = new ArrayList<StudentAccount>();
        if(!students.isEmpty()){//if we have list of ids then extract students from firebase
            students_from_firebase = FirebaseTest.getStudents(students);
        }
        return students_from_firebase;
    }
    //setters
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
    public void setStudents(List<Integer> student_ids){
        students=student_ids;
    }


    public boolean checkIn(StudentAccount student){
        return true;
    }
    public boolean checkOut(StudentAccount student){
        return true;
    }




}
