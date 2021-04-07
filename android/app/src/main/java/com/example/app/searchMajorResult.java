package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ListView;

import com.example.app.firebaseDB.FbQuery;
import com.example.app.users.StudentAccount;

import java.util.List;

public class searchMajorResult extends AppCompatActivity {
    ListView lv;
    MutableLiveData<List<StudentAccount>> studentsMLD= new MutableLiveData<>();
    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_major_result);
        lv= findViewById(R.id.majorLv);
        MutableStudent();
        FbQuery.search("Undeclared",studentsMLD);
    }

    public void MutableStudent(){
        final Observer<List<StudentAccount>> student_obs = new Observer<List<StudentAccount>>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final List<StudentAccount> accounts){
                if(accounts==null){
                    alertDialog.setMessage("No results found");
                    alertDialog.show();
                }
                else{
                    MajorListAdaptor adaptor= new MajorListAdaptor(searchMajorResult.this, R.layout.adapter_view_layout,accounts);
                    lv.setAdapter(adaptor);
                }
            }
        };
        studentsMLD.observe(this, student_obs);
    }
}