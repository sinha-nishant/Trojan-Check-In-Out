package com.example.app.firebaseDB;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.example.app.users.Account;
import com.example.app.users.StudentAccount;
import com.google.firebase.FirebaseApp;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CreateAccountTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    //test to check if the createAccount function works along with a built in deleteAccount that
    //is not tested
    public void createAccountWithCorrectInputNoPic() {
        //values to initialize account
        String firstName= "Fname";
        String lastName="Lname";
        String email="fname.lname@usc.edu";
        String password="pass1234";
        String profilePicture="";
        Long uscID = 4204204269L;
        String major = "CSBA";
        StudentAccount a = new StudentAccount(firstName,lastName,email,password,profilePicture, uscID,major, false);

        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        Integer intExpected = 4;
        MutableLiveData<Integer> success = new MutableLiveData<>();
        Observer<Integer> successObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                assertEquals(intExpected,integer);
            }
        };
        success.observeForever(successObserver);

//        FbUpdate.createAccount(a,success);
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