package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ListView;

import com.example.app.firebaseDB.FbQuery;
import com.example.app.users.StudentAccount;

import java.util.List;

public class searchResults extends AppCompatActivity {
    ListView lv;
    MutableLiveData<List<StudentAccount>> studentsMLD= new MutableLiveData<>();
    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        lv= findViewById(R.id.searchLv);
        MutableStudent();
        DialogInit();
        FbQuery.search("Undeclared",studentsMLD);
    }

    public void MutableStudent(){
        final Observer<List<StudentAccount>> student_obs = new Observer<List<StudentAccount>>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final List<StudentAccount> accounts){
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

                    }
                });
        alertDialog = builder.create();
    }
}