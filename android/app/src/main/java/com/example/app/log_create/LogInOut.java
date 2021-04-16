package com.example.app.log_create;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import com.example.app.firebaseDB.FbQuery;

public class LogInOut {
    public LogInOut(){}

    public static void LogIn(String email, String password, MutableLiveData<Boolean> login_success ){
        FbQuery.authenticate(email,password,login_success);
    }

    public static void SaveData(Context con,String userEmail, Long uscid){
        SharedPreferences sharedPreferences = con.getSharedPreferences("sharedPrefs",con.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email",userEmail);
        editor.putLong( "uscid",uscid);
        editor.apply();
    }

    public static void LogOut(Context con){
        //clear all sharedpreferences strings
        SaveData(con,"",0L);
    }


}
