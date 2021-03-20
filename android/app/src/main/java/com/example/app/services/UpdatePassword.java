package com.example.app.services;


import androidx.lifecycle.MutableLiveData;

import com.example.app.firebaseDB.FbUpdate;

public class UpdatePassword {
    public UpdatePassword(){

    }

    public static void UpdatePW(String email, String newPassword, MutableLiveData<Boolean> success){
        FbUpdate.updatePassword(email,newPassword, success);
    }
}
