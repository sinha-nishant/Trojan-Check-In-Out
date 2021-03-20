package com.example.app.users;

import java.util.Date;

public class StudentActivity {
    private String buildingName;
    private Date checkInTime;
    private Date checkOutTime;

    StudentActivity() {};

    public StudentActivity(String name, Date in, Date out){
        buildingName=name;
        checkInTime=in;
        checkOutTime = out;
    }
    public StudentActivity(String name, Date in){
        buildingName=name;
        checkInTime=in;
        checkOutTime=null;
    }

    public String getBuildingName(){
        return buildingName;
    }

    public Date getCheckInTime(){
        return checkInTime;
    }

    public Date getCheckOutTime(){
        return checkOutTime;
    }

    public void setCheckInTime(Date checkInTime) {
        this.checkInTime = checkInTime; // do we need to edit checkout ?
    }

    public void setCheckOutTime(Date checkOutTime) {
        this.checkOutTime = checkOutTime; // do we need to edit checkout ?
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String toString()
    {
        if(checkOutTime==null){
            return this.buildingName + " (" + this.checkInTime.toString() + ")";
        }
        return this.buildingName + " (" + this.checkInTime.toString() + " - " + this.checkOutTime.toString() + ")";
    }
}
