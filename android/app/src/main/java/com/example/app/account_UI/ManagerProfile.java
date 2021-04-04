package com.example.app.account_UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.app.R;
import com.example.app.services.UrlUploadImage;
import com.example.app.building.BuildingsOccupancyList;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.firebaseDB.FbUpdate;
import com.example.app.log_create.LogInOut;
import com.example.app.log_create.uploadPhoto;
import com.example.app.pre_login_UI.StartPage;
import com.example.app.users.Account;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ManagerProfile extends AppCompatActivity {
    ImageView imgView;
    TextView nameView;
    TextView emailView;
    MutableLiveData<Account> student= new MutableLiveData<>();
    MutableLiveData<Integer> delete_success= new MutableLiveData<>();
    MutableLiveData<Boolean> upload_success= new MutableLiveData<>();
    MutableLiveData<Boolean> Firebase_success= new MutableLiveData<>();
    String name;
    String email;
    Uri profilePic;
    Activity activity= this;
    AlertDialog alertDialog;
    int SELECT_PICTURE = 200;
    ProgressBar pb;
    AlertDialog deleteDialog;
    Button photoBtn, deleteBtn, signOutBtn, csvBtn, viewBuildingBtn, urlBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_profile_v2);
        Log.d("ManagerProfile","in manager Profile");
        SharedPreferences sp=  activity.getSharedPreferences("sharedPrefs",activity.MODE_PRIVATE);

        email= sp.getString("email","");
        Log.d("ManagerProfile", email+"ending");
        imgView= (ImageView)findViewById(R.id.imageView);
        nameView= (TextView)findViewById(R.id.textView18);
        emailView= (TextView)findViewById(R.id.textView19);
        pb= (ProgressBar)findViewById(R.id.progressBar3);
        pb.setVisibility(View.GONE);

        photoBtn= findViewById(R.id.button11);
        deleteBtn= findViewById(R.id.button15);
        signOutBtn= findViewById(R.id.button16);
        csvBtn= findViewById(R.id.csvBtn);
        viewBuildingBtn= findViewById(R.id.button17);
        urlBtn=findViewById(R.id.urlManagerProfile);

//        SharedPreferences sp=  activity.getSharedPreferences("sharedPrefs",activity.MODE_PRIVATE);
//
//        email= sp.getString("email","");
//        Log.d("ManagerProfile", email);
        MutableStudent();
        AmplifyInit();
        DialogInit();
        DeleteDialog();
        MutableDelete();
        MutableBoolean();
        MutableFirebase();
//        SharedPreferences sp=  activity.getSharedPreferences("sharedPrefs",activity.MODE_PRIVATE);
//
//        email= sp.getString("email","");
//        Log.d("ManagerProfile", email);
        FbQuery.getManager(email, student);


    }


    public void upload(View v){
        imageChooser();
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

        if (resultCode == this.RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                profilePic = data.getData();

                Log.i("Image",profilePic.toString());
                if (null != profilePic) {
                    // update the preview image in the layout

                    String uri =profilePic.toString();
                    InputStream exampleInputStream=null;



                    try {
                        exampleInputStream = this.getContentResolver().openInputStream(Uri.parse(uri));
                        if(exampleInputStream==null){
                            Log.i("upload", "stream is null");
                        }
                        else{
                            Log.i("upload", "stream is valid");
                        }


                    } catch (FileNotFoundException e) {
                        Log.i("upload", "error in uri parsing");
                    }
                    if(email!=null){
                        Log.i("photo",email);
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
                    disableBtns();
                    uploadPhoto.update(exampleInputStream,email,upload_success);


                }

            }
        }
    }

    public void AmplifyInit(){
        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(this);
        } catch (AmplifyException e) {
            Log.i("MyAmplifyApp", "could not add plugins ");
        }
    }

    public void DialogInit(){
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

                        Integer val=  delete_success.getValue();
                        if(val==null){
                            return;
                        }
                        if(val<2){
                            return;
                        }
                        openHomePage();

                    }
                });
        alertDialog = builder.create();
    }

    public void DeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirmation of Delete Request");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes",
                new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {
                        student.getValue().delete(delete_success);

                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {


                    }
                });
        deleteDialog = builder.create();
    }


    public void MutableStudent(){

        final Observer<Account> obs = new Observer<Account>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final Account a){
                Log.d("ManagerProfile","mld changed");
                if(a==null){
                    Log.d("ManagerProfile","is null");
                    return;
                }
                else{
                    Log.d("profile","in profile onchaged");
                    Log.d("profile",a.toString());
                    name= a.getFirstName()+ " "+ a.getLastName();
                    email=a.getEmail();
                    String uri=a.getProfilePicture();
                    if(uri==null){
                        profilePic=null;
                    }
                    else{
                        profilePic=Uri.parse(a.getProfilePicture());
                    }
                    nameView.setText(name);
                    emailView.setText(email);
                    if(profilePic!=null){
                        Glide.with(activity).load(profilePic.toString()).error(Glide.with(imgView).load(R.drawable.profile_blank)).diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true).into(imgView);
                    }
                    pb.setVisibility(View.GONE);

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
                    if(profilePic==null){
                        Log.d("Check", "profile pic is null");
                    }
                    if(imgView==null){
                        Log.d("Check", "img is null");
                    }
                    FbUpdate.updatePhoto(email,Firebase_success);
                }
                else{
                    enableBtns();
                    pb.setVisibility(View.GONE);
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
                    if(profilePic==null){
                        Log.d("Check", "profile pic is null");
                    }
                    if(imgView==null){
                        Log.d("Check", "img is null");
                    }
                    Glide.with(activity).load(profilePic.toString()).error(Glide.with(imgView).load(R.drawable.profile_blank)).diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true).into(imgView);
                    pb.setVisibility(View.GONE);
                    enableBtns();
                    alertDialog.setMessage("updated image successfully");
                    alertDialog.show();
                }
                else{
                    pb.setVisibility(View.GONE);
                    enableBtns();
                    alertDialog.setMessage("Error. Could not update profile Picture");
                    alertDialog.show();
                }

            }

        };
        Firebase_success.observe(this, obs3);

    }

    public void Delete(View v){
        disableBtns();
        deleteDialog.show();
    }

    public void signOut(View v){
        disableBtns();
        pb.setVisibility(View.VISIBLE);
        LogInOut.LogOut(this);
        pb.setVisibility(View.GONE);
        openHomePage();
    }
    public void openHomePage() {
        Intent i = new Intent(this, StartPage.class);
        startActivity(i);
    }

    public void MutableDelete(){
        final Observer<Integer> obs3 = new Observer<Integer>(){
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
                    LogInOut.LogOut(activity);

                }

            }

        };
        delete_success.observe(this, obs3);
    }


    public void openBuildings(View v){
        Intent i = new Intent(this, BuildingsOccupancyList.class);
        startActivity(i);
    }
    public void openCSVView(View v){
        Intent i = new Intent(this, ManagerCSV.class);
        startActivity(i);

    }

    public void disableBtns(){
        photoBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        signOutBtn.setEnabled(false);
        csvBtn.setEnabled(false);
        viewBuildingBtn.setEnabled(false);
    }
    public void enableBtns(){
        photoBtn.setEnabled(true);
        deleteBtn.setEnabled(true);
        signOutBtn.setEnabled(true);
        csvBtn.setEnabled(true);
        viewBuildingBtn.setEnabled(true);
    }

    public void url(View v){
        Intent i = new Intent(this, UrlUploadImage.class);
        Bundle bundle=new Bundle();
        bundle.putString("email",email);
        bundle.putString("created","yes");
        i.putExtras(bundle);
        startActivity(i);
    }


}