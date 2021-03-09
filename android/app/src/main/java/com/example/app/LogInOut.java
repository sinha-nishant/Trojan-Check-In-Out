package com.example.app;


import android.content.*;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LogInOut {
    private boolean login=false;
    public LogInOut(){}
    public void isCorrectLogin(boolean login_success){
        login=login_success;
    }
    public static void LogIn(String email, String password, ProgressBar loading_circle,Context con){
        //hash password ]
        //send hash password and email to firebase to authenticate
        // callback
            //if sucessfull store email and id if necessary with sharedpreferences and stop progress bar
            // got to next page(can't do until get in touch wit UI person)
            //if not sucessful present a popup that says username or password incorrect and stop progress bar
        String bcryptHashString = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        Log.d("TEST", "Hashed: " + bcryptHashString);
         FirebaseTest.authenticate(email,password,loading_circle,con);
    }
    public static void LogInSuccess(String email, ProgressBar loading_circle, Context con){

        //store email
        SaveData(con, email);
        //stop progress bar
        loading_circle.setVisibility(View.GONE);
        //switch page

    }
    public static void LogInFail(ProgressBar loading_circle){
        //stop progress bar
        loading_circle.setVisibility(View.GONE);
        //Generate popupmessage
        Log.d("Login Attempt: ", "Failed");

    }
    public static void SaveData(Context con,String userEmail){
        SharedPreferences sharedPreferences = con.getSharedPreferences(HassibTest.shared_pref,con.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(HassibTest.emailEntry,userEmail);
        editor.apply();
        LoadData(con);
    }
    public static void LoadData(Context con){
        SharedPreferences sharedPreferences = con.getSharedPreferences(HassibTest.shared_pref,con.MODE_PRIVATE);
        String test_retrieve_email = sharedPreferences.getString(HassibTest.emailEntry,"");
//        Log.d("Saved email is : ", test_retrieve_email);
    }
    public static void LogOut(Context con){
        //clear all sharedpreferences strings
        SaveData(con,"");
    }
}
