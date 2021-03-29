package com.example.app.whiteBox;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.example.app.Credentials;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.users.StudentAccount;
import com.google.firebase.FirebaseApp;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class FbStudentSearchTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    //test to check if search using ID is working
    public void search() {
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        Long uscID= Long.valueOf(Credentials.id);
        String emailExpected = Credentials.email;
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

        //search using ID
        FbQuery.search(uscID,student);

        //amount of delay in order to ensure the Firebase command is executed
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (student.getValue() == null) {
            fail("did not update MLD");
        }

    }
}