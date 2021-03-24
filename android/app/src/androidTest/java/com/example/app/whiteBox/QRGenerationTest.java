package com.example.app.whiteBox;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.example.app.building.Building;
import com.example.app.firebaseDB.FbQuery;
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

import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import static org.junit.Assert.*;

public class QRGenerationTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    public static List<Building> post_value;
    public static List<String>  decoded_bitmaps= new ArrayList<>();
    @Test
    public void getBitMap() {
        //get all buildings from firebase
        //generate bitmap by Using GetBitMap function and get their decoded values
        //put values into new array and make sure it is equal to building size returned by firebase confirming that there are no duplicates
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        MutableLiveData<List<Building>> builingMLD = new MutableLiveData<>();
        Reader reader = new QRCodeReader();

        final Observer<List<Building>> observer = new Observer<List<Building>>(){
            @Override
            public void onChanged(@Nullable final List<Building> buildings){
                post_value=buildings;
            }
        };
        builingMLD.observeForever(observer);
        FbQuery.getAllBuildings(builingMLD);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(int i=0;i<post_value.size();i++){
            Bitmap bMap = QRGeneration.GetBitMap(post_value.get(i).getName());
            bMap=Bitmap.createScaledBitmap(bMap, 130,130,false);
            String decoded = null;
            int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
            bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(),
                    bMap.getHeight());
            LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(),
                    bMap.getHeight(), intArray);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                Result result = reader.decode(bitmap);
                decoded = result.getText();
//                Log.d("Building is : ", decoded);
                decoded_bitmaps.add(decoded);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }catch (ChecksumException e) {
                e.printStackTrace();
            } catch (FormatException e) {
                e.printStackTrace();
            }
        }
        assertEquals(decoded_bitmaps.size(),post_value.size());
    }
}