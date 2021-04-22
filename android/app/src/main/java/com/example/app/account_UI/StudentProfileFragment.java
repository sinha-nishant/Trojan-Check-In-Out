package com.example.app.account_UI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.app.R;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.firebaseDB.FbUpdate;
import com.example.app.log_create.uploadPhoto;
import com.example.app.services.UrlUploadImage;
import com.example.app.users.StudentAccount;
import com.example.app.users.StudentActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

public class StudentProfileFragment extends Fragment implements View.OnClickListener{

    private String str_name, str_email, str_id, str_major;
    protected TextView name, building_name;
    private Uri profilepic;
    private AlertDialog alertDialog,picDialog;
    private ProgressBar pb;
    protected Button UploadBtn;
    private ImageView img;
    private final MutableLiveData<StudentAccount> student = new MutableLiveData<>();
    private final MutableLiveData<Boolean> upload_success = new MutableLiveData<>();
    private final MutableLiveData<Boolean> firebase_success = new MutableLiveData<>();
    private final int SELECT_PICTURE = 200;

    public StudentProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TO BACKEND: use str_email to make your database call, and fill in these fields:
        AmplifyInit();
        pb = getActivity().findViewById(R.id.progressBar6);
        building_name = getActivity().findViewById(R.id.textViewCurrBuilding);
        MutableStudent();
        DialogInit();
        DialogPicInit();
        MutableBoolean();
        MutableFirebase();
        SharedPreferences sp=  getContext().getSharedPreferences("sharedPrefs",getActivity().MODE_PRIVATE);
        Long id = sp.getLong("uscid",0L);
        FbQuery.getStudent(id,student);
        this.getLifecycle().addObserver(new ForegroundBackgroundListener());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_student_profile, container, false);
        UploadBtn = view.findViewById(R.id.studentUpdatePic);
        UploadBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name = getView().findViewById(R.id.tvProfileName);
        name.setText(String.format("Welcome, %s", str_name));

        TextView id = getView().findViewById(R.id.textView13);
        id.setText(String.format("USC ID: %s", str_id));

        TextView email = getView().findViewById(R.id.textView14);
        email.setText(String.format("Email: %s", str_email));

        TextView major = getView().findViewById(R.id.textView15);
        major.setText(String.format("Major: %s", str_major));

        img = getView().findViewById(R.id.imageView2);
        if(profilepic != null){
            Glide.with(getActivity()).load(profilepic.toString()).error(Glide.with(img).load(R.drawable.profile_blank)).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(img);
        }
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

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                profilepic = data.getData();

                if (null != profilepic) {

                    // update the preview image in the layout
                    String uri = profilepic.toString();
                    InputStream exampleInputStream;

                    try {
                        exampleInputStream = getActivity().getContentResolver().openInputStream(Uri.parse(uri));

                    } catch (FileNotFoundException e) {
                        Log.i("upload", "error in uri parsing");
                        UploadBtn.setEnabled(true);
                        alertDialog.setMessage("Error. Could not find profile picture");
                        alertDialog.show();
                        return;
                    }
                    pb.setVisibility(View.VISIBLE);
                    uploadPhoto.update(exampleInputStream,str_email,upload_success);
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
            Log.i("MyAmplifyApp", "could not add plugins");
        }
    }

    public void DialogInit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Image Upload");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirm",
                new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {}
                });
        alertDialog = builder.create();
    }
    public void DialogPicInit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Image Upload Format");
        builder.setCancelable(false);
        builder.setPositiveButton("File",
                new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {
                        UploadBtn.setEnabled(false);
                        imageChooser();
                    }
                });
        builder.setNegativeButton("URL",
                new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {
                        UploadBtn.setEnabled(false);
                        url();
                    }
                });
        builder.setNeutralButton("Cancel",
                new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {


                    }
                });
        picDialog = builder.create();
    }

    public void MutableStudent(){
        final Observer<StudentAccount> obs = new Observer<StudentAccount>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final StudentAccount sa){
                if(sa == null) {
                    Log.d("StudentProfileFragment","student mld is null");
                    UploadBtn.setEnabled(true);
                    alertDialog.setMessage("Error. Could not retrieve account information");
                    alertDialog.show();
                }
                else {
                    if(sa.getActivity().isEmpty()){
                        building_name.setText("You are not checked into a building");
                    }
                    else {
                        StudentActivity act=sa.getActivity().get(sa.getActivity().size() - 1);
                        if(act.getCheckOutTime()==null){
                            building_name.setText(String.format("You are now checked into a building %s", act.getBuildingName()));
                        }
                        else {
                            building_name.setText("You are not checked into a building");
                        }
                    }

                    str_name = sa.getFirstName() + " " + sa.getLastName();
                    str_email =sa.getEmail();
                    str_id = sa.getUscID().toString();
                    str_major = sa.getMajor();
                    TextView id = getView().findViewById(R.id.textView13);
                    id.setText(String.format("USC ID: %s", str_id));

                    TextView email = getView().findViewById(R.id.textView14);
                    email.setText(String.format("Email: %s", str_email));

                    TextView profileName = getView().findViewById(R.id.tvProfileName);
                    profileName.setText(String.format("Welcome, %s", str_name));
                    TextView major = getView().findViewById(R.id.textView15);
                    major.setText(String.format("Major: %s", str_major));
                    if(sa.getProfilePicture() != null){
                        profilepic = Uri.parse(sa.getProfilePicture());
                        img = getView().findViewById(R.id.imageView2);
                        Glide.with(getActivity()).load(profilepic.toString()).error(Glide.with(img).load(R.drawable.profile_blank)).diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true).into(img);
                    }
                    pb.setVisibility(View.GONE);
                }
            }
        };
        student.observe(this, obs);
    }

    public void MutableBoolean(){
        final Observer<Boolean> uploadSuccessObserver = new Observer<Boolean>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final Boolean uploadSuccess){
                if(uploadSuccess==null) {
                    Log.d("StudentProfileFragment","upload mld is null");
                    UploadBtn.setEnabled(true);
                    alertDialog.setMessage("Error occurred while trying to upload image");
                    alertDialog.show();
                    return;
                }
                if(uploadSuccess){
                    FbUpdate.updatePhoto(str_email, firebase_success);
                }
                else{
                    UploadBtn.setEnabled(true);
                    alertDialog.setMessage("Error. Could not upload change profile picture");
                    alertDialog.show();
                }
            }
        };

        upload_success.observe(this, uploadSuccessObserver);
    }

    public void MutableFirebase(){
        final Observer<Boolean> firebaseSuccessObserver = new Observer<Boolean>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final Boolean firebaseSuccess){
                if(firebaseSuccess==null){
                    Log.d("StudentProfileFragment","firebase mld is null");
                    UploadBtn.setEnabled(true);
                    alertDialog.setMessage("Error occurred in database while processing your request");
                    alertDialog.show();
                    return;
                }
                if(firebaseSuccess){
                    img = getView().findViewById(R.id.imageView2);
                    Glide.with(getActivity()).load(profilepic.toString()).error(Glide.with(img).load(R.drawable.profile_blank)).diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true).into(img);
                    pb.setVisibility(View.GONE);
                    UploadBtn.setEnabled(true);
                    alertDialog.setMessage("Updated image successfully");
                }
                else {
                    UploadBtn.setEnabled(true);
                    alertDialog.setMessage("Error. Could not update profile picture");
                }
                alertDialog.show();
            }
        };

        firebase_success.observe(this, firebaseSuccessObserver);
    }

    public void url(){
        Intent i = new Intent(getActivity(), UrlUploadImage.class);
        Bundle bundle=new Bundle();
        bundle.putString("email",str_email);
        bundle.putString("id",str_id);
        bundle.putString("created","yes");
        i.putExtras(bundle);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        picDialog.setMessage("How do you want to upload your picture");
        picDialog.show();
    }

    class ForegroundBackgroundListener implements LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        public void connectListener() {
            SharedPreferences sp=  getContext().getSharedPreferences("sharedPrefs",getActivity().MODE_PRIVATE);
            Long id = sp.getLong("uscid",0L);
            FbQuery.getStudent(id,student);
        }

    }
}