package com.example.app;

import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.lifecycle.MutableLiveData;

import com.google.android.material.snackbar.Snackbar;

import java.io.InputStream;
import java.security.cert.Extension;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class CreateAccount {

    public CreateAccount(){

    }

    public static void Create(String firstName, String lastName, String email, String pw, Boolean isManager, MutableLiveData<Integer> create_success){
        Log.i("CreateAccount","in create account");

        String hashedPw = BCrypt.withDefaults().hashToString(12, pw.toCharArray());
        Account a= new Account(firstName,lastName,email,hashedPw,isManager);
        FirebaseTest.checkEmailExists(email,a,create_success,true);


    }

    public static void Create(String firstName, String lastName, String email, String pw, Boolean isManager,Long id, String major,MutableLiveData<Integer> success){
        Log.i("CreateAccount","in create account");

        String hashedPw = BCrypt.withDefaults().hashToString(12, pw.toCharArray());
        Account a = new StudentAccount(firstName,lastName,email,hashedPw,id,major,isManager);
        FirebaseTest.checkEmailExists(email,a,success,false);

    }
    public static void Create(String firstName, String lastName, String email, String pw, InputStream url, String Extension, Boolean isManager, MutableLiveData<Integer> success){
        Log.i("CreateAccount","in create account");

        String picUrl=AWSLink(email,Extension);
        String hashedPw = BCrypt.withDefaults().hashToString(12, pw.toCharArray());
        Account a = new StudentAccount(firstName,lastName,email,hashedPw,picUrl,isManager);
        FirebaseTest.checkEmailExists(email,success,a,url,Extension,true);

    }

    public static void Create(String firstName, String lastName, String email, String pw,InputStream url,String Extension ,Boolean isManager,Long id, String major,MutableLiveData<Integer> success){
        Log.i("CreateAccount","in create account");

        String picUrl=AWSLink(email,Extension);
        String hashedPw = BCrypt.withDefaults().hashToString(12, pw.toCharArray());
        Account a = new StudentAccount(firstName,lastName,email,hashedPw,picUrl,id,major,isManager);
        FirebaseTest.checkEmailExists(email,success,a,url,Extension,false);

    }

    public static String AWSLink(String email,String Extension){
        String domain="https://trojan-check-in-and-out183928-dev173416-dev.s3-us-west-2.amazonaws.com/public/";
        return domain+email.replaceFirst("@","%40")+Extension;
    }


}
