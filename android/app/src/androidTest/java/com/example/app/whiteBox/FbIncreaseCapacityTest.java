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

public class FbIncreaseCapacityTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    //test to check if the createAccount function works along with a built in deleteAccount that
    //is not tested
    public void increaseBuildingCapacityTest() {
        //values to initialize account
        String buildingName = "Ahmanson Ctr. for Biological Research Animal Section";
        int newCapacity = 420;

        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        MutableLiveData<Boolean> success = new MutableLiveData<>();
        Observer<Boolean> successObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean==null){
                    fail("Did not observe");
                    return;
                }
                assertEquals(true,aBoolean);
            }
        };
        success.observeForever(successObserver);

        FbUpdate.updateCapacity(buildingName,success,newCapacity);
        //To get the test to run add this - Firebase takes time to execute the query and the thread
        //will just run in the background without testing the Firebase database if the code isn't
        //there
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (success.getValue() == null) {
            fail("did not update MLD");

        }


    }
}
