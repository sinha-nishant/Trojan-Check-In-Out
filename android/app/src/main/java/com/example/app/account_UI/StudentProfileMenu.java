package com.example.app.account_UI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

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
    Boolean isDelete = false;

    AlertDialog alertDialog;
    AlertDialog checkInOutMessage;


    // TODO: Rename and change types of parameters
    Button btnQR, btnHistory, btnSignOut, btnDelete,manualCheckOut;
    String email;
    String uscID;
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
        pb.setVisibility(View.GONE);

        DialogInit();
        MutableStudent();
        MutableBoolean();
        SharedPreferences sp=  getContext().getSharedPreferences("sharedPrefs",MODE_PRIVATE);
        Long id = sp.getLong("uscid",123456790L);
        Log.d("StudentProfile",id+"ending");
        uscID=id.toString();
        FbQuery.getStudent(id,student);
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
                pb.setVisibility(View.VISIBLE);
                isDelete=true;
                if(uscID==null){
                    Log.d("id","id is null");
                }
                Log.d("id",uscID);
                FbQuery.getStudent(Long.valueOf((uscID)),student);

            }
        });
        manualCheckOut = (Button)getView().findViewById(R.id.checkOutBtn);
        manualCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Calling manual check out","line 156");
               manualCheckOut();
                Log.d("After manualcheckout","line 158");

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

    public void MutableStudent(){
        final Observer<StudentAccount> obs = new Observer<StudentAccount>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final StudentAccount sa){
                if(sa==null){
                    return;
                }
                else{
                    Log.d("student","student found");
                    if(isDelete){
                        StudentAccount acc = student.getValue();
                        sa.delete(delete_success);
                    }
                }

            }

        };
        student.observe(this, obs);
    }

    public void MutableBoolean(){
        final Observer<Integer> obs2 = new Observer<Integer>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final Integer result){
                if(result==null){
                    return;
                }
                if(result==0){
                    alertDialog.setMessage("We could not check you out of your last building. Delete failed");
                    alertDialog.show();
                }
                else if(result==1){
                    alertDialog.setMessage("Unsuccessful in deleting your account");
                    alertDialog.show();
                }
                else{
                    pb.setVisibility(View.GONE);
                    alertDialog.setMessage("Successful in deleting your account");
                    alertDialog.show();
                    LogInOut.LogOut(getContext());

                }

            }

        };
        delete_success.observe(this, obs2);
    }

    public void manualCheckOut(){
        AlertDialog.Builder builder= new AlertDialog.Builder(getContext());

        MutableLiveData<Boolean> checkOutMLD = new MutableLiveData<>();
        MutableLiveData<StudentAccount> studentMLD = new MutableLiveData<>();


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
                                }
                            });
                    checkInOutMessage=builder.create();
                    //stop loading bar
                    checkInOutMessage.show();
                }else { //wasn't able to check in student
                    builder.setTitle("Check Out Failure")
                            .setMessage("Something went wrong with our database. Please try again later.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // do whatever reflects wireframe the best(such as switching pages
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
                    StudentActivity sa = sa_list.get(sa_list.size()-1);
                    Date date = new Date();
                    if(sa.getCheckOutTime()==null){
                        FbCheckInOut.checkOut(student.getUscID(),sa,date,checkOutMLD);


                    }else{
                        builder.setTitle("Check Out Failure")
                                .setMessage("You are not checked into a building")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do whatever reflects wireframe the best(such as switching pages
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
                                }
                            });
                    checkInOutMessage=builder.create();
                    //stop loading bar
                    checkInOutMessage.show();
                }
            }
        };
        studentMLD.observe(this,studentAccountObserver);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("sharedPrefs",MODE_PRIVATE);

        Long retrieveID = sharedPreferences.getLong("uscid",0L);
        FbQuery.getStudent(retrieveID, studentMLD);

    }
}