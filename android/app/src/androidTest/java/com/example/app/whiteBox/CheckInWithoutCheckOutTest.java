package com.example.app.whiteBox;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.example.app.Credentials;
import com.example.app.building.Building;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.services.CheckInOut;
import com.example.app.users.StudentAccount;
import com.google.firebase.FirebaseApp;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CheckInWithoutCheckOutTest {
    public static Integer compareOccupancy;
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    public List<Building> post_value;
    public static String random_building;
    public StudentAccount student;
    @Before
    public void setUp() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
    }

    @After
    public void tearDown() throws Exception {
        MutableLiveData<List<Building>> buildingsMLD = new MutableLiveData<>();
        final Observer<List<Building>> observer = new Observer<List<Building>>(){
            @Override
            public void onChanged(@Nullable final List<Building> buildings){
                post_value=buildings;
            }
        };
        //get buildings from firebase
        buildingsMLD.observeForever(observer);
        FbQuery.getAllBuildings(buildingsMLD);
        Thread.sleep(5000);
        //get random building from returned buildings
        Random rand = new Random();
        Integer i = rand.nextInt(post_value.size());
        random_building = post_value.get(i).getName();
        Integer old_occ = post_value.get(i).getOccupancy();
        MutableLiveData<Boolean> checkInMLD = new MutableLiveData<>();
        Observer<Boolean> observerCheckIn = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean success) {
                if(!success){//if doesn't check in student test fails
                    fail();
                }
            }
        };
        checkInMLD.observeForever(observerCheckIn);
        MutableLiveData<Building> buildingMLD = new MutableLiveData<>();
        Observer<Building> observerSingleBuilding = new Observer<Building>() {
            @Override
            public void onChanged(Building building) {
                compareOccupancy = building.getOccupancy();
            }
        };
        buildingMLD.observeForever(observerSingleBuilding);
        //check into random building
        CheckInOut.checkIn(checkInMLD,random_building, Long.parseLong(Credentials.id));
        Thread.sleep(5000);
        Integer occupancyAfterFirstCheckIn = old_occ+1;
        MutableLiveData<StudentAccount> studentMLD = new MutableLiveData<>();
        Observer<StudentAccount> studentObserver = new Observer<StudentAccount>() {
            @Override
            public void onChanged(StudentAccount studentAccount) {
                student=studentAccount;
            }
        };
        FbQuery.getStudent(Long.parseLong(Credentials.id),studentMLD);
        if(i+1<post_value.size()){
            i++;
        }else{
            i--;
        }
        //check into different building
        CheckInOut.canCheckIn(checkInMLD,post_value.get(i).getName(),Long.parseLong(Credentials.id),post_value.get(i),student);
        Thread.sleep(5000);
        FbQuery.getBuilding(random_building, buildingMLD);
        Thread.sleep(5000);
        //occupancy should be same because student hasn't checked out
        assertEquals(occupancyAfterFirstCheckIn,compareOccupancy);
    }

    @Test
    public void CheckInWithoutCheckOut() {
    }
}