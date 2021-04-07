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
        this.checkInTime = checkInTime;
    }

    public void setCheckOutTime(Date checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String toString()
    {
        SimpleDateFormat format1 = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a z");
        String checkIn = format1.format(this.checkInTime);
        String checkOut = "none";
        if(checkOutTime!=null){
            checkOut = format1.format(this.checkOutTime);
        }
        return "\n" + this.buildingName + "\n\tCheck-in: " + checkIn + "\n\tCheck-out: " + checkOut +"\n";
    }
}
