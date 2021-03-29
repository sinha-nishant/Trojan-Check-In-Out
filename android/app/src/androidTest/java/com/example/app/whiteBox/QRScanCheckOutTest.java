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
import com.example.app.users.StudentActivity;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class QRScanCheckOutTest {
    Integer returnedOccupancy;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {


    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void CheckOutTest() throws InterruptedException{
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        //create bitmap from random building name that student is checked into
        Reader reader = new QRCodeReader();
        Bitmap bMap = QRGeneration.GetBitMap(QRScanCheckInTest.random_building);
        bMap=Bitmap.createScaledBitmap(bMap, 130,130,false);
        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(),
                bMap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(),
                bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        MutableLiveData<Boolean> checkOutMLD = new MutableLiveData<>();
        Observer<Boolean> observerCheckOut = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean success) {
                if(!success){//if doesn't check out student test fails
                    fail();
                }
            }
        };
        checkOutMLD.observeForever(observerCheckOut);
        MutableLiveData<Building> buildingMLD = new MutableLiveData<>();
        Observer<Building> observerSingleBuilding = new Observer<Building>() {
            @Override
            public void onChanged(Building building) {
                returnedOccupancy = building.getOccupancy();
            }
        };
        buildingMLD.observeForever(observerSingleBuilding);
        try {
            //decode QRcode bitmap
            Result result = reader.decode(bitmap);
            //get student so you can get exact StudentActivity object
            StudentActivity sa = new StudentActivity(QRScanCheckInTest.random_building,QRScanCheckInTest.checkInDate,null);
            //check out student with the use of Services package
            CheckInOut.checkOut(checkOutMLD,sa,Long.parseLong(Credentials.id));
            Thread.sleep(5000);
            //get buildings again from firebase
            FbQuery.getBuilding(QRScanCheckInTest.random_building, buildingMLD);
            Thread.sleep(5000);
            //check if new occupancy is equal to old occupancy-1
            Integer new_occ = QRScanCheckInTest.compareOccupancy-1;
            assertEquals(new_occ,returnedOccupancy);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

}