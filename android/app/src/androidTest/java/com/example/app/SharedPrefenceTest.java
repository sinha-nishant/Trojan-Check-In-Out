package com.example.app;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import androidx.test.core.app.ApplicationProvider;


import com.example.app.log_create.LogInOut;

import org.junit.Rule;
import org.junit.Test;

import static android.content.Context.MODE_PRIVATE;


public class SharedPrefenceTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void checkPreferences() throws InterruptedException {
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String inputString="TestEmail20@usc.edu";
        editor.putString("email",inputString);
        Long inputLong= 1717171212L;
        editor.putLong( "uscid",inputLong);
        editor.apply();
        String test_retrieve_email = sharedPreferences.getString("email","");
        Long test_retrieve_id = sharedPreferences.getLong("uscid",0L);
        assert test_retrieve_email.equals(inputString) ;
        assert test_retrieve_id.equals(inputLong);


    }

}
