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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FbDeleteManagerAccountTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    //test to check if the createAccount function works along with a built in deleteAccount that
    //is not tested
    public void deleteAccountTest() {
        //values to initialize account
        String email= Credentials.email;

        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        Integer intExpected = 2;
        MutableLiveData<Integer> success = new MutableLiveData<>();
        Observer<Integer> successObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer==null){
                    fail("did not set integer");
                }
                assertEquals(intExpected,integer);
            }
        };
        success.observeForever(successObserver);

        FbUpdate.deleteAccount(email,success);

        //amount of delay in order to ensure the Firebase command is executed
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(success.getValue()==null){
            fail("did not update MLD");
        }

    }
}
