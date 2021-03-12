package com.example.app;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.Extension;

import javax.annotation.Nullable;

public class S3Test extends AppCompatActivity {
     


    // One Button
    Button BSelectImage;

    // One Preview Image
    ImageView IVPreviewImage;
    ProgressBar circle_thing;

    AlertDialog alertDialog;

    // constant to compare
    // the activity result code
    int SELECT_PICTURE = 200;

    private final MutableLiveData<Integer> create_success = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s3_test);
        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());
        } catch (AmplifyException e) {
            Log.i("MyAmplifyApp", "could not add plugins ");
        }

        circle_thing =(ProgressBar)findViewById(R.id.progressBar4);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Dialog box works");
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
                        finish();
                    }
                });
        alertDialog = builder.create();

        final Observer<Integer> obs = new Observer<Integer>(){
            @Override
            public void onChanged(@Nullable final Integer b){
                if(b==null){
                    alertDialog.setMessage("Failed to work");
                    alertDialog.show();
                    return;
                }
                if(b==0){
//                    //store email
//                    SaveData();
                    //stop progress bar
                    circle_thing.setVisibility(View.GONE);
                    //switch page
                    alertDialog.setMessage("Error. This Email already exists.");
                    alertDialog.show();


                }
                else if(b==1){
                    circle_thing.setVisibility(View.GONE);
                    //switch page
                    alertDialog.setMessage("Error. Failed to create your account successfully");
                    alertDialog.show();
                }
                else{
                    //stop progress bar
                    circle_thing.setVisibility(View.GONE);
                    //Generate popupmessage
                    Log.d("Create", "success");
                    alertDialog.setMessage("Succeeded in creatng your account");
                    alertDialog.show();
                }
            }

        };
        create_success.observe(this, obs);
        // register the UI widgets with their appropriate IDs
        BSelectImage = findViewById(R.id.BSelectImage);
        IVPreviewImage = findViewById(R.id.IVPreviewImage);


        // handle the Choose Image button to trigger
        // the image chooser function
        BSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });
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
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    IVPreviewImage.setImageURI(selectedImageUri);
                    TextView tv= (TextView)findViewById(R.id.uri);
                    tv.setText(selectedImageUri.toString());

                }

            }
        }
    }

    public void upload(View v){
        upload_with_image();
    }

    public void manger_nopic_delete(){
        Account a= new Account("Testing","delete","delete@gmail.com","$2a$12$V6BidSy9OtVWWCh6/cVj1O1hPZccvMUW5duW1pVUDZXPl4IB76sVe",true);
//        a.delete(create_success);

    }

    public void student_pic_delete(){
        Account a = new StudentAccount("tre","young","yeet@basketball.com","$2a$12$SKSpA5/C47szgrva82A6lO/44MV6pMLxE785T6VTkHPFODMAdrkGq","https://trojan-check-in-and-out183928-dev173416-dev.s3-us-west-2.amazonaws.com/public/yeet%40basketball.com.JPEG",false);
//        a.delete(create_success);
    }

    public void updatePW(){
//        CreateAccount ca= new CreateAccount("Update", "Password", "updatepw@gmail.com","pw",true,create_success);
//        UpdatePassword up= new UpdatePassword("updatepw@gmail.com","updatedpw",create_success);
    }

    public void upload_without_image(){

//                    CreateAccount ca= new CreateAccount("Lebron", "James", "LBJ@basketball.com","blocked",true,create_success);
//        CreateAccount ca= new CreateAccount("Anthony", "Davis", "AD@basketball.com","brow",false,Long.valueOf("1234567890"),"monster",create_success);
//        CreateAccount ca= new CreateAccount("Testing", "delete", "delete@gmail.com","del",true,create_success);
//        CreateAccount ca= new CreateAccount("Update", "Password", "updatepw@gmail.com","pw",true,create_success);
//        CreateAccount ca= new CreateAccount("Chris", "Paul", "CP3@gmail.com","cp3",true,create_success);
//          CreateAccount ca= new CreateAccount("Blake", "Griffin", "Blake@basketball.com","dunk",false,Long.valueOf("1234567890"),"monster",create_success);


    }

    public void upload_with_image(){
        Log.i("upload", "in upload");
        TextView tv= (TextView)findViewById(R.id.uri);
        String uri =tv.getText().toString();
        Log.i("upload", "uri= "+uri);
        Uri myUri= Uri.parse(uri);
        InputStream exampleInputStream=null;
        int last_dot= uri.toString().lastIndexOf(".");
        String Extension = uri.toString().substring(last_dot);
        Log.i("Image",uri.toString().substring(last_dot));

        if(myUri==null){
            Log.i("upload", "uri did not get parsed");
        }
        else{
            Log.i("upload", "uri parsed= "+myUri.getPath());
        }
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
//        CreateAccount ca= new CreateAccount("Ben", "Simmons", "Benny@basketball.edu","threes",exampleInputStream, Extension,true,create_success);
//        CreateAccount ca= new CreateAccount("Joel", "Embiid", "troll@basketball.com","flop",exampleInputStream,Extension,false,Long.valueOf("8694251037"),"dunk",create_success);
//        CreateAccount ca= new CreateAccount("Luca", "Doncic", "Wonderkid@basketball.com","clutch",exampleInputStream,Extension,false,Long.valueOf("8694251037"),"funny",create_success);
//          CreateAccount ca= new CreateAccount("tre", "young", "yeet@basketball.com","logo",exampleInputStream,Extension,false,Long.valueOf("8694251037"),"skills",create_success);

//        CreateAccount ca= new CreateAccount("Malcolm", "Brogdon", "freethrow@basketball.com","assits",exampleInputStream,Extension,false,Long.valueOf("8694251037"),"funny",create_success);
//          CreateAccount ca= new CreateAccount("Miles", "Turner", "denied@basketball.com","block",exampleInputStream,Extension,false,Long.valueOf("8694251037"),"rejections",create_success);
//        CreateAccount ca= new CreateAccount("Kyle", "Kuzma", "Kuz@basketball.com","assists",exampleInputStream,Extension,false,Long.valueOf("8694251037"),"funny",create_success);
//          CreateAccount ca= new CreateAccount("Wilf", "Zaha", "Zaha@fball.com","skills",exampleInputStream,Extension,false,Long.valueOf("8694251037"),"rejections",create_success);
            CreateAccount.Create("John", "Cena", "Invisible@fball.com","skills",exampleInputStream,Extension,false,Long.valueOf("8694251037"),"rejections",create_success);

    }

    public void changeImage(View v){
        ImageView IVPreviewImage= findViewById(R.id.IVPreviewImage);
        String url=  "https://trojan-check-in-and-out183928-dev173416-dev.s3-us-west-2.amazonaws.com/public/IMG_2405.JPG";
        Glide.with(this).load(url).into(IVPreviewImage);
    }


}