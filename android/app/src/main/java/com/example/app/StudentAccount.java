package com.example.app;

import java.time.LocalDateTime;
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

    public boolean setMajor(String newMajor)
    {
        //Todo
        return true;

    }

    public void checkIN(StudentActivity act){
        //Todo
    }

    public void checkOut(String buildingName, LocalDateTime checkOutTime){
        //Todo
    }

    public Integer getID(){
        return ID;
    }

    public String getMajor(){
        return major;
    }


}
