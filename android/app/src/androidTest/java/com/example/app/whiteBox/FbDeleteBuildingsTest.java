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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FbDeleteBuildingsTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    //test to increase building capacity (non-requirement)
    public void deleteBuildingsTest() {
        //Building Names
        String buildingName1 = "Charlie's Chocolate Factory";
        String buildingName2 = "Charlie's Chocolate Factory2";
        List<String> buildings = new ArrayList<String>();
        buildings.add(buildingName1);
        buildings.add(buildingName2);

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

        FbUpdate.deleteBuildings(buildings, success);
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
