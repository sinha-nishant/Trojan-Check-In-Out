package com.example.app.users;


import androidx.lifecycle.MutableLiveData;

import com.example.app.firebaseDB.FbCheckInOut;
import com.example.app.firebaseDB.FbUpdate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StudentAccount extends Account {
    private Long uscID;
    private String major;
    private List<StudentActivity> activity;


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


    public void delete(MutableLiveData<Integer> delete_success){
    Date time = new Date();
    if(this.activity!=null&&this.activity.size()!=0){
        int last= this.activity.size()-1;
        StudentActivity act=activity.get(last);
        if(act.getCheckOutTime()==null){
            FbCheckInOut.checkOut(uscID,act,time,delete_success,this.email,true);
        }
        else{
            FbUpdate.deleteAccount(email,delete_success);
        }
    }
    else{
        FbUpdate.deleteAccount(email,delete_success);
    }



}




    public void setMajor(String newMajor,MutableLiveData<Boolean> success)
    {
        FbUpdate.updateMajor(uscID,newMajor,success);

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
        return this.getFirstName() + " " + this.getLastName() + " " + this.getUscID() + " " + this.getEmail()+" "+this.getMajor()+" "
                +this.getPassword()+" "+this.getProfilePicture();
    }



}
