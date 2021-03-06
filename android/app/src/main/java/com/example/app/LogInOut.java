package com.example.app;


import android.util.Log;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LogInOut {
    public LogInOut(){

    }
    public boolean LogIn(String email, String password){
        String bcryptHashString = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        Log.d("TEST", "Hashed: " + bcryptHashString);
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), bcryptHashString);
        Log.d("TEST", result.toString());

// result.verified == true
        return true;
    }
    public void LogOut(){

    }
}
