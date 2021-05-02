package com.example.app.pre_login_UI;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.app.R;
import com.example.app.StudentProfileNavDrawerJohn;
import com.example.app.account_UI.StudentProfile;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.log_create.CreateAccount;
import com.example.app.log_create.LogInOut;
import com.example.app.log_create.uploadPhoto;
import com.example.app.services.UrlUploadImage;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import javax.annotation.Nullable;

public class StudentUploadPhoto extends AppCompatActivity {

    private String email, password, fName, lName, id, major;
    private ImageView uploadImage;
    protected Button bUploadImage, submitBtn;
    private ProgressBar studentProgress;
    private AlertDialog alertDialog;
    private AlertDialog picDialog;
    private Uri selectedImage;
    private Integer ImageSet=0; //0 not set, 1 set by local imaage, 2 set by url
    private final MutableLiveData<Boolean> create_success = new MutableLiveData<>();
    private final MutableLiveData<Boolean> email_success = new MutableLiveData<>();
    private final MutableLiveData<Boolean> id_success = new MutableLiveData<>();
    private final MutableLiveData<Boolean> restore_success = new MutableLiveData<>();
    private final MutableLiveData<Boolean> upload_success = new MutableLiveData<>();
    private final int SELECT_PICTURE = 200;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_upload_photo);

        AmplifyInit();

        uploadImage = (ImageView)findViewById(R.id.imageToUpload);
        studentProgress = (ProgressBar)findViewById(R.id.progressBarStudentPhoto);
        studentProgress.setVisibility(View.GONE);

        DialogInit();
        DialogPicInit();
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
            selectedImage=Uri.parse(bundle.getString("url"));
            Glide.with(this)
                    .load(selectedImage.toString()).error(Glide.with(this).load(R.drawable.profile_blank)).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(uploadImage);
            ImageSet = 2;
        }


        bUploadImage = (Button)findViewById(R.id.createPic);
        submitBtn= findViewById(R.id.submit);

        // handle the Choose Image button to trigger
        // the image chooser function
        bUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picDialog.setMessage("How do you want to upload your picture");
                picDialog.show();

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
                if (null != selectedImage) {
                    // update the preview image in the layout
                    uploadImage.setImageURI(selectedImage);
                    ImageSet = 1;


                }

            }
        }
    }



    private void openProfile() {
        //Intent i = new Intent(this, StudentProfileNavDrawerJohn.class);
        Intent i = new Intent(this, StudentProfile.class);
        startActivity(i);
    }

    private void openSignUp() {
        Intent i = new Intent(this, StudentSignUpStart.class);
        startActivity(i);
    }

    private void openRestore() {
        Intent i = new Intent(this, RestorePage.class);
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

        builder.setTitle("Student Account Creation");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirm",
                new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {

                        studentProgress.setVisibility(View.GONE);

                        if(email_success.getValue()==null || email_success.getValue()){
                            openSignUp();
                            return;
                        }
                        if(id_success.getValue()==null||id_success.getValue()){
                            openSignUp();
                            return;
                        }
                        if(restore_success.getValue()==null){
                            openSignUp();
                            return;
                        }
                        if(restore_success.getValue()){
                            //Todo redirect to restore page
                            openRestore();
                            return;
                        }
                        if(upload_success.getValue()!=null && !upload_success.getValue()){
                            openSignUp();
                            return;
                        }

                        if(create_success.getValue()!=null &&!create_success.getValue()){
                            openSignUp();
                            return;
                        }
                        if(create_success.getValue()!=null){
                            openProfile();
                        }


                    }
                });
        alertDialog = builder.create();
    }

    public void DialogPicInit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Image Upload Format");
        builder.setCancelable(false);
        builder.setPositiveButton("File",
                new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {
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

    private void createMLDInit(){
        final Observer<Boolean> create_obs = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean isSuccess){
                if(isSuccess==null){
                    Log.d("StudentUploadPhoto","create mld is null");
                    studentProgress.setVisibility(View.GONE);
                    alertDialog.setMessage("Error in database when processing the new account");
                    alertDialog.show();
                    return;
                }
                if(isSuccess){
                    //stop progress bar
                    studentProgress.setVisibility(View.GONE);
                    //Generate popupmessage

                    LogInOut.SaveData(StudentUploadPhoto.this,email,Long.valueOf(id));
                    bUploadImage.setEnabled(true);
                    submitBtn.setEnabled(true);
                    alertDialog.setMessage("Succeeded in creating your account");
                    alertDialog.show();
                }
                else{
                    studentProgress.setVisibility(View.GONE);
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
                if(exists==null){
                    Log.d("StudentUploadPhoto","email mld is null");
                    studentProgress.setVisibility(View.GONE);
                    alertDialog.setMessage("Error processing your email");
                    alertDialog.show();
                    return;
                }
                if(!exists){
                    FbQuery.checkUSCidExists(Long.valueOf(id),id_success);
                }
                else{
                    studentProgress.setVisibility(View.GONE);
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
                if(exists==null){
                    Log.d("StudentUploadPhoto","id mld is null");
                    studentProgress.setVisibility(View.GONE);
                    alertDialog.setMessage("Error processing your ID");
                    alertDialog.show();
                    return;
                }
                if(!exists){
                    FbQuery.checkStudentRestore(Long.valueOf(id),email,restore_success);

                }
                else{
                    studentProgress.setVisibility(View.GONE);
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
                if(exists==null){
                    Log.d("StudentUploadPhoto","id mld is null");
                    studentProgress.setVisibility(View.GONE);
                    alertDialog.setMessage("Error processing account restoration");
                    alertDialog.show();
                    return;
                }
                if(!exists){

                    if(ImageSet==0){
            CreateAccount.CreateStudent(fName,lName,email,password,Long.valueOf(id),major,create_success,false);
                        return;
                    }
                    if(ImageSet==1){
                    String uri =selectedImage.toString();
                    InputStream exampleInputStream=null;


                    try {
                        exampleInputStream = getContentResolver().openInputStream(Uri.parse(uri));

                    } catch (FileNotFoundException e) {
                        Log.i("upload", "error in uri parsing");
                        studentProgress.setVisibility(View.GONE);
                        alertDialog.setMessage("Could not find the image");
                        alertDialog.show();
                        return;
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
                if(isSuccess==null){
                    Log.d("StudentUploadPhoto","upload mld is null");
                    studentProgress.setVisibility(View.GONE);
                    alertDialog.setMessage("Error processing your account");
                    alertDialog.show();
                    return;
                }
                if(isSuccess){
                    CreateAccount.CreateStudent(fName, lName, email,password,Long.valueOf(id),major,create_success,true);

                }
                else{
                    studentProgress.setVisibility(View.GONE);
                    bUploadImage.setEnabled(true);
                    submitBtn.setEnabled(true);
                    alertDialog.setMessage("Could not upload image");
                    alertDialog.show();
                }

            }

        };
        upload_success.observe(this, upload_obs);
    }

    public void url(){
        Intent i = new Intent(this, UrlUploadImage.class);
        Bundle bundle=getIntent().getExtras();
        i.putExtras(bundle);
        startActivity(i);


    }
    class uploadTask extends AsyncTask<Void, Void, Void> {
        private Boolean done=true;

        @Override
        protected Void doInBackground(Void... arg0) {
            doInBackground();
            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            if(!done){
                alertDialog.setMessage("Could not configure image using the url");
                alertDialog.show();
            }
        }


        protected void doInBackground() {

            try{
                URL link= new URL(selectedImage.toString());
                InputStream stream = link.openStream();
                uploadPhoto.upload(stream, email, upload_success);
            }catch(Exception e){
                done =false;
                e.printStackTrace();
            }




        }
    }


}

