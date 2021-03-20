package com.example.app;


import androidx.lifecycle.MutableLiveData;

public class UpdatePassword {
    public UpdatePassword(){

    }

    public static void UpdatePW(String email, String newPassword, MutableLiveData<Boolean> success){
        FbUpdate.updatePassword(email,newPassword, success);
    }
}
