package com.example.app.whiteBox;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.example.app.Credentials;
import com.example.app.firebaseDB.FbUpdate;
import com.google.firebase.FirebaseApp;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.fail;

public class FbUpdateStudentPasswordTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    //non-requirement test to try and update the password
    @Test
    public void UpdatePassword() {
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        MutableLiveData<Boolean> updateMLD = new MutableLiveData<>();
        Observer<Boolean> Observer = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isUpdated) {
                assert isUpdated;
            }
        };
        updateMLD.observeForever(Observer);
        String email = Credentials.email;
        FbUpdate.updatePassword(email,"SecurePassword69", updateMLD);

        //amount of delay in order to ensure the Firebase command is executed
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(updateMLD.getValue()==null){
            fail("Could not find email");
        }
    }
}
