package com.example.app.blackBox;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.app.building.BuildingsOccupancyList;
import com.example.app.firebaseDB.FbCheckInOut;
import com.example.app.users.StudentActivity;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;


@RunWith(AndroidJUnit4.class)
public class StudentChecksIntoFullBuildingTest extends TestCase {

    @Rule
    // creating the activityRule for the Manager home screen activity
    public ActivityScenarioRule<BuildingsOccupancyList> activityRule =
            new ActivityScenarioRule<>(BuildingsOccupancyList.class);

//    // creating the activityRule to simulate the student trying to check into a building
//    public ActivityScenarioRule<BuildingsOccupancyList> activityRule =
//            new ActivityScenarioRule<>(BuildingsOccupancyList.class);

    // going to make a student check into the already full building
    public FbCheckInOut fbcheckinout = new FbCheckInOut();
    //long usdID = 4829692065; THIS LINE COMMENTED OUT BY JOHN FOR ERROR
    // just creating an empty studentactivity list for now
    List<StudentActivity> sa_list = new ArrayList<StudentActivity>();
    // creating instance of MutableLiveData?
    








    @Test
    public void testStudentChecksIntoFullBuilding() throws InterruptedException {
        Thread.sleep(5000);// wait 5 seconds, want to see the screen


    }
}