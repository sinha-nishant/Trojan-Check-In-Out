package com.example.app.firebaseDB;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.app.users.Account;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.io.InputStream;

import static org.junit.Assert.*;

public class FbUpdateTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();





    @Test
    public void testCreateAccountWhenInputValidNoPic() {

        String firstName= "Fname";
        String lastName="Lname";
        String email="fname.lname@usc.edu";
        String password="pass1234";
        String profilePicture="";
        Boolean type=false;
        Account a = new Account(firstName,lastName,email,password,profilePicture,type);
        MutableLiveData<Integer> success = new MutableLiveData<>();
        FbUpdate.createAccount(a,success);

        int successInt = success.getValue();
        assertEquals(successInt,4);
    }

}