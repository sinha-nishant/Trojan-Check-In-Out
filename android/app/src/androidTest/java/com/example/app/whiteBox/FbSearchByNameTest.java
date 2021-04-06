package com.example.app.whiteBox;

import android.content.Context;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.example.app.Credentials;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.firebaseDB.FbUpdate;
import com.example.app.users.Account;
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
        String fname_substring = "fna";
        String lname_substring = "ame";
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        MutableLiveData<List<StudentAccount>> studentsMLD = new MutableLiveData<List<StudentAccount>>();
        Observer<List<StudentAccount>> listObserver = new Observer<List<StudentAccount>>() {
            @Override
            public void onChanged(List<StudentAccount> studentAccountList) {
                if (studentAccountList == null) {
                    fail("Student accounts are null");
                }
                for (StudentAccount studentAccount : studentAccountList) {
                    // hardcoded from FbCreateStudentAccountTest
                    if (!(studentAccount.getFirstName().contains(fname_substring)
                            || studentAccount.getLastName().contains(lname_substring))) {
                        fail("Something doesn't look quite right");
                    }
                    Log.d("SEARCH_Test",studentAccount.getFirstName() + ": " + studentAccount.getLastName());
                }

                assert (true);
            }
        };
        studentsMLD.observeForever(listObserver);

        // create account hardcoded for this test
        String firstName= "Fname";
        String lastName="Lname";
        String password="pass1234";
        Long uscID= Long.valueOf(Credentials.id);
        String email = Credentials.email;
        String major = "CSBA";
        Integer intExpected = 0;
        StudentAccount a1= new StudentAccount(firstName,lastName,"lmaoboi@usc.com",password,uscID,major,false);
        StudentAccount a2= new StudentAccount("hahahafname","lalalalalname","a1@usc.edu",password,uscID,major,false);
        StudentAccount a3= new StudentAccount("setleanbagfnamegotalotofchips","driplnamelol","a2@usc.edu",password,uscID,major,false);
        StudentAccount a4= new StudentAccount("FNAME","LNAME","a3@usc.edu",password,uscID,major,false);

        MutableLiveData<Boolean> success = new MutableLiveData<>();
        MutableLiveData<Integer> delete_success = new MutableLiveData<>();

        FbUpdate.createAccount(a1,success);
        FbUpdate.createAccount(a2,success);
        FbUpdate.createAccount(a3,success);
        FbUpdate.createAccount(a4,success);

        FbQuery.search(fname_substring, lname_substring, studentsMLD);

        FbUpdate.deleteAccount("lmaoboi@gmail.com",delete_success);
        FbUpdate.deleteAccount("a1@usc.edu",delete_success);
        FbUpdate.deleteAccount("a2@usc.edu",delete_success);
        FbUpdate.deleteAccount("a3@usc.edu",delete_success);

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}