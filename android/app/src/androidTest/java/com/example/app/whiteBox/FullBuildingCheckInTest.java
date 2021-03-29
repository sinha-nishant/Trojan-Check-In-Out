package com.example.app.whiteBox;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.example.app.Credentials;
import com.example.app.building.Building;
import com.example.app.firebaseDB.FbQuery;
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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import javax.annotation.Nullable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FullBuildingCheckInTest {
    public static Integer compareOccupancy;
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    public List<Building> post_value;
    public static String random_building;

    @Before
    public void setUp() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void fullBuildingTest() throws InterruptedException{
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
        //get building that has full capacity
        Integer i = 0;
        random_building = post_value.get(i).getName();
        Integer old_occ = post_value.get(i).getOccupancy();
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
            CheckInOut.canCheckIn(checkInMLD,result.getText(), Long.parseLong(Credentials.id),post_value.get(i),null);
            //get buildings again from firebase
            FbQuery.getBuilding(random_building, buildingMLD);
            Thread.sleep(5000);
            //new occupancy shouldn't have changed since building is full
            assertEquals(old_occ,compareOccupancy);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }


    }
}