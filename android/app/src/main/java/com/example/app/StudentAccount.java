package com.example.app;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StudentAccount extends Account {
    private Integer ID;
    private String major;
    private List<StudentActivity> activity;


    public StudentAccount(String firstName, String lastName, String email, String password, Boolean type) {
        super(firstName, lastName, email, password, type);
    }

    public StudentAccount(String firstName, String lastName, String email, String password, Integer ID, String major, Boolean type) {
        super(firstName, lastName, email, password, type);
        this.ID=ID;
        this.major=major;
    }

    public Boolean delete(){
        //update with GraphQL
        LocalDateTime time= LocalDateTime.now();
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

    public boolean checkOut(String buildingName, LocalDateTime checkOutTime){
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

    public Integer getID(){
        return ID;
    }

    public String getMajor(){
        return major;
    }


}
