package com.example.app.building;

import com.example.app.users.StudentAccount;

import java.util.ArrayList;
import java.util.List;

public class Building {
    private String name;
    private Integer capacity;
    private Integer occupancy;
    private List<Long> students_ids;//list of uscId
    private String qrCodeURL;

    public Building(String building_name, Integer max_capacity, Integer current_occupancy,String image_location ,List<Long> students_in_building){
        name=building_name;
        capacity=max_capacity;
        occupancy=current_occupancy;
        qrCodeURL=image_location;
        if(students_in_building!=null){
            students_ids=students_in_building;
        }
        else{
            students_ids = new ArrayList<Long>();
        }
    }
    public Building(){}

    //getters
    public String getName(){
        return name;
    }
    public Integer getCapacity(){
        return capacity;
    }
    public Integer getOccupancy(){
        return occupancy;
    }
    public String getQrCodeURL(){
        return qrCodeURL;
    }
    public List<StudentAccount> getStudents(){
        return new ArrayList<StudentAccount>();
    }
    public List<Long> getStudents_ids(){
        return this.students_ids;
    }
    public Boolean getAvailability(){return occupancy<capacity;}

    //setters
    public void setName(String name){
        this.name=name;
    }
    public void setCapacity(Integer capacity){
        this.capacity=capacity;
    }
    public void setOccupancy(Integer occupancy){
        this.occupancy=occupancy;
    }
    public void setQrCodeURL(String url){
        qrCodeURL=url;
    }
    public void setStudents_ids(List<Long> students_ids){
        this.students_ids=students_ids;
    }
}
