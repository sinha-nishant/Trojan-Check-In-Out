package com.example.app.whiteBox;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.example.app.firebaseDB.FbUpdate;
import com.example.app.users.Account;
import com.example.app.users.StudentAccount;
import com.google.firebase.FirebaseApp;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FbCreateAccountTest {
    public static String email;
    public static Long uscID;
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    //test to check if the createAccount function works along with a built in deleteAccount that
    //is not tested
    public void createAccountWithCorrectInputNoPic() {
        //values to initialize account

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(20);

        for (int i = 0; i < 20; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        String generatedString = sb.toString();
        generatedString+="@usc.edu";
        email=generatedString;



        String firstName= "Fname";
        String lastName="Lname";

        String password="pass1234";


        long leftLimit = 1000000000L;
        long rightLimit = 9999999999L;
        uscID = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));

        String major = "CSBA";
        StudentAccount a= new StudentAccount(firstName,lastName,email,password,uscID,major,false);
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        MutableLiveData<Boolean> success = new MutableLiveData<>();
        Observer<Boolean> successObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSuccess) {
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