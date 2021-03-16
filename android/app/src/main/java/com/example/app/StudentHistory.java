package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

public class StudentHistory extends AppCompatActivity {

    ListView lv;
    ArrayList<String> buildings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_history);

        //BACKEND: Load in list of student activities here
        ArrayList<StudentActivity>activities= new ArrayList<>();
        for(int i = 0; i < 20; i++) {
            activities.add(new StudentActivity("Parkside IRC", new Date(), new Date()));
            activities.add(new StudentActivity("Webb Tower", new Date(), new Date()));
        }
        //You may need to reverse arraylist here, depending on how the data is stored
        //END
        lv = (ListView)findViewById(R.id.buildingList);
        buildings = new ArrayList<>(activities.size());

        for(int i = 0; i < activities.size(); i++)
            buildings.add(activities.get(i).toString());

        ArrayAdapter<String> buildingAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, buildings);
        lv.setAdapter(buildingAdapter);
    }
}