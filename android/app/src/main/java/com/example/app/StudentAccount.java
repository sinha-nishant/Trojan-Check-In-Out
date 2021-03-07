package com.example.app;


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

    public Boolean delete(){
        //update with GraphQL
        Date time = new Date();
        if(this.activity!=null){
            int last= this.activity.size()-1;
            StudentActivity act= activity.get(last);
            if(act.getCheckOutTime()!=null){
                Boolean successCheckOut=checkOut(act.getBuildingName(),time);
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
        //update with GraphQL
        Boolean isSuccess=true;
        //isSuccess=Update.deleteAccount(this.email); //GraphQL function call
        return isSuccess;


    }


    public boolean setMajor(String newMajor)
    {
        // update with GraphQL
        Boolean isSuccess=true;
        this.major= newMajor;
        // isSuccess = Update.updateMajor(this.ID,  newMajor);
        return isSuccess;

    }

    public boolean checkIN(StudentActivity act){
        // updated to boolean so activity class can give pop up message for failure
        //update with GraphQL
        Boolean isSuccess = true;
        //isSuccess = CheckInOut.checkIn(this.ID, act);
        if(isSuccess==true){
            if(this.activity==null){
                this.activity = new ArrayList<StudentActivity>();
            }
            this.activity.add(act);
        }
       return isSuccess;

    }

    public boolean checkOut(String buildingName, Date checkOutTime){
        // updated to boolean so activity class can give pop up message for failure
        //update with GraphQL
        Boolean isSuccess = true;
        //isSuccess = CheckInOut.checkOut(this.ID, buildingName,checkOutTime);
        if(isSuccess==true){
            int last= this.activity.size()-1;
            StudentActivity updatedLastActivity= this.activity.get(last);
            updatedLastActivity.setCheckOutTime(checkOutTime);
            this.activity.set(last,updatedLastActivity);
        }
        return isSuccess;
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

}
