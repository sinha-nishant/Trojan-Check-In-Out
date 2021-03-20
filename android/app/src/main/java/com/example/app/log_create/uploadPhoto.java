package com.example.app.log_create;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.amplifyframework.core.Amplify;

import java.io.InputStream;

public class uploadPhoto {
    public void uploadPhoto(){

    }

    public static void uploadCreate(InputStream uri,String email,MutableLiveData<Integer>success){
        Log.i("uploadPhoto","in upload photo");
        String key=email;
            Amplify.Storage.uploadInputStream(
            key,
            uri,
            result -> success.setValue(4),
            storageFailure -> success.setValue(3)
        );

    }

    public static void uploadUpdate(InputStream uri, String email, MutableLiveData<Boolean> success){
        Log.i("uploadPhoto","in upload photo");
        String key=email;
        Amplify.Storage.uploadInputStream(
                key,
                uri,
                result -> success.setValue(true),
                storageFailure -> success.setValue(false)
        );

    }
}
