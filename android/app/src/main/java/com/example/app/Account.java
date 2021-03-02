package com.example.app;

public class Account {
    private String firstName;
    private String lastName;
    private String email;
    private String profilePicture;
    private String password;
    private Boolean isManager;

    public Account(String firstName, String lastName, String email, String password, Boolean type){
        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.password= password;
        this.isManager=type;
    }
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public void setProfilePicture(){
        //Todo
    }

    public void delete(){
        //Todo
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




}
