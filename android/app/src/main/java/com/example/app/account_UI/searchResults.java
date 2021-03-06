package com.example.app.account_UI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.app.R;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.services.SearchListAdaptor;
import com.example.app.users.StudentAccount;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class searchResults extends AppCompatActivity {
    private ListView lv;
    private final MutableLiveData<List<StudentAccount>> nameMLD = new MutableLiveData<>();
    private final MutableLiveData<List<StudentAccount>> majorMLD = new MutableLiveData<>();
    private final MutableLiveData<List<StudentAccount>> buildingMLD = new MutableLiveData<>();
    private final MutableLiveData<StudentAccount> idMLD = new MutableLiveData<>();
    private final MutableLiveData<Boolean> finished=new MutableLiveData<>();
    private Integer total=0;
    private Integer done=0;
    private Set<StudentAccount> searchResults= new HashSet<>();
    private Boolean isName,isMajor,isBuilding, isID;
    private AlertDialog alertDialog;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        lv = findViewById(R.id.searchLv);

        DialogInit();
        bundle = this.getIntent().getExtras();

        MutableSearchInit(nameMLD);
        MutableSearchInit(majorMLD);
        MutableSearchInit(buildingMLD);
        MutableSearchID(idMLD);
        MutableFinished();
        try {
            searchMany();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void MutableFinished() {
        final Observer<Boolean> finished_obs = new Observer<Boolean>() {
            @Override
            public void onChanged(@javax.annotation.Nullable final Boolean isComplete) {
                if(searchResults.isEmpty()){
                    alertDialog.setMessage("No results found");
                    alertDialog.show();
                }
                else{
                    List<StudentAccount> accounts= new ArrayList<>(searchResults);
                    SearchListAdaptor adaptor = new SearchListAdaptor(searchResults.this, R.layout.adapter_view_layout, accounts);
                    lv.setAdapter(adaptor);
                }
            }
        };
        finished.observe(this, finished_obs);
    }

    public void MutableSearchInit(MutableLiveData<List<StudentAccount>> mld){
        final Observer<List<StudentAccount>>search_obs = new Observer<List<StudentAccount>>() {
            @Override
            public void onChanged(@javax.annotation.Nullable final List<StudentAccount> accounts) {
                if (accounts == null || accounts.size() == 0) {
                    if (!searchResults.isEmpty())
                    {
                        searchResults=new HashSet<>();
                    }
                    finished.setValue(true);
                }
                else {
                    if (finished.getValue()!=null){
                        return;
                    }
                    if (done==0){
                        searchResults= new HashSet<>(accounts);
                    }
                    else{
                        Set<StudentAccount> updateResults= new HashSet<>();
                        for (StudentAccount acc: accounts){
                            if(searchResults.contains(acc)){
                                updateResults.add(acc);
                            }
                        }
                        searchResults= updateResults;
                    }
                    done++;
                    if (done.equals(total)){
                        finished.setValue(true);
                    }
                }
            }
        };
        mld.observe(this, search_obs);
    }

    public void MutableSearchID(MutableLiveData<StudentAccount> mld){
        final Observer<StudentAccount>id_obs = new Observer<StudentAccount>() {
            @Override
            public void onChanged(@javax.annotation.Nullable final StudentAccount account) {
                if (account == null) {
                    if (!searchResults.isEmpty())
                    {
                        searchResults=new HashSet<>();
                    }
                    finished.setValue(true);
                }
                else {
                    if (finished.getValue()!=null){
                        return;
                    }
                    if (done==0){
                        searchResults= new HashSet<>();
                        searchResults.add(account);
                    }
                    else{
                        Set<StudentAccount> updateResults= new HashSet<>();
                        if(searchResults.contains(account)){
                            updateResults.add(account);
                        }
                        searchResults= updateResults;
                    }
                    done++;
                    if (done.equals(total)){
                        finished.setValue(true);
                    }
                }
            }
        };
        mld.observe(this, id_obs);
    }

    private void DialogInit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Return to Search");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirm",
                new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        openManagerSearch();

                    }
                });
        alertDialog = builder.create();
    }

    public void searchMany() throws ParseException {

        if(bundle.containsKey("name")){
            isName=false;
            total++;
        }
        if(bundle.containsKey("major")){
            isMajor=false;
            total++;
        }
        if(bundle.containsKey("building")){
            isBuilding=false;
            total++;
        }
        if(bundle.containsKey("id")){
            isID=false;
            total++;
        }
        if(isName!=null){
            String fName = bundle.getString("fName");
            String lName = bundle.getString("lName");
            FbQuery.search(fName, lName, nameMLD);
        }
        if(isMajor!=null){
            String major = bundle.getString("majorValue");//Todo  update key
            FbQuery.search(major, majorMLD);
        }
        if(isBuilding!=null){
            if(!bundle.containsKey("startDate")){
                String building = bundle.getString("buildingValue");
                FbQuery.searchByBuilding(building,buildingMLD);
            }
            else{
                String startDate = bundle.getString("startDate");
                String endDate = bundle.getString("endDate");
                String startTime = bundle.getString("startTime");
                String endTime = bundle.getString("endTime");
                String building = bundle.getString("buildingValue");
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm");
                String startString = startDate + " " + startTime;
                String endString = endDate + " " + endTime;
                Date initialDate = sdf.parse(startString);
                Date finalDate = sdf.parse(endString);
                FbQuery.search(building, initialDate, finalDate, buildingMLD);
            }

        }
        if(isID!=null){
            String id = bundle.getString("idValue");
            FbQuery.search(Long.valueOf(id),idMLD);
        }
    }

    private void openManagerSearch() {
        Intent i = new Intent(this, ManagerSearch.class);
        startActivity(i);
    }
}