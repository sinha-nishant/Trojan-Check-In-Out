package com.example.app.account_UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.example.app.R;
import com.example.app.building.Building;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.searchResults;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

public class ManagerSearch extends AppCompatActivity {
    private EditText fName;
    private EditText lName;
    private Spinner building_spinner;
    private Spinner major_spinner;
    private MutableLiveData<List<Building>> buildingsMLD= new MutableLiveData<List<Building>>();
    private AlertDialog alertDialog;
    private Calendar myCalendar = Calendar.getInstance();
    private EditText startDate;
    private EditText endDate;
    private EditText startTime;
    private EditText endTime;
    Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_search);
        fName= findViewById(R.id.first_name_input);
        lName= findViewById(R.id.last_name_input);
        building_spinner= findViewById(R.id.building_spinner);
        major_spinner= findViewById(R.id.major_spinner);
        buildingsMLDInit();
        DialogInit();
        startDate= findViewById(R.id.start_date_text);
        endDate= findViewById(R.id.end_date_text);
        setDate(startDate);
        setDate(endDate);
        startTime = findViewById(R.id.start_time_text);
        endTime = findViewById(R.id.end_time_text);
        setTime(startTime);
        setTime(endTime);
        submitBtn=findViewById(R.id.submit_button);
        FbQuery.getAllBuildings(buildingsMLD);
    }

    private void buildingsMLDInit(){
        final Observer<List<Building>> building_obs = new Observer<List<Building>>(){
            @Override
            public void onChanged(@Nullable final List<Building> buildings){
                if(buildings!=null){
                    List<String> names= new ArrayList<>();
                    names.add("empty");
                    for(Building location: buildings){
                        names.add(location.getName());
                        ArrayAdapter<String> adp = new ArrayAdapter<> (ManagerSearch.this,android.R.layout.simple_list_item_1,names);
                        building_spinner.setAdapter(adp);
                    }

                }
                else{
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
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {



                    }
                });
        alertDialog = builder.create();
    }

    private void setDate(EditText tv){
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel(tv);
            }

        };
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ManagerSearch.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateDateLabel(EditText tv) {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tv.setText(sdf.format(myCalendar.getTime()));
    }

    private void setTime(EditText tv){
        tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ManagerSearch.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                        tv.setText( selectedHour + ":" + selectedMinute);
                        tv.setText(String.format("%02d:%02d", selectedHour,selectedMinute));

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
    }

    public void onSubmit(View v){
        if(fName.getText().length()!=0 && lName.getText().length()!=0){
            Bundle bundle= new Bundle();
            bundle.putString("type","name");
            bundle.putString("fName",fName.getText().toString());
            bundle.putString("lName",lName.getText().toString());
            openSearch(bundle);
        }
        else if(!major_spinner.getSelectedItem().toString().equals("empty")){
            Bundle bundle= new Bundle();
            bundle.putString("type","major");
            bundle.putString("major",major_spinner.getSelectedItem().toString());
            openSearch(bundle);
        }
        else if(!startDate.getText().toString().equals("dd/mm/yy")&&
                !endDate.getText().toString().equals("dd/mm/yy")&&
                !startTime.getText().toString().equals("hh:mm")&&
                !endTime.getText().toString().equals("hh:mm")&&
                !building_spinner.getSelectedItem().toString().equals("empty")){
            Bundle bundle= new Bundle();
            bundle.putString("type","building");
            bundle.putString("startDate",startDate.getText().toString());
            bundle.putString("endDate",endDate.getText().toString());
            bundle.putString("startTime",startTime.getText().toString());
            bundle.putString("endTime",endTime.getText().toString());
            bundle.putString("building",building_spinner.getSelectedItem().toString());
            openSearch(bundle);

        }

    }

    private void openSearch(Bundle bundle){
        Intent i  = new Intent(this, searchResults.class);
        i.putExtras(bundle);
        startActivity(i);
    }


}