package com.example.app.whiteBox;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.example.app.Credentials;
import com.example.app.firebaseDB.FbUpdate;
import com.example.app.users.Account;
import com.example.app.users.StudentAccount;
import com.google.firebase.FirebaseApp;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FbCreateStudentAccountTest {
    public static String email;
    public static Long uscID;
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    //test to check if the createAccount function works along with a built in deleteAccount that
    //is not tested
    public void createAccountWithCorrectInputNoPic() {

        email= Credentials.email;



        String firstName= "Fname";
        String lastName="Lname";

        String password="pass1234";

        uscID= Long.valueOf(Credentials.id);

        String major = "CSBA";
        StudentAccount a= new StudentAccount(firstName,lastName,email,password,uscID,major,false);
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        MutableLiveData<Boolean> success = new MutableLiveData<>();
        Observer<Boolean> successObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSuccess) {
                //check if account was created
                if(isSuccess==null){
                    fail("did not observe");
                    return;
                }
                assertEquals(true,isSuccess);
            }
        };
        success.observeForever(successObserver);

        FbUpdate.createAccount(a,success);
        //To get the test to run add this - Firebase takes time to execute the query and the thread
        //will just run in the background without testing the Firebase database if the code isn't
        //there
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}