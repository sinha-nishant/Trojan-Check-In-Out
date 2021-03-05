package com.example.app;

import android.util.Log;

import java.io.InputStream;

public class CreateAccount {
    public static Boolean accepted;
    public CreateAccount(String firstName, String lastName, String email, InputStream url, Boolean isManager){
        Log.i("CreateAccount","in create account");
//        FirebaseTest fb= new FirebaseTest();
//        fb.checkEmailExists(email);
//        check value of 'accepted'
        uploadPhoto up= new uploadPhoto();
        String uri= up.upload(url,email);
        Log.i("upload", uri);
        Account a= new Account(firstName,lastName,email,uri,isManager);
//        fb.createAccount(a);
        accepted=true;

    }

    public static void setAccepted(Boolean accepted) {
        accepted = accepted;
    }

    public Boolean getAccepted(){
        return accepted;
    }
}
