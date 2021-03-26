package com.example.app.whiteBox;


import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.example.app.blackBox.CreateStudentTest;
import com.example.app.firebaseDB.FbCheckInOut;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.firebaseDB.FbUpdate;
import com.example.app.users.StudentAccount;
import com.example.app.users.StudentActivity;
import com.google.firebase.FirebaseApp;

import org.junit.Rule;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FbCheckStudentActivity {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void checkActivity() {
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        Long id = Long.valueOf(CreateStudentTest.uscID);
        StudentActivity act= new StudentActivity(QRScanCheckInTest.random_building,QRScanCheckInTest.checkInDate);
        MutableLiveData<StudentAccount> studentMLD = new MutableLiveData<>();
        Observer<StudentAccount> student_obs = new Observer<StudentAccount>() {
            @Override
            public void onChanged(StudentAccount sa) {
                if(sa == null){
                    fail("could not find account");
                }
                List<StudentActivity> activities= sa.getActivity();
                if(activities==null|| activities.size()==0){
                    fail("did not update activity");
                }
                assertEquals(activities.get(0).getBuildingName(),act.getBuildingName());
            }
        };
        studentMLD.observeForever(student_obs);
        FbQuery.search(id,studentMLD);



        //To get the test to run add this - Firebase takes time to execute the query and the thread
        //will just run in the background without testing the Firebase database if the code isn't
        //there
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(studentMLD.getValue()==null){
            fail("did not set activity");
        }
    }
}
