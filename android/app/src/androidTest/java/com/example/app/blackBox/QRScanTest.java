package com.example.app.blackBox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.ActivityTestRule;

import com.budiyev.android.codescanner.CodeScanner;
import com.example.app.R;
import com.example.app.account_UI.QRScan;
import com.example.app.blackBox.CreateStudentTest;
import com.example.app.building.Building;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.pre_login_UI.LogInPage;
import com.example.app.services.CheckInOut;
import com.example.app.services.QRGeneration;
import com.google.firebase.FirebaseApp;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;


import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.example.app.whiteBox.QRGenerationTest.decoded_bitmaps;
import static org.junit.Assert.*;

public class QRScanTest {
    Intent intent;
    SharedPreferences.Editor preferencesEditor;
    Integer compareOccupancy;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    public  List<Building> post_value;

    @Before
    public void setUp() throws Exception {


    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void onCreate() throws InterruptedException{
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
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
        int i = rand.nextInt(post_value.size());
        String random_building = post_value.get(i).getName();
        int old_occ = post_value.get(i).getOccupancy();
        //create bitmap from random building name
        Reader reader = new QRCodeReader();
        Bitmap bMap = QRGeneration.GetBitMap(random_building);
        bMap=Bitmap.createScaledBitmap(bMap, 130,130,false);
        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(),
                bMap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(),
                bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
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
        try {
            //decode QRcode bitmap
            Result result = reader.decode(bitmap);
            //check in student with the use of Services package
            CheckInOut.checkIn(checkInMLD,result.getText(),Long.getLong(CreateStudentTest.uscID));
            Thread.sleep(5000);
            //get buildings again from firebase
            FbQuery.getBuilding(random_building, buildingMLD);
            Thread.sleep(5000);
            //check if new occupancy is equal to old occupancy +1
            Integer new_occ = old_occ+1;
            assertEquals(new_occ,compareOccupancy);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }

    }
}