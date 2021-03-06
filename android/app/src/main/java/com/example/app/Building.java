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

    public Building(String building_name, Integer max_capacity, Integer current_occupancy,List<Integer> students_in_building){
        name=building_name;
        capacity=max_capacity;
        occupancy=current_occupancy;
        if(students_in_building!=null){
            students=students_in_building;
        }
        else{
            students = new ArrayList<Integer>();
        }




    }
    public boolean setCapacity(int newCapacity){
        return true;
    }
    public boolean checkIn(StudentAccount student){
        return true;
    }
    public boolean checkOut(StudentAccount student){
        return true;
    }
    public String getName(){
        return name;
    }
    public Integer getCapacity(){
        return capacity;
    }
    public Integer getOccupancy(){
        return occupancy;
    }
    public List<StudentAccount> getStudents(){
        List<StudentAccount> students_from_firebase = new ArrayList<StudentAccount>();
        if(!students.isEmpty()){//if we have list of ids then extract students from firebase
           students_from_firebase = FirebaseTest.getStudents(students);
        }
        return students_from_firebase;
    }
    public String getQrCodeURL(){
        return qrCodeURL;
    }

}
