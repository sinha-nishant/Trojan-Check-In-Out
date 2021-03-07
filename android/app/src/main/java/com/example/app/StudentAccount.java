package com.example.app;


import android.view.View;
import android.widget.ProgressBar;

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

    public Boolean delete(ProgressBar bar){

        Date time = new Date();
        if(this.activity!=null){
            int last= this.activity.size()-1;
            StudentActivity act= activity.get(last);
            if(act.getCheckOutTime()!=null){
                Boolean successCheckOut=checkOut(act.getBuildingName(),time,bar);
                if(successCheckOut==false){
                    return false;
                }
                else{
                    StudentActivity updatedLastActivity= this.activity.get(last);
                    updatedLastActivity.setCheckOutTime(time);
                    this.activity.set(last,updatedLastActivity);
                }
            }

        }
        //Todo
//        FirebaseTest.deleteAccount(this.email,bar);
        return deleteSuccess;


    }


    public boolean setMajor(String newMajor,ProgressBar bar)
    {

        //Todo
//        FirebaseTest.updateMajor(uscID,newMajor,bar);
        return majorSuccess;

    }

    public boolean checkIN(StudentActivity act,ProgressBar bar){

        //Todo
//        FirebaseTest.checkIn(uscID,act,bar);
        if(checkInSuccess==true){
            if(this.activity==null){
                this.activity = new ArrayList<StudentActivity>();
            }
            this.activity.add(act);
        }
       return checkInSuccess;

    }

    public boolean checkOut(String buildingName, Date checkOutTime,ProgressBar bar){

        //Todo
//        FirebaseTest.checkOut(uscID,buildingName,checkOutTime, bar);
        if(checkOutSuccess==true){
            int last= this.activity.size()-1;
            StudentActivity updatedLastActivity= this.activity.get(last);
            updatedLastActivity.setCheckOutTime(checkOutTime);
            this.activity.set(last,updatedLastActivity);
        }
        return checkOutSuccess;
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

    public static void setDeleteSuccess(Boolean outcome,ProgressBar bar) {
        deleteSuccess = outcome;
        bar.setVisibility(View.GONE);
        bar.stopNestedScroll();
    }
    public Boolean getDeleteSuccess(){
        return deleteSuccess;
    }
    public static void setMajorSuccess(Boolean outcome,ProgressBar bar) {
        majorSuccess = outcome;
        bar.setVisibility(View.GONE);
        bar.stopNestedScroll();
    }
    public Boolean getMajorSuccess(){
        return majorSuccess;
    }
    public static void setCheckInSuccess(Boolean outcome,ProgressBar bar) {
        checkInSuccess = outcome;
        bar.setVisibility(View.GONE);
        bar.stopNestedScroll();
    }
    public Boolean getCheckInSuccess(){
        return checkInSuccess;
    }

    public static void setCheckOutSuccess(Boolean outcome,ProgressBar bar) {
        checkOutSuccess = outcome;
        bar.setVisibility(View.GONE);
        bar.stopNestedScroll();
    }
    public Boolean getCheckOutSuccess(){
        return checkOutSuccess;
    }

}
