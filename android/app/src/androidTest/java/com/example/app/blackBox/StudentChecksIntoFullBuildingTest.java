package com.example.app.blackBox;

import android.app.Activity;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.app.building.BuildingsOccupancyList;
import com.example.app.pre_login_UI.LogInPage;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class StudentChecksIntoFullBuildingTest extends TestCase {

    @Rule
    // creating the activityRule for the Manager home screen activity
    public ActivityScenarioRule<BuildingsOccupancyList> activityRule =
            new ActivityScenarioRule<>(BuildingsOccupancyList.class);

//    // creating the activityRule to simulate the student trying to check into a building
//    public ActivityScenarioRule<BuildingsOccupancyList> activityRule =
//            new ActivityScenarioRule<>(BuildingsOccupancyList.class);

    @Test
    public void testStudentChecksIntoFullBuilding() throws InterruptedException {
        Thread.sleep(5000);// wait 5 seconds, want to see the screen


    }
}