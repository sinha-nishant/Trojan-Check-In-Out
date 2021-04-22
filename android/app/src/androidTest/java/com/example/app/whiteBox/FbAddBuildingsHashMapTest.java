package com.example.app.whiteBox;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.example.app.firebaseDB.FbUpdate;
import com.google.firebase.FirebaseApp;

import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FbAddBuildingsHashMapTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    //test to increase building capacity (non-requirement)
    public void AddBuildingsHashMapTest() {
        //values to initialize account
        String buildingName = "Charlie's Chocolate Factory";
        String buildingName2 = "Charlie's Chocolate Factory2";
        int newCapacity = 100;
        HashMap<String, Integer> buildingHash = new HashMap<>();
        buildingHash.put(buildingName,newCapacity);
        buildingHash.put(buildingName2, newCapacity);

        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        MutableLiveData<Boolean> success = new MutableLiveData<>();
        Observer<Boolean> successObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean == null) {
                    fail("Did not observe");
                    return;
                }
                assertEquals(true, aBoolean);
            }
        };
        success.observeForever(successObserver);

        FbUpdate.addBuildings(buildingHash, success);
        //amount of delay in order to ensure the Firebase command is executed
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //if there is a problem with the test such that live data is never updated
        if (success.getValue() == null) {
            fail("did not update observer");
        }


    }
}
