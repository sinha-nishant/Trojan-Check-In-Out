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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FbGetStudentTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    //test to check if getStudent using ID is working
    public void getStudent() {
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        //existing email and id of student account
        Long uscID = Long.valueOf(Credentials.id);
        String emailExpected = Credentials.email;
        MutableLiveData<StudentAccount> student = new MutableLiveData<>();
        Observer<StudentAccount> saObserver = new Observer<StudentAccount>() {
            @Override
            public void onChanged(StudentAccount studentAccount) {
                if (studentAccount == null) {
                    fail("could not find account");
                }
                assertEquals(emailExpected, studentAccount.getEmail());
            }
        };
        student.observeForever(saObserver);

        //getStudent using ID
        FbQuery.getStudent(uscID, student);

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
