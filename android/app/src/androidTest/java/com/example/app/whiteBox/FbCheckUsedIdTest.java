package com.example.app.whiteBox;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.example.app.blackBox.CreateStudentTest;
import com.example.app.firebaseDB.FbQuery;
import com.google.firebase.FirebaseApp;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FbCheckUsedIdTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    @Test
    public void check() {
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        Long uscID= Long.valueOf(CreateStudentTest.uscID);// should be user in an account
        MutableLiveData<Boolean> mld = new MutableLiveData<>();
        Observer<Boolean> id_obs = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean Success) {
                assertEquals(Success,false);
            }
        };
        mld.observeForever(id_obs);
        FbQuery.checkUSCidExists(uscID,mld);


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