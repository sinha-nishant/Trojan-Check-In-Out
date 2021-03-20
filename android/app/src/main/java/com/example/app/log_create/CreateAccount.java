package com.example.app.log_create;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.app.users.Account;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.users.StudentAccount;

import java.io.InputStream;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class CreateAccount {

    public CreateAccount(){

    }

    public static void CreateManager(String firstName, String lastName, String email, String pw, MutableLiveData<Integer> create_success){
        Log.i("CreateAccount","in create account");

        String hashedPw = BCrypt.withDefaults().hashToString(12, pw.toCharArray());
        Account a= new Account(firstName,lastName,email,hashedPw,true);
        FbQuery.checkEmailExists(email,a,create_success,true);


    }

    public static void CreateStudent(String firstName, String lastName, String email, String pw,Long id, String major,MutableLiveData<Integer> success){
        Log.i("CreateAccount","in create account");

        String hashedPw = BCrypt.withDefaults().hashToString(12, pw.toCharArray());
        Account a = new StudentAccount(firstName,lastName,email,hashedPw,id,major,false);
        FbQuery.checkEmailExists(email,a,success,false);

    }
    public static void CreateManager(String firstName, String lastName, String email, String pw, InputStream url, MutableLiveData<Integer> success){
        Log.i("CreateAccount","in create account");

        String picUrl=AWSLink(email);
        String hashedPw = BCrypt.withDefaults().hashToString(12, pw.toCharArray());
        Account a = new Account(firstName,lastName,email,hashedPw,picUrl,true);
        FbQuery.checkEmailExists(email,success,a,url,true);

    }

    public static void CreateStudent(String firstName, String lastName, String email, String pw,InputStream url,Long id, String major,MutableLiveData<Integer> success){
        Log.i("CreateAccount","in create account");

        String picUrl=AWSLink(email);
        String hashedPw = BCrypt.withDefaults().hashToString(12, pw.toCharArray());
        Account a = new StudentAccount(firstName,lastName,email,hashedPw,picUrl,id,major,true);
        FbQuery.checkEmailExists(email,success,a,url,false);

    }

    public static String AWSLink(String email){
        String domain="https://trojan-check-in-and-out183928-dev173416-dev.s3-us-west-2.amazonaws.com/public/";
        return domain+email.replaceFirst("@","%40");
    }


}
