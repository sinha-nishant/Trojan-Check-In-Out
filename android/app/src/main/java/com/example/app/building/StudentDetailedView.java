package com.example.app.building;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.app.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.text.Html.fromHtml;

public class StudentDetailedView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detailed_view);

        Bundle extras = getIntent().getExtras();
        TextView studentIdTextView = findViewById(R.id.studentProfileID);
        studentIdTextView.setText( "USC ID: "+extras.getString("STUDENT_ID"));
        TextView studentNameTextView = findViewById(R.id.studentFullName);
        studentNameTextView.setText(extras.getString("STUDENT_NAME"));
        TextView studentMajorTextView = findViewById(R.id.studentProfileMajor);
        studentMajorTextView.setText("Major: "+ extras.getString("STUDENT_MAJOR"));
        TextView studentHistoryTextView = findViewById(R.id.studentProfileHistory);
        studentHistoryTextView.setMovementMethod(new ScrollingMovementMethod());
        ImageView studentProfileImage = findViewById(R.id.studentImageView);
        String studentEmail = extras.getString("STUDENT_EMAIL");
        TextView studentEmailTextView = findViewById(R.id.studentProfileEmail);
        studentEmailTextView.append(studentEmail);
        Glide.with(this).load(extras.getString("STUDENT_IMAGE")).placeholder(R.drawable.profile_blank).into(studentProfileImage);
        studentHistoryTextView.setText("");
        for(int i =0;i<BuildingStudents.studentActivityList.size();i++){
            String buildingName ="<b>"+ BuildingStudents.studentActivityList.get(i).getBuildingName()+"</b>";
            String checkInTime =  "Check In: ";
            String checkOutTime = "Check Out: Hasn't Checked Out";
            String dateFormat = "MMMM dd, yyyy";
            String timeFormat = "hh:mm a";
            TimeZone timeZone = TimeZone.getTimeZone("US/Pacific");
            String formattedCheckInDate = getTodayDate(dateFormat,timeZone, BuildingStudents.studentActivityList.get(i).getCheckInTime());
            String formattedCheckInTime = getCurrentTime(timeFormat,timeZone, BuildingStudents.studentActivityList.get(i).getCheckInTime());
            checkInTime+= formattedCheckInDate+" "+formattedCheckInTime;
            if( BuildingStudents.studentActivityList.get(i).getCheckOutTime()!=null){
                String formattedCheckOutDate = getTodayDate(dateFormat,timeZone,BuildingStudents.studentActivityList.get(i).getCheckOutTime());
                String formattedCheckOutTime = getCurrentTime(timeFormat,timeZone,BuildingStudents.studentActivityList.get(i).getCheckOutTime());
                checkOutTime = "Check Out: "+ formattedCheckOutDate + " "+ formattedCheckOutTime;
            }
            String text = "";
            if(i==BuildingStudents.studentActivityList.size()-1){
                text = buildingName+"<br/>"+checkInTime+"<br/>"+checkOutTime;
            }else{
                text = buildingName+"<br/>"+checkInTime+"<br/>"+checkOutTime+"<br/><br/><br/>";
            }

            studentHistoryTextView.append(fromHtml(text,1));

        }
    }
    public String getCurrentTime(String timeFormat, TimeZone timeZone, Date date)
    {
        /* Specifying the format */
        DateFormat dateFormat = new SimpleDateFormat(timeFormat);
        /* Setting the Timezone */
        Calendar cal = Calendar.getInstance(timeZone);

        dateFormat.setTimeZone(cal.getTimeZone());
        /* Picking the time value in the required Format */
        String currentTime = dateFormat.format(date);
        return currentTime;
    }


    public String getTodayDate(String dateFormat, TimeZone timeZone,
    Date date)
    {
        /* Specifying the format */
        DateFormat todayDateFormat = new SimpleDateFormat(dateFormat);
        /* Setting the Timezone */
        todayDateFormat.setTimeZone(timeZone);
        /* Picking the date value in the required Format */
        String strTodayDate = todayDateFormat.format(date);
        return strTodayDate;
    }
}