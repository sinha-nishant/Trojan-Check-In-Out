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
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudentProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentProfileFragment extends Fragment {

    private String str_name, str_email, str_id, str_major;
    private TextView name;
    private Uri profilepic;
    private AlertDialog alertDialog;
    private ProgressBar pb;
    ImageView img;
    Button uploadButton;
    MutableLiveData<StudentAccount> student= new MutableLiveData<>();
    MutableLiveData<Boolean> upload_success= new MutableLiveData<>();
    MutableLiveData<Boolean> Firebase_success= new MutableLiveData<>();
    int SELECT_PICTURE = 200;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StudentProfileFragment() {
        // Required empty public constructor
    }

    public StudentProfileFragment(String email,String id) {
        str_email = email;
        str_id=id;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudentProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentProfileFragment newInstance(String param1, String param2) {
        StudentProfileFragment fragment = new StudentProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //TO BACKEND: use str_email to make your database call, and fill in these fields:
        Log.i("Views","in onCreated");
        AmplifyInit();
        pb= (ProgressBar) getActivity().findViewById(R.id.progressBar6);
//        pb.setVisibility(View.GONE);

        MutableStudent();

        DialogInit();

        MutableBoolean();
        MutableFirebase();
        SharedPreferences sp=  getContext().getSharedPreferences("sharedPrefs",getActivity().MODE_PRIVATE);
        Long id = sp.getLong("uscid",0L);
        //keep shared pref access here
//        pb= (ProgressBar) getActivity().findViewById(R.id.progressBar6);
//        pb.setVisibility(View.GONE);
        FirebaseTest.search(id,student);


        //END BACKEND
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("Views","in onCreatedView");
        return inflater.inflate(R.layout.fragment_student_profile, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name = (TextView)(getView().findViewById(R.id.tvProfileName));
        name.setText("Welcome, " + str_name);

        name = (TextView)(getView().findViewById(R.id.textView13));
        name.setText("USC ID: " + str_id);

        name = (TextView)(getView().findViewById(R.id.textView14));
        name.setText("Email: " + str_email);

        name = (TextView)(getView().findViewById(R.id.textView15));
        name.setText("Major: " + str_major);


        img= (ImageView)(getView().findViewById(R.id.imageView2));
        if(profilepic!=null){
            Glide.with(getActivity()).load(profilepic.toString()).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(img);
        }

        Log.i("Views","in onViewCreated");
    }



    // this function is triggered when
    // the Select Image Button is clicked
    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                profilepic = data.getData();

                Log.i("Image",profilepic.toString());
                if (null != profilepic) {
                    // update the preview image in the layout

                    String uri =profilepic.toString();
                    InputStream exampleInputStream=null;



                    try {
                        exampleInputStream = getActivity().getContentResolver().openInputStream(Uri.parse(uri));
                        if(exampleInputStream==null){
                            Log.i("upload", "stream is null");
                        }
                        else{
                            Log.i("upload", "stream is valid");
                        }


                    } catch (FileNotFoundException e) {
                        Log.i("upload", "error in uri parsing");
                    }
                    if(str_email!=null){
                        Log.i("photo",str_email);
                    }
                    else{
                        Log.i("photo","email is null");
                    }

                    if(upload_success!=null){
                        Log.i("photo",upload_success.toString());
                    }
                    else{
                        Log.i("photo","mutable null");
                    }
                    pb.setVisibility(View.VISIBLE);
                    uploadPhoto.upload(exampleInputStream,str_email,upload_success);


                }

            }
        }
    }

    public void AmplifyInit(){
        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getContext());
        } catch (AmplifyException e) {
            Log.i("MyAmplifyApp", "could not add plugins ");
        }
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

                        // When the user click yes button
                        // then app will close
//                        finish();
//                        if(ImageSet==true){
//                            openProfile();
//                        }
//                        openProfile();//check if created and redirect
                    }
                });
        alertDialog = builder.create();
    }

    public void MutableStudent(){

        final Observer<StudentAccount> obs = new Observer<StudentAccount>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final StudentAccount sa){
                if(sa==null){
                    Log.d("profile","is null");
                    return;
                }
                else{
                    Log.d("profile","in profile onchaged");
                    Log.d("profile",sa.toString());
                    str_name= sa.getFirstName()+ " "+ sa.getLastName();
                    str_email=sa.getEmail();
                    str_id=sa.getUscID().toString();
                    str_major=sa.getMajor();
                    name = (TextView)(getView().findViewById(R.id.tvProfileName));
                    name.setText("Welcome, " + str_name);
                    name = (TextView)(getView().findViewById(R.id.textView15));
                    name.setText("Major: " + str_major);
                    if(sa.getProfilePicture()!=null){
                        profilepic =Uri.parse(sa.getProfilePicture());
                        Log.d("URI",profilepic.toString());
                        img= (ImageView)(getView().findViewById(R.id.imageView2));
                        Glide.with(getActivity()).load(profilepic.toString()).diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true).into(img);
                        Log.d("URI",profilepic.toString());

                    }
                    pb.setVisibility(View.GONE);

                    uploadButton=(Button)(getView().findViewById(R.id.SelectImg));
                    uploadButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            imageChooser();

                        }
                    });
                }

            }

        };
        student.observe(this, obs);
    }

    public void MutableBoolean(){
        final Observer<Boolean> obs2 = new Observer<Boolean>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final Boolean b){
                if(b){
                    if(profilepic==null){
                        Log.d("Check", "profile pic is null");
                    }
                    if(img==null){
                        Log.d("Check", "img is null");
                    }
//                    img= (ImageView)(getView().findViewById(R.id.imageView2));
//                    Glide.with(getActivity()).load(profilepic.toString()).into(img);
//                    alertDialog.setMessage("updated image successfully");
//                    alertDialog.show();
                    FirebaseTest.updatePhoto(str_email,Firebase_success);
                }
                else{
                    alertDialog.setMessage("Error. Could not upload change profile picture");
                    alertDialog.show();
                }

            }

        };
        upload_success.observe(this, obs2);
    }

    public void MutableFirebase(){
        final Observer<Boolean> obs3 = new Observer<Boolean>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final Boolean b){
                if(b){
                    if(profilepic==null){
                        Log.d("Check", "profile pic is null");
                    }
                    if(img==null){
                        Log.d("Check", "img is null");
                    }
                    img= (ImageView)(getView().findViewById(R.id.imageView2));
                    Glide.with(getActivity()).load(profilepic.toString()).diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true).into(img);
                    pb.setVisibility(View.GONE);
                    alertDialog.setMessage("updated image successfully");
                    alertDialog.show();
                }
                else{
                    alertDialog.setMessage("Error. Could not update profile Picture");
                    alertDialog.show();
                }

            }

        };
        Firebase_success.observe(this, obs3);

    }

}