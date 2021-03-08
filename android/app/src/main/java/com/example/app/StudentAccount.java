package com.example.app;


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

    public void delete(ProgressBar progressbar, Snackbar snackbar){

        //Todo update when you understand how to retrieve building sync
//        Date time = new Date();
//        if(this.activity!=null){
//            int last= this.activity.size()-1;
//            StudentActivity act= activity.get(last);
//            if(act.getCheckOutTime()==null){
//                Boolean successCheckOut=checkOut(act.getBuildingName(),time,progressbar,snackbar);
//                if(successCheckOut==false){
//                    return false;
//                }
//                else{
//                    StudentActivity updatedLastActivity= this.activity.get(last);
//                    updatedLastActivity.setCheckOutTime(time);
//                    this.activity.set(last,updatedLastActivity);
//                }
//            }
//
//        }
        //Todo figure out how to sync the 2 firebase calls
        Date time = new Date();
        if(this.activity!=null){
            int last= this.activity.size()-1;
            StudentActivity act=activity.get(last);
            if(act.getCheckOutTime()==null){
                FirebaseTest.checkOut(uscID,act,time,progressbar,snackbar);
            }
        }
        FirebaseTest.deleteAccount(this.email,progressbar,snackbar);


    }


    public void setMajor(String newMajor,ProgressBar progressBar,Snackbar snackbar)
    {
        FirebaseTest.updateMajor(uscID,newMajor,progressBar,snackbar);

    }

    public void checkIN(StudentActivity act,ProgressBar progressbar,Snackbar snackbar){
        FirebaseTest.checkIn(uscID,act,progressbar,snackbar);

    }

    public void checkOut(String buildingName, Date checkOutTime,ProgressBar progressbar,Snackbar snackbar){

        //Todo  need to figure out how to retrieve building sync
//        FirebaseTest.getBuilding(buildingName,progressbar);
//        FirebaseTest.checkOut(uscID,building,checkOutTime, progressbar,snackbar);
        FirebaseTest.checkOut(uscID,null,checkOutTime, progressbar,snackbar);


//        if(checkOutSuccess==true){
//            int last= this.activity.size()-1;
//            StudentActivity updatedLastActivity= this.activity.get(last);
//            updatedLastActivity.setCheckOutTime(checkOutTime);
//            this.activity.set(last,updatedLastActivity);
//        }
//        return checkOutSuccess;
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

    public static void setDeleteSuccess(Boolean outcome,ProgressBar progressbar,Snackbar snackbar) {
        deleteSuccess = outcome;
        progressbar.setVisibility(View.GONE);
        progressbar.stopNestedScroll();
        if(deleteSuccess ==true){
            snackbar.setText("Successfully deleted account");
        }
        else{
            snackbar.setText("Error. Could not delete account successfully");
        }
        snackbar.show();
    }
    public Boolean getDeleteSuccess(){
        return deleteSuccess;
    }
    public static void setMajorSuccess(Boolean outcome,ProgressBar progressbar,Snackbar snackbar) {
        majorSuccess = outcome;
        progressbar.setVisibility(View.GONE);
        progressbar.stopNestedScroll();
        if(majorSuccess ==true){
            snackbar.setText("Successfully updated your major");
        }
        else{
            snackbar.setText("Error. Could not update major successfully");
        }
        snackbar.show();
    }
    public Boolean getMajorSuccess(){
        return majorSuccess;
    }
    public static void setCheckInSuccess(Boolean outcome,ProgressBar progressbar,Snackbar snackbar) {
        checkInSuccess = outcome;
        progressbar.setVisibility(View.GONE);
        progressbar.stopNestedScroll();
        if(checkInSuccess ==true){
            snackbar.setText("Check in was successful");
        }
        else{
            snackbar.setText("Error. Could not successfully check you in");
        }
        snackbar.show();
    }
    public Boolean getCheckInSuccess(){
        return checkInSuccess;
    }

    public static void setCheckOutSuccess(Boolean outcome,ProgressBar progressbar,Snackbar snackbar) {
        checkOutSuccess = outcome;
        progressbar.setVisibility(View.GONE);
        progressbar.stopNestedScroll();
        if(checkOutSuccess==true){
            snackbar.setText("Check out was successful");
        }
        else{
            snackbar.setText("Error. Could not successfully check you out");
        }
        snackbar.show();

    }
    public Boolean getCheckOutSuccess(){
        return checkOutSuccess;
    }

}
