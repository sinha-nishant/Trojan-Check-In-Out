package com.example.app.log_create;


import android.content.*;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.app.firebaseDB.FbQuery;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LogInOut {
    private boolean login=false;
    public LogInOut(){}
    public void isCorrectLogin(boolean login_success){
        login=login_success;
    }
    public static void LogIn(String email, String password, MutableLiveData<Boolean> login_success ){
        //hash password ]
        //send hash password and email to firebase to authenticate
        // callback
            //if sucessfull store email and id if necessary with sharedpreferences and stop progress bar
            // got to next page(can't do until get in touch wit UI person)
            //if not sucessful present a popup that says username or password incorrect and stop progress bar
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
    //Todo
    //could we just store email and id as static variables here and just call it from here instead of writing load data everytime
}
