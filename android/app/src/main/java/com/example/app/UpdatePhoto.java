package com.example.app;

import androidx.lifecycle.MutableLiveData;

import java.io.InputStream;

public class UpdatePhoto {
    public UpdatePhoto(String email, InputStream stream,String Extension, MutableLiveData<Boolean> success){
        String url= AWSLink(email,Extension);
//        FirebaseTest.updatePhoto(url,email,stream,Extension,success);
        //call uploadPhoto from here to actually upload a photo

    }

    public String AWSLink(String email,String Extension){
        String domain="https://trojan-check-in-and-out183928-dev173416-dev.s3-us-west-2.amazonaws.com/public/";
        return domain+email.replaceFirst("@","%40")+Extension;
    }
}
