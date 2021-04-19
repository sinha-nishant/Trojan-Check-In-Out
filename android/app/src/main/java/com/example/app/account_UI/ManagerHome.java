package com.example.app.account_UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.example.app.building.BuildingsOccupancyList;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.firebaseDB.FbUpdate;
import com.example.app.log_create.LogInOut;
import com.example.app.log_create.uploadPhoto;
import com.example.app.pre_login_UI.StartPage;
import com.example.app.services.UrlUploadImage;
import com.example.app.users.Account;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ManagerHome extends AppCompatActivity {
    ImageView imgView;
    TextView nameView;
    TextView emailView;
    MutableLiveData<Account> account= new MutableLiveData<>();
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
    AlertDialog picDialog;
    Button deleteBtn, signOutBtn, csvBtn, viewBuildingBtn, picBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_home);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        findViewById(R.id.manager_building).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ManagerHome.this, BuildingsOccupancyList.class);
                startActivity(intent1);
            }
        });

        findViewById(R.id.manager_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(ManagerHome.this, ManagerSearch.class);
                startActivity(intent2);
            }
        });

        SharedPreferences sp=  activity.getSharedPreferences("sharedPrefs",MODE_PRIVATE);
        email= sp.getString("email","");
        imgView= (ImageView)findViewById(R.id.imageView);
        nameView= (TextView)findViewById(R.id.textView18);
        emailView= (TextView)findViewById(R.id.textView19);
        pb= (ProgressBar)findViewById(R.id.progressBar3);
        pb.setVisibility(View.GONE);
        deleteBtn= findViewById(R.id.button15);
        signOutBtn= findViewById(R.id.button16);
        csvBtn= findViewById(R.id.csvBtn);
        viewBuildingBtn= findViewById(R.id.button17);
        picBtn=findViewById(R.id.ManagerProfilePic);

        MutableAccount();
        AmplifyInit();
        DialogInit();
        DeleteDialog();
        DialogPicInit();
        MutableDelete();
        MutableUpdatePic();
        MutableFirebase();
        FbQuery.getManager(email, account);


    }


    public void upload(){
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

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                profilePic = data.getData();

                if (null != profilePic) {
                    // update the preview image in the layout

                    String uri =profilePic.toString();
                    InputStream exampleInputStream=null;
                    try {
                        exampleInputStream = this.getContentResolver().openInputStream(Uri.parse(uri));

                    } catch (FileNotFoundException e) {
                        Log.i("upload", "error in uri parsing");
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

    public void DialogPicInit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Status of Action");
        builder.setCancelable(false);
        builder.setPositiveButton("File",
                new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {
                        upload();

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
                        if(account.getValue()==null){
                            Log.d("ManageHome","account mld is null in delete dialog");
                            return;
                        }
                        account.getValue().delete(delete_success);

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


    public void MutableAccount(){

        final Observer<Account> account_obs = new Observer<Account>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final Account acc){
                if(acc==null){
                    Log.d("ManagerHome","account mld is null");
                }
                else{
                    name= acc.getFirstName()+ " "+ acc.getLastName();
                    email=acc.getEmail();
                    String uri=acc.getProfilePicture();
                    if(uri==null){
                        profilePic=null;
                    }
                    else{
                        profilePic=Uri.parse(acc.getProfilePicture());
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
        account.observe(this, account_obs);
    }

    public void MutableUpdatePic(){
        final Observer<Boolean> update_obs = new Observer<Boolean>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final Boolean isSuccess){
                if(isSuccess==null){
                    Log.d("ManagerHome","update pic mld is null");
                    return;
                }
                if(isSuccess){
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
        upload_success.observe(this, update_obs);
    }

    public void MutableFirebase(){
        final Observer<Boolean> firebase_obs = new Observer<Boolean>(){
            @Override
            public void onChanged(@javax.annotation.Nullable final Boolean isSuccess){
                if(isSuccess==null){
                    Log.d("ManagerHome","firebase mld is null");
                    return;
                }
                if(isSuccess){
                    Glide.with(activity).load(profilePic.toString()).error(Glide.with(imgView).load(R.drawable.profile_blank)).diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true).into(imgView);
                    pb.setVisibility(View.GONE);
                    enableBtns();
                    alertDialog.setMessage("updated image successfully");
                }
                else{
                    pb.setVisibility(View.GONE);
                    enableBtns();
                    alertDialog.setMessage("Error. Could not update profile Picture");
                }
                alertDialog.show();

            }

        };
        Firebase_success.observe(this, firebase_obs);

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
        final Observer<Integer> delete_obs = new Observer<Integer>(){
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
        delete_success.observe(this, delete_obs);
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
        picBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        signOutBtn.setEnabled(false);
        csvBtn.setEnabled(false);
        viewBuildingBtn.setEnabled(false);
    }
    public void enableBtns(){
        picBtn.setEnabled(true);
        deleteBtn.setEnabled(true);
        signOutBtn.setEnabled(true);
        csvBtn.setEnabled(true);
        viewBuildingBtn.setEnabled(true);
    }

    public void url(){
        Intent i = new Intent(this, UrlUploadImage.class);
        Bundle bundle=new Bundle();
        bundle.putString("email",email);
        bundle.putString("created","yes");
        i.putExtras(bundle);
        startActivity(i);
    }

    public  void choosePic(View v){
        picDialog.setMessage("How do you want to upload your picture");
        picDialog.show();

    }




}