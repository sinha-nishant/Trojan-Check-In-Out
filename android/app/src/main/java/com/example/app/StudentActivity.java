package com.example.app;

import java.time.LocalDateTime;

public class StudentActivity {
    private String buildingName;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    public StudentActivity(String name, LocalDateTime in, LocalDateTime out){
        buildingName=name;
        checkInTime=in;
        checkOutTime = out;
    }
    public StudentActivity(String name, LocalDateTime in){
        buildingName=name;
        checkInTime=in;
        checkOutTime=null;
    }

    public String getBuildingName(){
        return buildingName;
    }

    public LocalDateTime getCheckInTime(){
        return checkInTime;
    }

    public LocalDateTime getCheckOutTime(){
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime; // do we need to edit checkout ?
    }
}
