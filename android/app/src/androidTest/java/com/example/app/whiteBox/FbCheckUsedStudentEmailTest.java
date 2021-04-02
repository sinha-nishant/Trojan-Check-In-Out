package com.example.app.whiteBox;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.example.app.Credentials;
import com.example.app.firebaseDB.FbQuery;
import com.google.firebase.FirebaseApp;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FbCheckUsedStudentEmailTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    @Test
    public void checkUsedStudentEmail() {
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        //existing email
        String emailExpected = Credentials.email;
        MutableLiveData<Boolean> mld = new MutableLiveData<>();
        Observer<Boolean> email_obs = new Observer<Boolean>() {
            //check if email has been used
            @Override
            public void onChanged(Boolean Exists) {
                assertEquals(Exists,true);
            }
        };
        mld.observeForever(email_obs);
        FbQuery.checkEmailExists(emailExpected,mld);


        //To get the test to run add this - Firebase takes time to execute the query and the thread
        //will just run in the background without testing the Firebase database if the code isn't
        //there
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
