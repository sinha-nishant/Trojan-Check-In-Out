package com.example.app;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.InputStream;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class CreateAccount {
    public static Boolean emailAccepted=true;//possible error with firebase connection
    public static Boolean createAccountAccepted=true;
    public CreateAccount(String firstName, String lastName, String email, String pw, Boolean isManager, ProgressBar bar){
        Log.i("CreateAccount","in create account");

//        FirebaseTest.checkEmailExists(email); Nish: commented
//        check value of 'accepted'
        FirebaseTest.checkEmailExists(email,bar);

        if(emailAccepted==null){
            Log.i("progressbar","didnt work");
        }
        else{
            Log.i("worked","didnt work");
        }
        if(emailAccepted==false){
            return;
        }



//        String hashedPw = BCrypt.withDefaults().hashToString(12, pw.toCharArray());
//        Account a= new Account(firstName,lastName,email,hashedPw,isManager);
//        FirebaseTest.createAccount(a);
//        if(createAccountAccepted==false){
//            return;
//        }



//        Log.i("account",a.getFirstName());
//        Log.i("account",a.getLastName());
//        Log.i("account",a.getEmail());
//        Log.i("account",a.getPassword());
//
//        Log.i("account", String.valueOf(isManager));

    }
    public CreateAccount(String firstName, String lastName, String email, String pw, Boolean isManager,Long id, String major,ProgressBar bar){
        Log.i("CreateAccount","in create account");
//        FirebaseTest fb= new FirebaseTest();
//        fb.checkEmailExists(email);
//        FirebaseTest.checkEmailExists(email); Nish: commented
        FirebaseTest.checkEmailExists(email,bar);
        if(emailAccepted==null){
            Log.i("progressbar","didnt work");
        }
        else{
            Log.i("worked","didnt work");
        }
        if(emailAccepted==false){
            return;
        }
//        String picUrl=AWSLink(email);
        String hashedPw = BCrypt.withDefaults().hashToString(12, pw.toCharArray());
        Account a = new StudentAccount(firstName,lastName,email,hashedPw,id,major,isManager);
        FirebaseTest.createAccount(a);
        if(createAccountAccepted==false){
            return;
        }
//        check value of 'accepted'
//        uploadPhoto up= new uploadPhoto();
//
//        up.upload(url,email);
//        Log.i("upload", picUrl);
//
//        Log.i("account",a.getFirstName());
//        Log.i("account",a.getLastName());
//        Log.i("account",a.getEmail());
//        Log.i("account",a.getPassword());
//        Log.i("account",a.getProfilePicture());
//        Log.i("account", String.valueOf(((StudentAccount)a).getUscID()));
//        Log.i("account",((StudentAccount)a).getMajor());
//        Log.i("account", String.valueOf(isManager));




    }
    public CreateAccount(String firstName, String lastName, String email, String pw,InputStream url, Boolean isManager, ProgressBar bar){
        Log.i("CreateAccount","in create account");

//        FirebaseTest.checkEmailExists(email); Nish: commented
//        check value of 'accepted'
        FirebaseTest.checkEmailExists(email,bar);
        if(emailAccepted==null){
            Log.i("progressbar","didnt work");
        }
        else{
            Log.i("worked","didnt work");
        }
        if(emailAccepted==false){
            return;
        }

//        String picUrl=AWSLink(email);
//        String hashedPw = BCrypt.withDefaults().hashToString(12, pw.toCharArray());
//        Account a= new Account(firstName,lastName,email,hashedPw,picUrl,isManager);
//        FirebaseTest.createAccount(a);
//        if(createAccountAccepted==false){
//            return;
//        }

//        uploadPhoto up= new uploadPhoto();
//        up.upload(url,email);
//        Log.i("upload", picUrl);
//
//        Log.i("account",a.getFirstName());
//        Log.i("account",a.getLastName());
//        Log.i("account",a.getEmail());
//        Log.i("account",a.getPassword());
//        Log.i("account",a.getProfilePicture());
//        Log.i("account", String.valueOf(isManager));


    }

    public CreateAccount(String firstName, String lastName, String email, String pw,InputStream url, Boolean isManager,Long id, String major,ProgressBar bar){
        Log.i("CreateAccount","in create account");
//        FirebaseTest fb= new FirebaseTest();
//        fb.checkEmailExists(email);
//        FirebaseTest.checkEmailExists(email); Nish: commented
        FirebaseTest.checkEmailExists(email,bar);
        if(emailAccepted==null){
            Log.i("progressbar","didnt work");
        }
        else{
            Log.i("worked","didnt work");
        }
        if(emailAccepted==false){
            return;
        }
//        String picUrl=AWSLink(email);
//        String hashedPw = BCrypt.withDefaults().hashToString(12, pw.toCharArray());
//        Account a = new StudentAccount(firstName,lastName,email,hashedPw,picUrl,id,major,isManager);
//        FirebaseTest.createAccount(a);
//        if(createAccountAccepted==false){
//            return;
//        }
//        check value of 'accepted'
//        uploadPhoto up= new uploadPhoto();
//
//        up.upload(url,email);
//        Log.i("upload", picUrl);
//
//        Log.i("account",a.getFirstName());
//        Log.i("account",a.getLastName());
//        Log.i("account",a.getEmail());
//        Log.i("account",a.getPassword());
//        Log.i("account",a.getProfilePicture());
//        Log.i("account", String.valueOf(((StudentAccount)a).getUscID()));
//        Log.i("account",((StudentAccount)a).getMajor());
//        Log.i("account", String.valueOf(isManager));




    }

    public String AWSLink(String email){
        String domain="https://trojan-check-in-and-out183928-dev173416-dev.s3-us-west-2.amazonaws.com/public/";
        String key= email+".png";
        return domain+key.replaceFirst("@","%40");
    }
    public static void setEmailAccepted(Boolean accepted,ProgressBar bar) {
        emailAccepted = accepted;
        bar.setVisibility(View.GONE);
//        bar.stopNestedScroll();
    }

    public static void setCreateAccountAccepted(Boolean accepted) {
        createAccountAccepted = accepted;
    }

//    public Boolean getAccepted(){
//        return accepted;
//    }
    public Boolean getEmailAccepted(){
        return emailAccepted;
    }
    public Boolean getCreateAccountAccepted(){
        return createAccountAccepted;
    }

}
