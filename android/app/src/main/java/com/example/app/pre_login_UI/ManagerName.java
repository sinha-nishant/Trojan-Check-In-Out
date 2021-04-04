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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.app.services.UrlUploadImage;
import com.example.app.account_UI.ManagerProfile;
import com.example.app.R;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.log_create.CreateAccount;
import com.example.app.log_create.LogInOut;
import com.example.app.log_create.uploadPhoto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.annotation.Nullable;

public class ManagerName extends AppCompatActivity {

    String fName, lName;
    EditText firstNameInput;
    EditText lastNameInput;
    Button submitButton;
    Button profileButton;
    int SELECT_PICTURE = 200;
    ImageView img;
    Uri selectedImage;
    Integer ImageSet=0;//0 not set, 1 set by local imaage, 2 set by url
    String email,password;
    ProgressBar pb;
    AlertDialog alertDialog;
    private final MutableLiveData<Boolean> create_success = new MutableLiveData<>();
    private final MutableLiveData<Boolean> email_success = new MutableLiveData<>();
    private final MutableLiveData<Boolean> restore_success = new MutableLiveData<>();
    private final MutableLiveData<Boolean> upload_success = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_name);

        profileButton= (Button) findViewById(R.id.choosePic);
        img= (ImageView) findViewById(R.id.imageView3);
        firstNameInput = (EditText) findViewById(R.id.managerFirstName);
        lastNameInput = (EditText) findViewById(R.id.managerLastName);
        pb= (ProgressBar)findViewById(R.id.progressBar5);
        pb.setVisibility(View.GONE);

        //Get the bundle
        Bundle bundle = getIntent().getExtras();

        //Extract the data…
        email = bundle.getString("email");
        password = bundle.getString("password");

        submitButton = (Button) findViewById(R.id.nameButtonM);
        AmplifyInit();
        DialogInit();
        createMLDInit();
        emailMLDInit();
        restoreMLDInit();
        uploadMLDInit();
        if(bundle.containsKey("url")){
            Log.d("uriReturned", bundle.getString("url"));
            selectedImage=Uri.parse(bundle.getString("url"));
            Glide.with(this)
                    .load(selectedImage.toString()).error(Glide.with(this).load(R.drawable.profile_blank)).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(img);
            ImageSet = 2;
        }
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();

            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fName = firstNameInput.getText().toString();
                lName = lastNameInput.getText().toString();

                if(fName.length() == 0 && lName.length() == 0){
                    showToast("First and last name are blank");
                    return;
                }
                else if(fName.length() == 0 ) {
                    showToast("First name is blank");
                    return;
                }
                else if(lName.length() == 0 ) {
                    showToast("Last name is blank");
                    return;
                }
                pb.setVisibility(View.VISIBLE);
                submit();

            }
        });
    }

    private void showToast(String text)
    {
        Toast.makeText(ManagerName.this, text, Toast.LENGTH_SHORT).show();
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
                    img.setImageURI(selectedImage);
                    ImageSet = 1;


                }

            }
        }
    }

    public void submit(){
        profileButton.setEnabled(false);
        submitButton.setEnabled(false);
        FbQuery.checkEmailExists(email,email_success);

    }

   private void createMLDInit(){
       final Observer<Boolean> create_obs = new Observer<Boolean>(){
           @Override
           public void onChanged(@Nullable final Boolean isSuccess){
               if(isSuccess){
                   //stop progress bar
                   pb.setVisibility(View.GONE);
                   //Generate popupmessage
                   Log.d("Create", "success");


                   LogInOut.SaveData(ManagerName.this,email,0L);
                   profileButton.setEnabled(true);
                   submitButton.setEnabled(true);
                   alertDialog.setMessage("Succeeded in creating your account");
                   alertDialog.show();
               }
               else{
                   pb.setVisibility(View.GONE);
                   //switch page
                   profileButton.setEnabled(true);
                   submitButton.setEnabled(true);
                   alertDialog.setMessage("Could not successfully create your account");
                   alertDialog.show();
               }

           }

       };
       create_success.observe(this, create_obs);
   }

    private void uploadMLDInit(){
        final Observer<Boolean> upload_obs = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean isSuccess){
                if(isSuccess){

//                    FbQuery.checkUSCidExists(Long.valueOf(id),id_success);
                    CreateAccount.CreateManager(fName, lName, email,password,create_success,true);

                }
                else{
                    pb.setVisibility(View.GONE);
                    //switch pagebUploadImage.setEnabled(false);
                    profileButton.setEnabled(true);
                    submitButton.setEnabled(true);
                    alertDialog.setMessage("Could not upload image");
                    alertDialog.show();
                }

            }

        };
        upload_success.observe(this, upload_obs);
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
                            openManagerSignUp();
                            return;
                        }
                        if(upload_success.getValue()!=null && !upload_success.getValue()){
                            openManagerSignUp();
                            return;
                        }
                        if(create_success==null||create_success.getValue()==null){
                            openManagerSignUp();
                            return;
                        }
                        if(create_success.getValue()){
                            openProfile();
                        }
                        else{
                            openManagerSignUp();
                        }

                    }
                });
        alertDialog = builder.create();
    }


    private void openProfile() {
        //What unique identifier will be used to draw up profile page? Email?
        Intent i = new Intent(this, ManagerProfile.class);
        startActivity(i);
    }

    private void openManagerSignUp() {
        //What unique identifier will be used to draw up profile page? Email?
        Intent i = new Intent(this, ManagerSignUpStart.class);
        startActivity(i);
    }

    private void emailMLDInit(){
        final Observer<Boolean> email_obs = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean exists){
                if(!exists){
                    FbQuery.checkManagerRestore(email,restore_success);

                }
                else{
                    pb.setVisibility(View.GONE);
                    profileButton.setEnabled(true);
                    submitButton.setEnabled(true);
                    //switch page
                    alertDialog.setMessage("This email is already in use");
                    alertDialog.show();
                }

            }

        };
        email_success.observe(this, email_obs);
    }

    private void restoreMLDInit(){
        final Observer<Boolean> restore_obs = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean exists){
                if(!exists){


                    if(ImageSet==0){

                        CreateAccount.CreateManager(fName,lName,email,password,create_success,false);
                        return;
                    }

                    if(ImageSet==1){
                        String uri =selectedImage.toString();
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
                        CreateAccount.CreateManager(fName, lName, email,password,exampleInputStream,create_success);
                    }
                    if(ImageSet==2){
                        uploadTask task= new uploadTask();
                        task.execute();
                    }



                }
                else{
                    pb.setVisibility(View.GONE);
                    profileButton.setEnabled(true);
                    submitButton.setEnabled(true);
                    //switch page
                    alertDialog.setMessage("This account existed before. Please restore");
                    alertDialog.show();
                }

            }

        };
        restore_success.observe(this, restore_obs);
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

    public void url(View v){
        Intent i = new Intent(this, UrlUploadImage.class);
        Bundle bundle=getIntent().getExtras();
        i.putExtras(bundle);
        startActivity(i);

    }

}