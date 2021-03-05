package com.example.app;

import android.util.Log;

import com.amplifyframework.core.Amplify;

import java.io.InputStream;

public class uploadPhoto {
    public void uploadPhoto(){

    }


    public String upload(InputStream uri,String email){
        Log.i("uploadPhoto","in upload photo");
        String key=email+".png";
            Amplify.Storage.uploadInputStream(
            key,
            uri,
            result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey()),
            storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
        );
            return key.replaceFirst("@","%40");
    }
}
