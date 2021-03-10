package com.example.app;

import android.util.Log;

import com.amplifyframework.core.Amplify;

import java.io.InputStream;

public class uploadPhoto {
    public void uploadPhoto(){

    }


    public void upload(InputStream uri,String email,String Extension){
        Log.i("uploadPhoto","in upload photo");
        String key=email+Extension;
            Amplify.Storage.uploadInputStream(
            key,
            uri,
            result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey()),
            storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
        );

    }
}
