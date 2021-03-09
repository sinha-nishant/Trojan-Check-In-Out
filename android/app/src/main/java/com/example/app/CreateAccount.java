package com.example.app;

import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;

import java.io.InputStream;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class CreateAccount {
    public static Boolean emailAccepted=true;//possible error with firebase connection
    public static Boolean createAccountAccepted=true;
    public CreateAccount(String firstName, String lastName, String email, String pw, Boolean isManager, ProgressBar bar, AlertDialog alert){
        Log.i("CreateAccount","in create account");

        String hashedPw = BCrypt.withDefaults().hashToString(12, pw.toCharArray());
        Account a= new Account(firstName,lastName,email,hashedPw,isManager);
        FirebaseTest.checkEmailExists(email,bar,alert,a);


    }
    public CreateAccount(String firstName, String lastName, String email, String pw, Boolean isManager,Long id, String major,ProgressBar bar,AlertDialog alert){
        Log.i("CreateAccount","in create account");

        String hashedPw = BCrypt.withDefaults().hashToString(12, pw.toCharArray());
        Account a = new StudentAccount(firstName,lastName,email,hashedPw,id,major,isManager);
        FirebaseTest.checkEmailExists(email,bar,alert,a);

    }
    public CreateAccount(String firstName, String lastName, String email, String pw,InputStream url, Boolean isManager, ProgressBar bar,AlertDialog alert){
        Log.i("CreateAccount","in create account");

        String picUrl=AWSLink(email);
        String hashedPw = BCrypt.withDefaults().hashToString(12, pw.toCharArray());
        Account a = new StudentAccount(firstName,lastName,email,hashedPw,picUrl,isManager);
        FirebaseTest.checkEmailExists(email,bar,alert,a,url);

    }

    public CreateAccount(String firstName, String lastName, String email, String pw,InputStream url, Boolean isManager,Long id, String major,ProgressBar bar,AlertDialog alert){
        Log.i("CreateAccount","in create account");

        String picUrl=AWSLink(email);
        String hashedPw = BCrypt.withDefaults().hashToString(12, pw.toCharArray());
        Account a = new StudentAccount(firstName,lastName,email,hashedPw,picUrl,id,major,isManager);
        FirebaseTest.checkEmailExists(email,bar,alert,a,url);

    }

    public String AWSLink(String email){
        String domain="https://trojan-check-in-and-out183928-dev173416-dev.s3-us-west-2.amazonaws.com/public/";
//        String key= email+".png";
        return domain+email.replaceFirst("@","%40");
    }
    public static void setEmailAccepted(Boolean accepted,ProgressBar bar) {
        emailAccepted = accepted;
        bar.setVisibility(View.GONE);
        bar.stopNestedScroll();
    }

    public static void setCreateAccountAccepted(Boolean accepted,ProgressBar progressbar,AlertDialog alert) {
        createAccountAccepted = accepted;
        progressbar.setVisibility(View.GONE);
        progressbar.stopNestedScroll();
        if(createAccountAccepted==true){
            alert.setMessage("Your account was successfully created");
        }
        else{
            alert.setMessage("Error. Could not create your account successfully");
        }
        alert.show();
    }


    public Boolean getEmailAccepted(){
        return emailAccepted;
    }
    public Boolean getCreateAccountAccepted(){
        return createAccountAccepted;
    }

}
