package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    public void changeFirebase(View v){
        Intent i= new Intent(this,FirebaseTest.class);
        startActivity(i);
    }

    public void changeJohn(View v){
        Intent i= new Intent(this,JohnTest.class);
        startActivity(i);
    }

    public void changeMarkus(View v){
        Intent i= new Intent(this,MarkusTest.class);
        startActivity(i);
    }


    public void changeHassib(View v){
        Intent i= new Intent(this,HassibTest.class);
        startActivity(i);
    }
    public void changeHassib2(View v){
        Intent i= new Intent(this,QRScanTest.class);
        startActivity(i);
    }

    public void changeAngad(View v){
        Intent i= new Intent(this,AngadTest.class);
        startActivity(i);
    }

    public void changeCreateDummy(View v) {
        Intent i= new Intent(this, CreateDummyAccount.class);
        startActivity(i);
    }
}
