package com.example.app;


import androidx.lifecycle.MutableLiveData;

public class UpdatePassword {

    public UpdatePassword(String email, String newPassword, MutableLiveData<Boolean> success){
        FirebaseTest.updatePassword(email,newPassword, success);
    }
}