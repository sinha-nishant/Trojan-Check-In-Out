package com.example.app;


import android.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StudentAccount extends Account {
    private Long uscID;
    private String major;
    private List<StudentActivity> activity;
    private static Boolean majorSuccess;
    private static Boolean checkInSuccess;
    private static Boolean checkOutSuccess;

    public StudentAccount() {};

    public StudentAccount(String firstName, String lastName, String email, String password,String profilePicture, Boolean type) {
        super(firstName, lastName, email, password,profilePicture, type);
        activity= new ArrayList<StudentActivity>();

    }

    public StudentAccount(String firstName, String lastName, String email, String password,String profilePicture, Long uscID, String major, Boolean type) {
        super(firstName, lastName, email, password,profilePicture, type);
        this.uscID=uscID;
        this.major=major;
        activity= new ArrayList<StudentActivity>();
    }

    public StudentAccount(String firstName, String lastName, String email, String password,String profilePicture, Long uscID, String major, List<StudentActivity> activity, Boolean type) {
        super(firstName, lastName, email, password, profilePicture,type);
        this.activity = activity;
        this.uscID=uscID;
        this.major=major;
    }

    public StudentAccount(String firstName, String lastName, String email, String hashedPw, Long id, String major, Boolean isManager) {
        super(firstName, lastName, email, hashedPw,isManager);
        this.activity = new ArrayList<StudentActivity>();
        this.uscID=id;
        this.major=major;
    }

    public void delete(ProgressBar progressbar, AlertDialog alert){
        Date time = new Date();
        if(this.activity!=null){
            int last= this.activity.size()-1;
            StudentActivity act=activity.get(last);
            if(act.getCheckOutTime()==null){
                FirebaseTest.checkOut(uscID,act,time,progressbar,alert,this.email);
            }
            else{
                FirebaseTest.deleteAccount(email,progressbar,alert);
            }
        }
        else{
            FirebaseTest.deleteAccount(email,progressbar,alert);
        }



    }


    public void setMajor(String newMajor,ProgressBar progressBar,AlertDialog alert)
    {
        FirebaseTest.updateMajor(uscID,newMajor,progressBar,alert);

    }

    public void checkIN(StudentActivity act,ProgressBar progressbar,AlertDialog alert){
        FirebaseTest.checkIn(uscID,act,progressbar,alert);

    }

    public void checkOut(String buildingName, Date checkOutTime,ProgressBar progressbar,AlertDialog alert){
        int last= this.activity.size()-1;
        StudentActivity sa= this.activity.get(last);
        FirebaseTest.getBuilding(buildingName,progressbar,alert,uscID,sa,checkOutTime);

    }

    public Long getUscID(){
        return this.uscID;
    }

    public void setUscID(Long uscID){
        this.uscID = uscID;
    }

    public String getMajor(){
        return major;
    }

    public List<StudentActivity> getActivity() {
        return this.activity;
    }

    public String toString() {
        return this.getFirstName() + " " + this.getLastName() + " " + this.getUscID() + " " + this.getEmail();
    }

    public static void setDeleteSuccess(Boolean outcome,ProgressBar progressbar,AlertDialog alert) {
        deleteSuccess = outcome;
        progressbar.setVisibility(View.GONE);
        progressbar.stopNestedScroll();
        if(deleteSuccess ==true){
            alert.setMessage("Successfully deleted account");
        }
        else{
            alert.setMessage("Error. Could not delete account successfully");
        }
        alert.show();
    }
    public Boolean getDeleteSuccess(){
        return deleteSuccess;
    }
    public static void setMajorSuccess(Boolean outcome,ProgressBar progressbar,AlertDialog alert) {
        majorSuccess = outcome;
        progressbar.setVisibility(View.GONE);
        progressbar.stopNestedScroll();
        if(majorSuccess ==true){
            alert.setMessage("Successfully updated your major");
        }
        else{
            alert.setMessage("Error. Could not update major successfully");
        }
        alert.show();
    }
    public Boolean getMajorSuccess(){
        return majorSuccess;
    }
    public static void setCheckInSuccess(Boolean outcome,ProgressBar progressbar,AlertDialog alert) {
        checkInSuccess = outcome;
        progressbar.setVisibility(View.GONE);
        progressbar.stopNestedScroll();
        if(checkInSuccess ==true){
            alert.setMessage("Check in was successful");
        }
        else{
            alert.setMessage("Error. Could not successfully check you in");
        }
        alert.show();
    }
    public Boolean getCheckInSuccess(){
        return checkInSuccess;
    }

    public static void setCheckOutSuccess(Boolean outcome,ProgressBar progressbar,AlertDialog alert) {
        checkOutSuccess = outcome;
        progressbar.setVisibility(View.GONE);
        progressbar.stopNestedScroll();
        if(checkOutSuccess==true){
            alert.setMessage("Check out was successful");
        }
        else{
            alert.setMessage("Error. Could not successfully check you out");
        }
        alert.show();

    }
    public Boolean getCheckOutSuccess(){
        return checkOutSuccess;
    }

}
