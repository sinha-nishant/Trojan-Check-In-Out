package com.example.app;

import androidx.lifecycle.MutableLiveData;

import java.io.InputStream;

public class UpdatePhoto {

    public UpdatePhoto(){

    }

    public static void Update(String email, InputStream stream,String Extension, MutableLiveData<Boolean> success){
        uploadPhoto.upload(stream,email,Extension,success);
    }

}
