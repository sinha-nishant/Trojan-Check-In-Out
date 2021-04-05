package com.example.app;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
//import android.widget.ListView;

import com.example.app.account_UI.StudentHistory;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.users.StudentAccount;
import com.example.app.users.StudentActivity;

import java.util.ArrayList;
import java.util.List;

public class searchMajorResult extends AppCompatActivity {
    ListView lv;
    MutableLiveData<List<StudentAccount>> studentsMLD= new MutableLiveData<List<StudentAccount>>();
    AlertDialog alertDialog;
    ArrayList<String> results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_major_result);
        lv= findViewById(R.id.majorLv);
        MutableStudent();
        DialogInit();
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
//                    results=new ArrayList<>();
//                    for(StudentAccount acc: accounts){
//                        results.add(acc.toString());
//                    }
//                    ArrayAdapter<String> buildingAdapter = new ArrayAdapter(searchMajorResult.this, android.R.layout.simple_list_item_1, results);
//                    lv.setAdapter(buildingAdapter);
//                    alertDialog.setMessage("retrieved results");
//                    alertDialog.show();
                    MajorListAdaptor adaptor= new MajorListAdaptor(searchMajorResult.this, R.layout.adapter_view_layout,accounts);
                    lv.setAdapter(adaptor);

                }

            }

        };
        studentsMLD.observe(this, student_obs);
    }

    public void DialogInit(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        builder.setTitle("Status of Action");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes",
                new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {


                    }
                });
        alertDialog = builder.create();
    }
}

