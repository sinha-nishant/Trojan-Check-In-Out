package com.example.app.whiteBox;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.example.app.Credentials;
import com.example.app.firebaseDB.FbUpdate;
import com.example.app.users.Account;
import com.example.app.users.StudentAccount;
import com.google.firebase.FirebaseApp;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FbCreateManagerAccountTest {
    public static String email;
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void createManager() {

        email= Credentials.email;



        String firstName= "Fname";
        String lastName="Lname";

        String password="pass1234";

        Account a= new Account(firstName,lastName,email,password,true);

        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        MutableLiveData<Boolean> success = new MutableLiveData<>();
        Observer<Boolean> successObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSuccess) {
                //check if account got created
                if(isSuccess==null){
                    fail("did not observe");
                    return;
                }
                assertEquals(true,isSuccess);
            }
        };
        success.observeForever(successObserver);
        //create account
        FbUpdate.createAccount(a,success);
        //To get the test to run add this - Firebase takes time to execute the query and the thread
        //will just run in the background without testing the Firebase database if the code isn't
        //there
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(success.getValue()==null){
            fail("did not update observer");
        }


    }
}
