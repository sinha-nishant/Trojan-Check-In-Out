package com.example.app.whiteBox;

import android.net.Uri;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.example.app.log_create.uploadPhoto;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class AWSUploadTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void AWSCheck() throws InterruptedException {
        AmplifyInit();
        // get picture address
        Uri uri = Uri.parse("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__340.jpg");
        MutableLiveData<Boolean> uploadMLD= new MutableLiveData<>();
        //email for associated account
        String email="TestUploadEspresso@usc.edu";
        Observer<Boolean> upload_obs = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean Success) {
                assertEquals(Success,true);
            }
        };
        uploadMLD.observeForever(upload_obs);
        try {

            URL url = new URL(uri.toString());
            InputStream stream = url.openStream();
            ///upload image
            uploadPhoto.upload(stream,email,uploadMLD);

        } catch (IOException e) {
            e.printStackTrace();
            fail("here");

        }

        Thread.sleep(10000);
        if(uploadMLD.getValue()==null){
            fail("did not update image");
        }
    }

    private void AmplifyInit(){
        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(ApplicationProvider.getApplicationContext());
        } catch (AmplifyException e) {
            Log.i("MyAmplifyApp", "could not add plugins ");
        }
    }
}