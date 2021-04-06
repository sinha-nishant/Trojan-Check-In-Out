package com.example.app.whiteBox;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.example.app.firebaseDB.FbQuery;
import com.example.app.users.StudentAccount;
import com.google.firebase.FirebaseApp;

import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.fail;

public class FbSearchByNameTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void searchByName() {
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
                    // hardcoded from FbCreateStudentAccountTest
                    if (!studentAccount.getMajor().equals("CSBA")) {
                        fail("Student account major is not CSBA");
                    }
                }

                assert(true);
            }
        };
        studentsMLD.observeForever(listObserver);

        //names hardcoded from the following test

        // major hardcoded from FbCreateStudentAccountTest
        FbQuery.search("CSBA", studentsMLD);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}