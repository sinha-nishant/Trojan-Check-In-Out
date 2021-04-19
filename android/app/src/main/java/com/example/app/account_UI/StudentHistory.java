package com.example.app.account_UI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.app.R;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.users.StudentAccount;
import com.example.app.users.StudentActivity;

import java.util.ArrayList;
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
        SharedPreferences sp=  getSharedPreferences("sharedPrefs",MODE_PRIVATE);
        uscID = sp.getLong("uscid",0L);
        MutableStudent();
        FbQuery.getStudent(uscID,student);



    }

    public void MutableStudent(){
        final Observer<StudentAccount> obs = new Observer<StudentAccount>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final StudentAccount sa){
                if(sa==null){
                    Log.d("StudentHistory","student mld is null");
                }
                else{
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