package com.example.app.log_create;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.app.firebaseDB.FbQuery;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LogInOut {
    public LogInOut(){}

    public static void LogIn(String email, String password, MutableLiveData<Boolean> login_success ){
        String bcryptHashString = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        Log.d("TEST", "Hashed: " + bcryptHashString);
        FbQuery.authenticate(email,password,login_success);
    }

    public static void SaveData(Context con,String userEmail, Long uscid){
        SharedPreferences sharedPreferences = con.getSharedPreferences("sharedPrefs",con.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email",userEmail);
        editor.putLong( "uscid",uscid);
        editor.apply();
//        LoadData(con);
    }
    public static void LoadData(Context con){
        SharedPreferences sharedPreferences = con.getSharedPreferences("sharedPrefs",con.MODE_PRIVATE);
        String test_retrieve_email = sharedPreferences.getString("email","");
        Log.d("Saved email is : ", test_retrieve_email);

        Long test_retrieve_id = sharedPreferences.getLong("uscid",0L);
        Log.d("Saved id is : ", test_retrieve_id.toString());

    }
    public static void LogOut(Context con){
        //clear all sharedpreferences strings
        SaveData(con,"",0L);
    }

    public static String getEmail(Context con){
        SharedPreferences sharedPreferences = con.getSharedPreferences("sharedPrefs",con.MODE_PRIVATE);
        String test_retrieve_email = sharedPreferences.getString("email","");
        return test_retrieve_email;
    }

    public static String getID(Context con){
        SharedPreferences sharedPreferences = con.getSharedPreferences("sharedPrefs",con.MODE_PRIVATE);
        Long test_retrieve_id = sharedPreferences.getLong("uscid",0L);
        return test_retrieve_id.toString();
    }
}
