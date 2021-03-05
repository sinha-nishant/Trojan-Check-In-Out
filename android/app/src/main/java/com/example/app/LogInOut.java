package com.example.app;


import at.favre.lib.crypto.bcrypt.BCrypt;

public class LogInOut {
    public LogInOut(){

    }
    public boolean LogIn(String email, String Password){
        String password = "1234";
        String bcryptHashString = BCrypt.withDefaults().hashToString(12, password.toCharArray());


        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), bcryptHashString);

// result.verified == true
        return true;
    }
    public void LogOut(){

    }
}
