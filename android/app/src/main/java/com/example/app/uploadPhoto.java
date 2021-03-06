package com.example.app;

import android.util.Log;

import com.amplifyframework.core.Amplify;

import java.io.InputStream;

public class uploadPhoto {
    public void uploadPhoto(){

    }


    public void upload(InputStream uri,String email){
//        String domain="https://trojan-check-in-and-out183928-dev173416-dev.s3-us-west-2.amazonaws.com/public/";
        Log.i("uploadPhoto","in upload photo");
        String key=email+".png";
            Amplify.Storage.uploadInputStream(
            key,
            uri,
            result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey()),
            storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
        );
//            return domain+key.replaceFirst("@","%40");
    }
}
