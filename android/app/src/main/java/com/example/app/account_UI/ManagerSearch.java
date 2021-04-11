package com.example.app.account_UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.app.R;
import com.example.app.building.Building;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.searchResults;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;


@SuppressWarnings("Convert2Lambda")
public class ManagerSearch extends AppCompatActivity {

    private EditText fName;
    private EditText lName;
    private AutoCompleteTextView building_spinner;
    private AutoCompleteTextView major_spinner;
    private final MutableLiveData<List<Building>> buildingsMLD = new MutableLiveData<>();
    private AlertDialog alertDialog;
    private final Calendar myCalendar = Calendar.getInstance();
    private EditText startDate;
    private EditText endDate;
    private EditText startTime;
    private EditText endTime;
    Button submitBtn;
    Bundle bundle;
    Boolean canSearch=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_search);

        findViewById(R.id.manager_building).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ManagerSearch.this, ManagerBuildings.class);
                startActivity(intent1);
            }
        });

        findViewById(R.id.manager_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(ManagerSearch.this, ManagerHome.class);
                startActivity(intent2);
            }
        });



        fName = findViewById(R.id.first_name_input);
        lName = findViewById(R.id.last_name_input);
        building_spinner = findViewById(R.id.building_spinner);
        major_spinner = findViewById(R.id.major_spinner);
        major_spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.majors_array)));
        buildingsMLDInit();
        DialogInit();
        startDate = findViewById(R.id.start_date_text);
        endDate = findViewById(R.id.end_date_text);
        setDate(startDate);
        setDate(endDate);
        startTime = findViewById(R.id.start_time_text);
        endTime = findViewById(R.id.end_time_text);
        setTime(startTime);
        setTime(endTime);
        submitBtn = findViewById(R.id.submit_button);
        bundle= new Bundle();
        FbQuery.getAllBuildings(buildingsMLD);
    }

    private void buildingsMLDInit(){
        final Observer<List<Building>> building_obs = new Observer<List<Building>>(){
            @Override
            public void onChanged(@Nullable final List<Building> buildings){
                if (buildings != null) {
                    List<String> names= new ArrayList<>();
                    for(Building location: buildings) {
                        names.add(location.getName());
                        ArrayAdapter<String> adp = new ArrayAdapter<>(ManagerSearch.this,android.R.layout.simple_list_item_1,names);
                        building_spinner.setAdapter(adp);
                    }
                }
                else {
                    alertDialog.setMessage("no results for buildings");
                    alertDialog.show();
                }
            }
        };
        buildingsMLD.observe(this, building_obs);
    }

    private void DialogInit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Status of Action");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes",
                new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
        alertDialog = builder.create();
    }

    private void setDate(EditText tv) {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel(tv);
            }
        };

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ManagerSearch.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateDateLabel(EditText tv) {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tv.setText(sdf.format(myCalendar.getTime()));
    }

    private void setTime(EditText tv) {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ManagerSearch.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        tv.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
    }

    public void onSubmit(View v){
        TextInputLayout selected_major = findViewById(R.id.selected_major);
        TextInputLayout selected_building = findViewById(R.id.selected_building);
        int fNameLength=fName.getText().length();
        int lNameLength=lName.getText().length();


        if (fName.getText().length() != 0 && lName.getText().length() != 0){
            bundle.putString("name", "yes");
            bundle.putString("fName", fName.getText().toString());
            bundle.putString("lName", lName.getText().toString());
            canSearch=true;
        }
        else if(!(fNameLength==0  && lNameLength==0) ){
            alertDialog.setMessage("Do not partial enter one name field.Fill both first and last name");
            alertDialog.show();
            canSearch=false;
            return;
        }

        if (!(selected_major.getEditText().getText().toString().isEmpty())) {
            bundle.putString("major", "yes");
            bundle.putString("majorValue", selected_major.getEditText().getText().toString());
            canSearch=true;
        }

        if (!startDate.getText().toString().isEmpty() &&
                !endDate.getText().toString().isEmpty() &&
                !startTime.getText().toString().isEmpty() &&
                !endTime.getText().toString().isEmpty() &&
                !(selected_building.getEditText().getText().toString().isEmpty())){
            bundle.putString("building", "yes");
            bundle.putString("startDate", startDate.getText().toString());
            bundle.putString("endDate", endDate.getText().toString());
            bundle.putString("startTime", startTime.getText().toString());
            bundle.putString("endTime", endTime.getText().toString());
            bundle.putString("buildingValue", selected_building.getEditText().getText().toString());
            canSearch=true;

        }
        else if(startDate.getText().toString().isEmpty() &&
                endDate.getText().toString().isEmpty() &&
                startTime.getText().toString().isEmpty() &&
                endTime.getText().toString().isEmpty() &&
                !(selected_building.getEditText().getText().toString().isEmpty())){
            bundle.putString("building", "yes");
            bundle.putString("buildingValue", selected_building.getEditText().getText().toString());
            canSearch=true;
        }
        else if(!(startDate.getText().length()==0 && endDate.getText().length()==0 && startTime.getText().length()==0
            && endTime.getText().length()==0 && selected_building.getEditText().getText().length()==0)){
            alertDialog.setMessage("Do not partial enter building,date and time fields");
            alertDialog.show();
            canSearch=false;
            return;
        }

        if(canSearch){
            openSearch();
        }
        else{
            alertDialog.setMessage("Please fill in the filters accurately");
            alertDialog.show();
        }

    }

    private void openSearch(){
        Intent i  = new Intent(this, searchResults.class);
        i.putExtras(bundle);
        startActivity(i);
    }

//    BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView2);

//    BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
//    NavController navController = Navigation.findNavController(this,  R.id.fragment2);
//
//    NavigationUI.setupWithNavController(bottomNavigationView, navController);


//
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()){
//                case R.id.manager_building:
//                    Intent intent1 = new Intent(ManagerSearch.this, ManagerBuildings.class);
//                    startActivity(intent1);
//                    return false;
//
//                //case R.id.manager_search:
//
//                case R.id.manager_home:
//                    Intent intent2 = new Intent(ManagerSearch.this, ManagerHome.class);
//                    startActivity(intent2);
//                    return false;
//            }
//
//        }
//    });






}