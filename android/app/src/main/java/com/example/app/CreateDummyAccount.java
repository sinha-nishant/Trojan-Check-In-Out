package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.InputStream;

public class CreateDummyAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_dummy_account);
    }

    public void createAcc(View v){
//        InputStream exampleInputStream = getContentResolver().openInputStream(Uri.parse(uri));
//        if(exampleInputStream==null){
//            Log.i("upload", "stream is null");
//        }
//        else{
//            Log.i("upload", "stream is valid");
//        }

        CreateAccount ca= new CreateAccount("Washington", "Sundar","96@usc.edu","Solid", false);
    }
}