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

        //amount of delay in order to ensure the Firebase command is executed
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //if there is a problem with the test such that live data is never updated
        if(success.getValue()==null){
            fail("did not update observer");
        }


    }
}
