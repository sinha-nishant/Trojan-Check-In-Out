package com.example.app.firebaseDB;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.example.app.users.StudentAccount;
import com.google.firebase.FirebaseApp;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeleteAccountTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    //test to check if the createAccount function works along with a built in deleteAccount that
    //is not tested
    public void deleteAccountTest() {
        //values to initialize account
        String email="fname.lname@usc.edu";

        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        Integer intExpected = 2;
        MutableLiveData<Integer> success = new MutableLiveData<>();
        Observer<Integer> successObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                assertEquals(intExpected,integer);
            }
        };
        success.observeForever(successObserver);

        FbUpdate.deleteAccount(email,success);
        //To get the test to run add this - Firebase takes time to execute the query and the thread
        //will just run in the background without testing the Firebase database if the code isn't
        //there
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}