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

public class FbUpdateStudentProfilePicTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void UpdatePic() {
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
        String email= Credentials.email;
        //update photo url according to the email above
        FbUpdate.updatePhoto(email,updateMLD);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(updateMLD.getValue()==null){
            fail("Could not find email");
        }
    }
}
