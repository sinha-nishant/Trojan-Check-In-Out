package com.example.app.building;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.app.R;
import com.example.app.account_UI.StudentProfile;
import com.example.app.firebaseDB.FCM;
import com.example.app.firebaseDB.FbCheckInOut;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.services.CheckInOut;
import com.example.app.users.StudentAccount;
import com.example.app.users.StudentActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.text.Html.fromHtml;

public class StudentDetailedView extends AppCompatActivity {
    private AlertDialog kickOutConfirmationMessage;
    private AlertDialog.Builder builderForDoubleCheck;
    private AlertDialog.Builder builder;
    private StudentActivity sa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detailed_view);
        builderForDoubleCheck= new AlertDialog.Builder(this);
        builder= new AlertDialog.Builder(this);
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
        ImageButton btn = findViewById(R.id.kickOutButton);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //display alert to kick out student
                doubleCheckMessage("Kick Out Student", "Are you sure you want to kick out "+extras.getString("STUDENT_NAME"));
            }
        });
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
    public void doubleCheckMessage(String title, String message){

        builderForDoubleCheck.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //kick out student
                        kickOutStudent();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        kickOutConfirmationMessage=builderForDoubleCheck.create();
        //stop loading bar
        kickOutConfirmationMessage.show();
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

    public void kickOutStudent(){
        MutableLiveData<StudentAccount> studentMLD = new MutableLiveData<>();
        MutableLiveData<Boolean> checkOutMLD = new MutableLiveData<>();
        Bundle extras = getIntent().getExtras();
        Long retrieveID =Long.parseLong(extras.getString("STUDENT_ID"));
        final Observer<Boolean> checkOutObserver = new Observer<Boolean>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final Boolean success){
                if(success){ //student is checked in  display checkin message
                   //display new alert saying student was kicked out
                    builder.setTitle("Success")
                            .setMessage("Student Kicked Out")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // switch back to list of students
                                    goToStudentList();
                                }
                            });
                    kickOutConfirmationMessage=builder.create();
                    kickOutConfirmationMessage.show();
                }else { //wasn't able to check in student
                   //student wasn't kicked out because student is not in the building
                    builder.setTitle("Error")
                            .setMessage("Student Not Kicked Out")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // stay on page
                                }
                            });
                    kickOutConfirmationMessage=builder.create();
                    //stop loading bar
                    kickOutConfirmationMessage.show();
                }
            }
        };
        checkOutMLD.observe(this, checkOutObserver);
        final Observer<StudentAccount> studentAccountObserver = new Observer<StudentAccount>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final StudentAccount student){
                // check last index of studentactivity list
                List<StudentActivity> sa_list = student.getActivity();
                if(!sa_list.isEmpty()) {//no activity so check in if occupancy isn't full
                    sa = sa_list.get(sa_list.size() - 1);
                    Date kickOutDate = new Date();
                    Log.d("Kick Out Date", kickOutDate.toString());
                    FbCheckInOut.checkOut(retrieveID,sa,kickOutDate,checkOutMLD);

                    // Send notification to kicked out user
                    FCM.notifyKickOut(student.getFcmToken());
                }
            }
        };
        studentMLD.observe(this,studentAccountObserver);
        FbQuery.getStudent(retrieveID, studentMLD);
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
    public void goToStudentList() {
        Intent i = new Intent(this, BuildingStudents.class);
        Bundle extras = getIntent().getExtras();
        i.putExtra("buildingName", extras.getString("buildingNameDetail"));
        startActivity(i);
    }
}