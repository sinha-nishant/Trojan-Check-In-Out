package com.example.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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

    AlertDialog alertDialog;

    // TODO: Rename and change types of parameters
    Button btnQR, btnHistory, btnSignOut, btnDelete;
    String email;
    String uscID;
    private ProgressBar pb;

    public StudentProfileMenu() {
        // Required empty public constructor
    }

    public StudentProfileMenu(String em,String id) {
        email = em;
        uscID=id;
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
        SharedPreferences sp=  getContext().getSharedPreferences("sharedPrefs",getActivity().MODE_PRIVATE);
        Long id = sp.getLong("uscid",0L);
        FirebaseTest.search(id,student);
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
                StudentAccount sa= student.getValue();
                sa.delete(delete_success);
//                openHomePage();
            }
        });
    }
    public void openQR() {
        Intent i = new Intent(getActivity(), QRScanTest.class);
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("uscID", uscID);
        i.putExtras(bundle);
        startActivity(i);
    }

    public void openStudentHistory(String email,String id) {
        Intent i = new Intent(getActivity(), StudentHistory.class);
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("uscID",id);
        i.putExtras(bundle);
        startActivity(i);
    }

    public void openHomePage() {
        Intent i = new Intent(getActivity(), JohnTest.class);
        /*
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        i.putExtras(bundle);
        */
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
}