package com.example.app.log_create;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.amplifyframework.core.Amplify;

import java.io.InputStream;

public class uploadPhoto {

    public static void upload(InputStream uri,String email,MutableLiveData<Integer>success){
        Log.i("uploadPhoto","in upload photo");
        String key=email;
            Amplify.Storage.uploadInputStream(
            key,
            uri,
            result -> success.setValue(0),
            storageFailure -> success.setValue(3)
        );

    }

    public static void update(InputStream uri,String email,MutableLiveData<Boolean>success){
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
