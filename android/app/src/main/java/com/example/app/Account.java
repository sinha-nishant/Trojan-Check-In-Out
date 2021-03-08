package com.example.app;

import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;

public class Account {
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String profilePicture;
    protected String password;
    protected Boolean isManager;
    protected static Boolean deleteSuccess;

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
        this.password= password;
        this.isManager=isManager;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public void setProfilePicture(String url){
        this.profilePicture= url;
    }

    public void delete(ProgressBar bar, Snackbar snackbar){
        //Todo
       FirebaseTest.deleteAccount(this.email,bar,snackbar);


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

    public static void setDeleteSuccess(Boolean outcome, ProgressBar bar,Snackbar snackbar) {
        deleteSuccess = outcome;
        bar.setVisibility(View.GONE);
        bar.stopNestedScroll();
        if(deleteSuccess ==true){
            snackbar.setText("Successfully deleted account");
        }
        else{
            snackbar.setText("Error. Could not delete account successfully");
        }
        snackbar.show();


    }

    public Boolean getDeleteSuccess(){
        return deleteSuccess;
    }
}
