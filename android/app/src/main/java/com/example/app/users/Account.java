package com.example.app.users;

import androidx.lifecycle.MutableLiveData;

import com.example.app.firebaseDB.FbUpdate;

public class Account {
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String profilePicture;
    protected String password;
    protected Boolean isManager;


    public Account() {};

    public Account(String firstName, String lastName, String email, String password,String profilePicture, Boolean type){
        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.password= password;
        this.profilePicture=profilePicture;
        this.isManager=type;
    }

    public Account(String firstName, String lastName, String email, String hashedPw, Boolean isManager) {
        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.password= hashedPw;
        this.isManager=isManager;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public void setProfilePicture(String url){
        this.profilePicture= url;
    }


    public void delete(MutableLiveData<Integer> delete_success){

        FbUpdate.deleteAccount(this.email,delete_success);


    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public String getEmail(){
        return email;
    }

    public String getProfilePicture(){
        return profilePicture;
    }

    public String getPassword(){
        return password;
    }

    public Boolean getIsManager(){
        return isManager;
    }

    public String toString() {
        return String.format(
                "First Name: %s\nLast Name: %s\nPassword: %s\nEmail: %s\nIsManager: %b\nProfile Pic: %s",
                this.getFirstName(),
                this.getLastName(),
                this.getPassword(),
                this.getEmail(),
                this.getIsManager(),
                this.getProfilePicture()
        );
    }
}
