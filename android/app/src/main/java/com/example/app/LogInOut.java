package com.example.app;


import android.util.Log;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LogInOut {
    public LogInOut(){

    }
    public boolean LogIn(String email, String password){
        String bcryptHashString = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        Log.d("TEST", "Hashed: " + bcryptHashString);
        boolean isCorrect = true;/*DataRetriever.authenticate(email,bcryptHashString);*/
        return isCorrect;
    }
    public void LogOut(){

    }
}
