package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StudentHistory extends AppCompatActivity {

    ListView lv;
    ArrayList<String> buildings;
    MutableLiveData<StudentAccount> student= new MutableLiveData<>();
    Long uscID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_history);

        //BACKEND: Load in list of student activities here

        Intent intent = getIntent();
        Bundle bundle= intent.getExtras();
        uscID= Long.valueOf(bundle.getString("uscID"));
        MutableStudent();
        FirebaseTest.search(uscID,student);



    }

    public void MutableStudent(){
        final Observer<StudentAccount> obs = new Observer<StudentAccount>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final StudentAccount sa){
                if(sa==null){
                    return;
                }
                else{
                    Log.d("student","student found");
                    List<StudentActivity> activities= sa.getActivity();

                    lv = (ListView)findViewById(R.id.buildingList);
                    buildings = new ArrayList<>(activities.size());

                    for(int i = 0; i < activities.size(); i++)
                        buildings.add(activities.get(i).toString());
                    ArrayAdapter<String> buildingAdapter = new ArrayAdapter(StudentHistory.this, android.R.layout.simple_list_item_1, buildings);
                    lv.setAdapter(buildingAdapter);



                }

            }

        };
        student.observe(this, obs);
    }
}