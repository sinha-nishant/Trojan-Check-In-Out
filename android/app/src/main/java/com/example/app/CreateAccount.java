package com.example.app;

import android.util.Log;

import java.io.InputStream;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class CreateAccount {
    public static Boolean emailAccepted=true;//possible error with firbase connection
    public static Boolean createAccountAccepted=true;
    public CreateAccount(String firstName, String lastName, String email, String pw, Boolean isManager){
        Log.i("CreateAccount","in create account");

        FirebaseTest.checkEmailExists(email);
//        check value of 'accepted'
        if(emailAccepted==false){
            return;
        }


        String hashedPw = BCrypt.withDefaults().hashToString(12, pw.toCharArray());
        Account a= new Account(firstName,lastName,email,hashedPw,isManager);
        FirebaseTest.createAccount(a);
        if(createAccountAccepted==false){
            return;
        }


//        Account a= new Account(firstName,lastName,email,pw,uri,isManager);
//        Account a= new Account(firstName,lastName,email,pw,"myPic",isManager);
        Log.i("account",a.getFirstName());
        Log.i("account",a.getLastName());
        Log.i("account",a.getEmail());
        Log.i("account",a.getPassword());
        Log.i("account",a.getProfilePicture());
        Log.i("account", String.valueOf(isManager));
//        FirebaseTest.createAccount(a);
//        accepted=true;
    }
    public CreateAccount(String firstName, String lastName, String email, String pw,InputStream url, Boolean isManager){
        Log.i("CreateAccount","in create account");

        FirebaseTest.checkEmailExists(email);
//        check value of 'accepted'
        if(emailAccepted==false){
            return;
        }

        String picUrl=AWSLink(email);
        String hashedPw = BCrypt.withDefaults().hashToString(12, pw.toCharArray());
        Account a= new Account(firstName,lastName,email,hashedPw,picUrl,isManager);
        FirebaseTest.createAccount(a);
        if(createAccountAccepted==false){
            return;
        }

        uploadPhoto up= new uploadPhoto();
        up.upload(url,email);
        Log.i("upload", picUrl);
//        Account a= new Account(firstName,lastName,email,pw,uri,isManager);
//        Account a= new Account(firstName,lastName,email,pw,"myPic",isManager);
        Log.i("account",a.getFirstName());
        Log.i("account",a.getLastName());
        Log.i("account",a.getEmail());
        Log.i("account",a.getPassword());
        Log.i("account",a.getProfilePicture());
        Log.i("account", String.valueOf(isManager));
//        FirebaseTest.createAccount(a);
//        accepted=true;

    }

    public CreateAccount(String firstName, String lastName, String email, String pw,InputStream url, Boolean isManager,Long id, String major){
        Log.i("CreateAccount","in create account");
//        FirebaseTest fb= new FirebaseTest();
//        fb.checkEmailExists(email);
        FirebaseTest.checkEmailExists(email);
        if(emailAccepted==false){
            return;
        }
        String picUrl=AWSLink(email);
        String hashedPw = BCrypt.withDefaults().hashToString(12, pw.toCharArray());
        Account a = new StudentAccount(firstName,lastName,email,hashedPw,picUrl,id,major,isManager);
        FirebaseTest.createAccount(a);
        if(createAccountAccepted==false){
            return;
        }
//        check value of 'accepted'
        uploadPhoto up= new uploadPhoto();

        up.upload(url,email);
        Log.i("upload", picUrl);

//        Account a = new StudentAccount(firstName,lastName,email,pw,uri,id,major,isManager);
//        Account a = new StudentAccount(firstName,lastName,email,pw,"uriTest",id,major,isManager);
        Log.i("account",a.getFirstName());
        Log.i("account",a.getLastName());
        Log.i("account",a.getEmail());
        Log.i("account",a.getPassword());
        Log.i("account",a.getProfilePicture());
        Log.i("account", String.valueOf(((StudentAccount)a).getUscID()));
        Log.i("account",((StudentAccount)a).getMajor());
        Log.i("account", String.valueOf(isManager));



//        fb.createAccount(a);
//        FirebaseTest.createAccount(a);

//        accepted=true;

    }

    public String AWSLink(String email){
        String domain="https://trojan-check-in-and-out183928-dev173416-dev.s3-us-west-2.amazonaws.com/public/";
        String key= email+".png";
        return domain+key.replaceFirst("@","%40");
    }
    public static void setEmailAccepted(Boolean accepted) {
        emailAccepted = accepted;
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
