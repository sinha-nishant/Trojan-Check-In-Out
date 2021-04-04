package com.example.app.pre_login_UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.app.R;
import com.example.app.services.UrlUploadImage;
import com.example.app.account_UI.StudentProfile;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.log_create.CreateAccount;
import com.example.app.log_create.LogInOut;
import com.example.app.log_create.uploadPhoto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.annotation.Nullable;

public class StudentUploadPhoto extends AppCompatActivity {

    String email, password, fName, lName, id, major;

    ImageView uploadImage;
    Button bUploadImage, submitBtn;
    ProgressBar studentProgress;
    AlertDialog alertDialog;
    Uri selectedImage;
    Integer ImageSet=0; //0 not set, 1 set by local imaage, 2 set by url
    private final MutableLiveData<Boolean> create_success = new MutableLiveData<>();
    private final MutableLiveData<Boolean> email_success = new MutableLiveData<>();
    private final MutableLiveData<Boolean> id_success = new MutableLiveData<>();
    private final MutableLiveData<Boolean> restore_success = new MutableLiveData<>();
    private final MutableLiveData<Boolean> upload_success = new MutableLiveData<>();

    int SELECT_PICTURE = 200;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_upload_photo);

        AmplifyInit();

        uploadImage = (ImageView)findViewById(R.id.imageToUpload);
        studentProgress = (ProgressBar)findViewById(R.id.progressBarStudentPhoto);
        studentProgress.setVisibility(View.GONE);

        DialogInit();
        createMLDInit();
        emailMLDInit();
        idMLDInit();
        restoreMLDInit();
        uploadMLDInit();


        //To Backend: What data y'all get:
        Bundle bundle = getIntent().getExtras();

        email = bundle.getString("email");
        password = bundle.getString("password");
        fName = bundle.getString("fName");
        lName = bundle.getString("lName");
        id = bundle.getString("id");
        major = bundle.getString("major");

        if(bundle.containsKey("url")){
            Log.d("uriReturned", bundle.getString("url"));
            selectedImage=Uri.parse(bundle.getString("url"));
            Glide.with(this)
                    .load(selectedImage.toString()).error(Glide.with(this).load(R.drawable.profile_blank)).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(uploadImage);
            ImageSet = 2;
        }


        bUploadImage = (Button)findViewById(R.id.uploadImageButton);
        submitBtn= findViewById(R.id.submit);

        // handle the Choose Image button to trigger
        // the image chooser function
        bUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });
    }

    // this function is triggered when
    // the Select Image Button is clicked
    private void imageChooser() {

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
                selectedImage = data.getData();
                Log.i("Image",selectedImage.toString());
                if (null != selectedImage) {
                    // update the preview image in the layout
                    uploadImage.setImageURI(selectedImage);
                    ImageSet = 1;


                }

            }
        }
    }



    private void openProfile() {
        //What unique identifier will be used to draw up profile page? Email?
        Intent i = new Intent(this, StudentProfile.class);
        startActivity(i);
    }

    private void openSignUp() {
        //What unique identifier will be used to draw up profile page? Email?
        Intent i = new Intent(this, StudentSignUpStart.class);
        startActivity(i);
    }




    public void submit(View v){
        studentProgress.setVisibility(View.VISIBLE);
        bUploadImage.setEnabled(false);
        submitBtn.setEnabled(false);
        FbQuery.checkEmailExists(email,email_success);

    }


    private void AmplifyInit(){
        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());
        } catch (AmplifyException e) {
            Log.i("MyAmplifyApp", "could not add plugins ");
        }
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

                        if(email_success.getValue()){
                            openSignUp();
                            return;
                        }
                        if(id_success.getValue()){
                            openSignUp();
                            return;
                        }
                        if(restore_success.getValue()){
                            //Todo redirect to restore page
                            openSignUp();
                            return;
                        }
                        if(upload_success.getValue()!=null && !upload_success.getValue()){
                            openSignUp();
                            return;
                        }
                        if(!create_success.getValue()){
                            openSignUp();
                            return;
                        }
                        openProfile();
                    }
                });
        alertDialog = builder.create();
    }

    private void createMLDInit(){
        final Observer<Boolean> create_obs = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean isSuccess){
                if(isSuccess){
                    //stop progress bar
                    studentProgress.setVisibility(View.GONE);
                    //Generate popupmessage
                    Log.d("Create", "success");


                    LogInOut.SaveData(StudentUploadPhoto.this,email,Long.valueOf(id));
                    bUploadImage.setEnabled(true);
                    submitBtn.setEnabled(true);
                    alertDialog.setMessage("Succeeded in creating your account");
                    alertDialog.show();
                }
                else{
                    studentProgress.setVisibility(View.GONE);
                    //switch page
                    bUploadImage.setEnabled(true);
                    submitBtn.setEnabled(true);
                    alertDialog.setMessage("Could not successfully create your account");

                    alertDialog.show();
                }


            }

        };
        create_success.observe(this, create_obs);
    }

    private void emailMLDInit(){
        final Observer<Boolean> email_obs = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean exists){
                if(!exists){

                    FbQuery.checkUSCidExists(Long.valueOf(id),id_success);
                }
                else{
                    studentProgress.setVisibility(View.GONE);
                    //switch pagebUploadImage.setEnabled(false);
                    Log.d("email","in student account email exists "+email);
                    bUploadImage.setEnabled(true);
                    submitBtn.setEnabled(true);
                    alertDialog.setMessage("This Email is already in use");
                    alertDialog.show();
                }

            }

        };
        email_success.observe(this, email_obs);
    }

    private void idMLDInit(){
        final Observer<Boolean> id_obs = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean exists){
                if(!exists){
                    FbQuery.checkStudentRestore(Long.valueOf(id),email,restore_success);

                }
                else{
                    studentProgress.setVisibility(View.GONE);
                    //switch page
                    bUploadImage.setEnabled(true);
                    submitBtn.setEnabled(true);
                    alertDialog.setMessage("This ID is already in use");
                    alertDialog.show();
                }

            }

        };
        id_success.observe(this, id_obs);
    }

    private void restoreMLDInit(){
        final Observer<Boolean> restore_obs = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean exists){
                if(!exists){

                    if(ImageSet==0){
            CreateAccount.CreateStudent(fName,lName,email,password,Long.valueOf(id),major,create_success,false);
                        return;
                    }
                    if(ImageSet==1){
                    String uri =selectedImage.toString();
                    Log.d("uploadUri", uri);
                    InputStream exampleInputStream=null;


                    try {
                        exampleInputStream = getContentResolver().openInputStream(Uri.parse(uri));
                        if(exampleInputStream==null){
                            Log.i("upload", "stream is null");
                        }
                        else{
                            Log.i("upload", "stream is valid");
                        }


                    } catch (FileNotFoundException e) {
                        Log.i("upload", "error in uri parsing");
                    }
                    if(exampleInputStream==null){
                        Log.d("uploadUri","stream is null");
                    }
                    CreateAccount.CreateStudent(fName, lName, email,password,exampleInputStream,Long.valueOf(id),major,create_success);
                    }
                    if(ImageSet==2){
                        uploadTask task= new uploadTask();
                        task.execute();
                    }

                }
                else{
                    studentProgress.setVisibility(View.GONE);
                    bUploadImage.setEnabled(true);
                    submitBtn.setEnabled(true);
                    alertDialog.setMessage("This account existed before. Please restore");
                    alertDialog.show();
                }

            }

        };
        restore_success.observe(this, restore_obs);
    }

    private void uploadMLDInit(){
        final Observer<Boolean> upload_obs = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean isSuccess){
                if(isSuccess){

//                    FbQuery.checkUSCidExists(Long.valueOf(id),id_success);
                    CreateAccount.CreateStudent(fName, lName, email,password,Long.valueOf(id),major,create_success,true);

                }
                else{
                    studentProgress.setVisibility(View.GONE);
                    //switch pagebUploadImage.setEnabled(false);
                    bUploadImage.setEnabled(true);
                    submitBtn.setEnabled(true);
                    alertDialog.setMessage("Could not upload image");
                    alertDialog.show();
                }

            }

        };
        upload_success.observe(this, upload_obs);
    }

    public void url(View v){
        Intent i = new Intent(this, UrlUploadImage.class);
        Bundle bundle=getIntent().getExtras();
        i.putExtras(bundle);
        startActivity(i);


    }
    class uploadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            doInBackground();   //your methods
            return null;
        }

        protected void doInBackground() {
            try{
                URL link= new URL(selectedImage.toString());
                InputStream stream = link.openStream();
                uploadPhoto.upload(stream, email, upload_success);
                Log.d("async","in background task for student upload");
            }catch (IOException e){
                upload_success.setValue(false);
            }


        }
    }


}

