package com.example.app.whiteBox;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.example.app.blackBox.CreateStudentTest;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.users.StudentAccount;
import com.google.firebase.FirebaseApp;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class FbSearchTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void search() {
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        Long uscID= FbCreateAccountTest.uscID;
        String emailExpected = FbCreateAccountTest.email;
        MutableLiveData<StudentAccount> student = new MutableLiveData<>();
        Observer<StudentAccount> saObserver = new Observer<StudentAccount>() {
            @Override
            public void onChanged(StudentAccount studentAccount) {
                if(studentAccount==null){
                    fail("could not find account");
                }
                assertEquals(emailExpected, studentAccount.getEmail());
            }
        };
        student.observeForever(saObserver);
        FbQuery.search(uscID,student);
        //To get the test to run add this - Firebase takes time to execute the query and the thread
        //will just run in the background without testing the Firebase database if the code isn't
        //there
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}