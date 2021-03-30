package com.example.app.whiteBox;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.example.app.building.Building;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.users.StudentAccount;
import com.google.firebase.FirebaseApp;

import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.fail;

public class FbGetCurrentStudentsTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private static Building building = null;

    @Test
    public void getCurrentStudents() {
        if (QRScanCheckInTest.random_building == null || QRScanCheckInTest.random_building.isEmpty()) {
            fail("random building name is empty or null");
        }

        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);

        MutableLiveData<List<StudentAccount>> studentsMLD = new MutableLiveData<List<StudentAccount>>();
        Observer<List<StudentAccount>> listObserver = new Observer<List<StudentAccount>>() {
            @Override
            public void onChanged(List<StudentAccount> studentAccountList) {
                if (studentAccountList == null) {
                    fail("Student accounts are null");
                }
                for (StudentAccount studentAccount: studentAccountList) {
                    if (!studentAccount.getActivity().get(studentAccount.getActivity().size() - 1).getBuildingName().equals(QRScanCheckInTest.random_building)) {
                        fail("Student is not checked into " + QRScanCheckInTest.random_building);
                    }
                }

                assert(true);
            }
        };
        studentsMLD.observeForever(listObserver);

        MutableLiveData<Building> buildingMLD = new MutableLiveData<Building>();
        Observer<Building> buildingObserver = new Observer<Building>() {
            @Override
            public void onChanged(Building building) {
                FbGetCurrentStudentsTest.building = building;
            }
        };
        buildingMLD.observeForever(buildingObserver);

        FbQuery.getBuilding(QRScanCheckInTest.random_building, buildingMLD);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        FbQuery.getCurrentStudents(FbGetCurrentStudentsTest.building, studentsMLD);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
