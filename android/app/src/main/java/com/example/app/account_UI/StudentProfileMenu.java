package com.example.app.account_UI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.app.R;
import com.example.app.firebaseDB.FbCheckInOut;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.log_create.LogInOut;
import com.example.app.pre_login_UI.StartPage;
import com.example.app.users.StudentAccount;
import com.example.app.users.StudentActivity;

import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudentProfileMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentProfileMenu extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    MutableLiveData<StudentAccount> student= new MutableLiveData<>();
    MutableLiveData<Integer> delete_success= new MutableLiveData<>();
    MutableLiveData<Boolean> checkOut_success= new MutableLiveData<>();
    StudentAccount deleteStudent;
    Boolean isDelete = false;

    AlertDialog alertDialog;
    AlertDialog checkInOutMessage;
    AlertDialog deleteDialog;

    private StudentActivity sa;


    // TODO: Rename and change types of parameters
    Button btnQR, btnHistory, btnSignOut, btnDelete,manualCheckOut;
    String email;
    String uscID;
    TextView building_name;
    private ProgressBar pb;

    public StudentProfileMenu() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudentProfileMenu.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentProfileMenu newInstance(String param1, String param2) {
        StudentProfileMenu fragment = new StudentProfileMenu();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pb= (ProgressBar) getActivity().findViewById(R.id.progressBar6);
        building_name = (TextView) getActivity().findViewById(R.id.textViewCurrBuilding);

        pb.setVisibility(View.GONE);

        DialogInit();
        DeleteDialog();
        MutableStudent();
        MutableDelete();
        MutableCheckOut();
        SharedPreferences sp=  getContext().getSharedPreferences("sharedPrefs",MODE_PRIVATE);
        Long id = sp.getLong("uscid",123456790L);
        uscID=id.toString();
        FbQuery.getStudent(id,student);
        this.getLifecycle().addObserver(new ForegroundBackgroundListener());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_profile_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnQR = (Button)getView().findViewById(R.id.studentMenuQR);
        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQR();
            }
        });

        btnHistory = (Button)getView().findViewById(R.id.studentMenuHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStudentHistory(email,uscID);
            }
        });

        btnSignOut = (Button)getView().findViewById(R.id.studentMenuLogOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DO BACKEND WORK
                disableBtns();
                pb.setVisibility(View.VISIBLE);
                LogInOut.LogOut(getContext());
                pb.setVisibility(View.GONE);
                openHomePage();
            }
        });

        btnDelete = (Button)getView().findViewById(R.id.studentMenuDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DO BACKEND WORK TO DELETE ACCOUNT
                //Ask User to confirm
                disableBtns();
                deleteDialog.show();
            }
        });
        manualCheckOut = (Button)getView().findViewById(R.id.checkOutBtn);
        manualCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableBtns();
               manualCheckOut();

            }
        });
    }
    public void openQR() {
        Intent i = new Intent(getActivity(), QRScan.class);
        startActivity(i);
    }

    public void openStudentHistory(String email,String id) {
        Intent i = new Intent(getActivity(), StudentHistory.class);
        startActivity(i);
    }

    public void openHomePage() {
        Intent i = new Intent(getActivity(), StartPage.class);
        startActivity(i);
    }

    public void DialogInit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Status of Action");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes",
                new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {

                        openHomePage();

                    }
                });
        alertDialog = builder.create();
    }

    public void DeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Confirmation of Delete Request");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes",
                new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {

                        pb.setVisibility(View.VISIBLE);
                        isDelete=true;
                        FbQuery.getStudent(Long.valueOf((uscID)),student);

                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {
                        enableBtns();
                    }
                });
        deleteDialog = builder.create();
    }

    public void MutableStudent(){
        final Observer<StudentAccount> student_obs = new Observer<StudentAccount>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final StudentAccount sa){
                if(sa==null){
                    Log.d("StudentProfileMenu","student mld is null");
                }
                else{
                    int activity_size= sa.getActivity().size();
                    if(activity_size==0){
                        building_name.setText("You are not checked into a building currently");
                    }
                    else{
                        StudentActivity act=sa.getActivity().get(activity_size-1);
                        if(act.getCheckOutTime()==null){
                            building_name.setText("You are now checked into a building currently "+ act.getBuildingName());
                        }
                        else{
                            building_name.setText("You are not checked into a building currently");
                        }
                    }
                    if(isDelete){
                        StudentAccount acc = student.getValue();
                        //
                        int size = acc.getActivity().size();
                        if(size>0 && acc.getActivity().get(size-1).getCheckOutTime()==null){
                            deleteStudent=sa;
                            FbCheckInOut.checkOut(Long.valueOf(uscID),acc.getActivity().get(size-1),new Date(),checkOut_success);
                        }
                        else{
                            sa.delete(delete_success);
                        }

                    }
                }

            }

        };
       student.observe(this, student_obs);
    }

    public void MutableDelete(){
        final Observer<Integer> delete_obs = new Observer<Integer>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final Integer result){
                if(result==null){
                    Log.d("StudentProfileMenu","delete mld is null");
                    return;
                }
                if(result==0){
                    enableBtns();
                    alertDialog.setMessage("We could not check you out of your last building. Delete failed");
                    alertDialog.show();
                }
                else if(result==1){
                    enableBtns();
                    alertDialog.setMessage("Unsuccessful in deleting your account");
                    alertDialog.show();
                }
                else{
                    enableBtns();
                    pb.setVisibility(View.GONE);
                    alertDialog.setMessage("Successful in deleting your account");
                    alertDialog.show();
                    LogInOut.LogOut(getContext());

                }

            }

        };
        delete_success.observe(this, delete_obs);
    }

    public void MutableCheckOut(){
        final Observer<Boolean> checkOutObs = new Observer<Boolean>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final Boolean result){
                if(result==null){
                    Log.d("StudentProfileMenu","check out mld is null");
                    return;
                }
                if(result){
                    deleteStudent.delete(delete_success);
                }
                else{
                    delete_success.setValue(0);
                }
            }

        };
        checkOut_success.observe(this, checkOutObs);
    }

    public void manualCheckOut(){
        AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
        AlertDialog.Builder doubleCheck = new AlertDialog.Builder(getContext());
        MutableLiveData<Boolean> checkOutMLD = new MutableLiveData<>();
        MutableLiveData<StudentAccount> studentMLD = new MutableLiveData<>();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("sharedPrefs",MODE_PRIVATE);
        Long retrieveID = sharedPreferences.getLong("uscid",0L);
        doubleCheck.setTitle("Eligible for Check Out")
                .setMessage("Are you sure you want to check out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //check in student since they double checked
                        Date date = new Date();
                        FbCheckInOut.checkOut(retrieveID,sa,date,checkOutMLD);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        builder.setTitle("Check Out Attempt Canceled")
                                .setMessage("You were not checked out of the building.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do whatever reflects wireframe the best(such as switching pages
                                    }
                                });
                        checkInOutMessage=builder.create();
                        //stop loading bar
                        checkInOutMessage.show();
                        enableBtns();

                    }
                })
        ;


        final Observer<Boolean> checkOutObserver = new Observer<Boolean>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final Boolean success){
                if(success){ //student is checked in  display checkin message
                    builder.setTitle("Check Out Success")
                            .setMessage("You are now checked out!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // do whatever reflects wireframe the best(such as switching pages
                                    enableBtns();
                                }
                            });
                    checkInOutMessage=builder.create();
                    //stop loading bar
                    checkInOutMessage.show();
                    FbQuery.getStudent(Long.valueOf(uscID),student);

                }else { //wasn't able to check in student
                    builder.setTitle("Check Out Failure")
                            .setMessage("Something went wrong with our database. Please try again later.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // do whatever reflects wireframe the best(such as switching pages
                                    enableBtns();
                                }
                            });
                    checkInOutMessage=builder.create();
                    //stop loading bar
                    checkInOutMessage.show();
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
                    sa = sa_list.get(sa_list.size()-1);
                    Date date = new Date();
                    if(sa.getCheckOutTime()==null){
                        //add double check message
                        checkInOutMessage=doubleCheck.create();
                        //stop loading bar
                        enableBtns();
                        checkInOutMessage.show();

                    }else{
                        builder.setTitle("Check Out Failure")
                                .setMessage("You are not checked into a building")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do whatever reflects wireframe the best(such as switching pages
                                        enableBtns();
                                    }
                                });
                        checkInOutMessage=builder.create();
                        //stop loading bar
                        checkInOutMessage.show();
                    }
                }else{
                    builder.setTitle("Check Out Failure")
                            .setMessage("You are not checked into a building")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // do whatever reflects wireframe the best(such as switching pages
                                    enableBtns();
                                }
                            });
                    checkInOutMessage=builder.create();
                    //stop loading bar
                    checkInOutMessage.show();
                }
            }
        };
        studentMLD.observe(this,studentAccountObserver);
        FbQuery.getStudent(retrieveID, studentMLD);

    }

    public void disableBtns(){
        btnQR.setEnabled(false);
        btnHistory.setEnabled(false);
        btnSignOut.setEnabled(false);
        btnDelete.setEnabled(false);
        manualCheckOut.setEnabled(false);
    }

    public void enableBtns(){
        btnQR.setEnabled(true);
        btnHistory.setEnabled(true);
        btnSignOut.setEnabled(true);
        btnDelete.setEnabled(true);
        manualCheckOut.setEnabled(true);
    }


    class ForegroundBackgroundListener implements LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        public void connectListener() {
            SharedPreferences sp=  getContext().getSharedPreferences("sharedPrefs",getActivity().MODE_PRIVATE);
            Long id = sp.getLong("uscid",0L);
            FbQuery.getStudent(id,student);

        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        public void disconnectListener() {
            Log.d("ProcessLog", "APP IS ON Background");

        }
    }
}