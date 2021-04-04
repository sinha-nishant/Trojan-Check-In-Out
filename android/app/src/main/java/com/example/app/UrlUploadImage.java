package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.app.account_UI.ManagerProfile;
import com.example.app.account_UI.StudentProfile;
import com.example.app.log_create.uploadPhoto;
import com.example.app.pre_login_UI.ManagerName;
import com.example.app.pre_login_UI.StudentUploadPhoto;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class UrlUploadImage extends AppCompatActivity {
    AlertDialog alertDialog;
    TextView url;
    ImageView img;
    Boolean uploadable;
    MutableLiveData<Boolean> upload_success= new MutableLiveData<>();
    String str_email;
    Boolean isStudent;
    Boolean isCreated;
    String finalUrl;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url_upload_image);
        DialogInit();
        MutableUpload();
        AmplifyInit();
        url= findViewById(R.id.url);
        img= findViewById(R.id.urlImage);
        uploadable=false;
        finalUrl="";
        pb= findViewById(R.id.urlProgress);
        pb.setVisibility(View.GONE);
        Bundle bundle= getIntent().getExtras();
        if(bundle==null){
            bundle= new Bundle();
        }

        str_email=bundle.getString("email");
        if(!bundle.containsKey("id")){
            isStudent=false;
        }
        else{
            isStudent=true;
        }
        if(!bundle.containsKey("created")){
            isCreated=false;
        }
        else{
            isCreated=true;
        }
        if(!isCreated){
            Button uploadBtn=  findViewById(R.id.update);
            uploadBtn.setEnabled(false);
            uploadBtn.setVisibility(View.GONE);
        }

    }

    public void DialogInit(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        builder.setTitle("Status of Action");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes",
                new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {


                    }
                });
        alertDialog = builder.create();
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

    public void MutableUpload(){
        final Observer<Boolean> upload_obs = new Observer<Boolean>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final Boolean isSuccess){
                if(isSuccess){
                    pb.setVisibility(View.GONE);
                    alertDialog.setMessage("Succeeded in uploading profile picture");
                    alertDialog.show();
                }
                else{
                    pb.setVisibility(View.GONE);
                    alertDialog.setMessage("Error. Could not upload change profile picture");
                    alertDialog.show();
                }

            }

        };
        upload_success.observe(this, upload_obs);
    }

    public void back(View v){
        if(isStudent && isCreated){
            openStudentProfile();
        }
        if(isStudent && !isCreated){
            openStudentSubmit();
        }
        if(!isStudent&& isCreated){
            openManagerProfile();
        }
        if(!isStudent &&!isCreated){
            openManagerSubmit();
        }
    }

    public void update(View v){
        if(!uploadable){
            alertDialog.setMessage("Can not upload.Please preview a valid image");
            alertDialog.show();
            return;
        }
        pb.setVisibility(View.VISIBLE);
        uploadTask task= new uploadTask();
        task.execute();





    }

    public void preview(View v){
        pb.setVisibility(View.VISIBLE);
        Uri profilepic= Uri.parse(url.getText().toString());
        Glide.with(this)
                .load(profilepic.toString()).error(Glide.with(this).load(R.drawable.profile_blank)).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(new RequestListener<Drawable>() {
                              @Override
                              public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                  pb.setVisibility(View.GONE);
                                  alertDialog.setMessage("Url is not valid");
                                  alertDialog.show();
                                  uploadable=false;
                                  Log.d("uri",uploadable.toString());
                                  return false;
                              }
                              @Override
                              public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  pb.setVisibility(View.GONE);
                                  uploadable=true;
                                  finalUrl=url.getText().toString();
                                  Log.d("uri",uploadable.toString());
                                  return false;
                              }
                          }
                )
                .into(img);



    }

    class uploadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            doInBackground();   //your methods
            return null;
        }

        protected void doInBackground() {
            try{
                URL link= new URL(url.getText().toString());
                InputStream stream = link.openStream();
                uploadPhoto.update(stream,str_email,upload_success);
                Log.d("async","in background task");
            }catch (IOException e){
                pb.setVisibility(View.GONE);
                alertDialog.setMessage("Failed to resolve url");
                alertDialog.show();
                e.printStackTrace();
            }


        }
    }

    public void openStudentProfile() {
        Intent i = new Intent(this, StudentProfile.class);
        startActivity(i);
    }
    public void openManagerProfile() {
        Intent i = new Intent(this, ManagerProfile.class);
        startActivity(i);
    }

    public void openStudentSubmit() {
        Intent i = new Intent(this, StudentUploadPhoto.class);
        Bundle bundle= getIntent().getExtras();
        bundle.putString("url", finalUrl);
        i.putExtras(bundle);
        startActivity(i);
    }

    public void openManagerSubmit() {
        Intent i = new Intent(this, ManagerName.class);
        Bundle bundle= getIntent().getExtras();
        bundle.putString("url", finalUrl);
        i.putExtras(bundle);
        startActivity(i);
    }
}