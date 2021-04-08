package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.app.account_UI.ManagerSearch;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.users.StudentAccount;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class searchResults extends AppCompatActivity {
    ListView lv;
    MutableLiveData<List<StudentAccount>> studentsMLD= new MutableLiveData<>();
    AlertDialog alertDialog;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        lv= findViewById(R.id.searchLv);
        MutableStudent();
        DialogInit();
        bundle=this.getIntent().getExtras();
        try {
            search();
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        FbQuery.search("Undeclared",studentsMLD);
    }

    public void MutableStudent(){
        final Observer<List<StudentAccount>> student_obs = new Observer<List<StudentAccount>>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final List<StudentAccount> accounts){
                Log.d("search", "search mld has changed");
                if(accounts==null||accounts.size()==0){
                    alertDialog.setMessage("No results found");
                    alertDialog.show();
                }
                else{
                    SearchListAdaptor adaptor= new SearchListAdaptor(searchResults.this, R.layout.adapter_view_layout,accounts);
                    lv.setAdapter(adaptor);
                }
            }
        };
        studentsMLD.observe(this, student_obs);
    }

    private void DialogInit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Status of Action");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes",
                new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {
                        openManagerSearch();

                    }
                });
        alertDialog = builder.create();
    }

    public void search() throws ParseException {
        String type= bundle.getString("type");
        if(type.equals("name")){
            String fName= bundle.getString("fName");
            String lName= bundle.getString("lName");
            FbQuery.search(fName,lName,studentsMLD);
        }
        else if(type.equals("major")){
            String major= bundle.getString("major");
            FbQuery.search(major,studentsMLD);
        }
        else{
            String startDate= bundle.getString("startDate");
            String endDate= bundle.getString("endDate");
            String startTime= bundle.getString("startTime");
            String endTime= bundle.getString("endTime");
            String building= bundle.getString("building");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy hh:mm");
            String startString= startDate+ " "+ startTime;
            String endString= endDate+ " "+ endTime;
            Date initialDate = sdf.parse(startString);
            Date finalDate = sdf.parse(endString);
            FbQuery.search(building,initialDate,finalDate,studentsMLD);

        }
    }

    private void openManagerSearch(){
        Intent i= new Intent(this, ManagerSearch.class);
        startActivity(i);
    }
}