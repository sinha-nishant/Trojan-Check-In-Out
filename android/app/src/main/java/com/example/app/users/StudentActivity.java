package com.example.app.users;

import java.text.SimpleDateFormat;
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
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        String checkIn = format1.format(this.checkInTime);
        String checkOut = "none";
        if(checkOutTime!=null){
            checkOut = format1.format(this.checkOutTime);
        }
        return this.buildingName + "\n\tCheck In Time: " + checkIn + "\n\tCheck Out Time: " + checkOut +"\n";
    }
}
