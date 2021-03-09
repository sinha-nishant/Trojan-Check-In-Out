package com.example.app;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class S3Test extends AppCompatActivity {
     

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_s3_test);
//    }

    // One Button
    Button BSelectImage;

    // One Preview Image
    ImageView IVPreviewImage;


    // constant to compare
    // the activity result code
    int SELECT_PICTURE = 200;

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


        // register the UI widgets with their appropriate IDs
        BSelectImage = findViewById(R.id.BSelectImage);
        IVPreviewImage = findViewById(R.id.IVPreviewImage);
//        RelativeLayout layout;
//        layout= findViewById(R.id.progress);
//        Snackbar snackbar= Snackbar.make(layout,"in snackbar",Snackbar.LENGTH_LONG);
//        snackbar.show();
//        snackbar.setText("updated text");
//        snackbar.show();

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
//                    InputStream exampleInputStream = null;
//                    try {
//                        exampleInputStream = getContentResolver().openInputStream(selectedImageUri);
//                    } catch (FileNotFoundException e) {
////                        e.printStackTrace();
//                        Log.i( "ERR", e.getMessage());
//                    }

//                    Amplify.Storage.uploadInputStream(
//                            "Test.png",
//                            exampleInputStream,
//                            result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey()),
//                            storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
//                    );
                }

            }
        }
    }

    public void upload(View v){

        Log.i("upload", "in upload");
        TextView tv= (TextView)findViewById(R.id.uri);
        String uri =tv.getText().toString();
        Log.i("upload", "uri= "+uri);
        Uri myUri= Uri.parse(uri);
        InputStream exampleInputStream=null;
        int last_dot= uri.toString().lastIndexOf(".");
        String Extension = uri.toString().substring(last_dot);
        Log.i("Image",uri.toString().substring(last_dot));
//        ProgressBar circle_thing =(ProgressBar)findViewById(R.id.progressBar4);
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
//            CreateAccount ca= new CreateAccount("Mike", "Scott","DunderMifflin@usc.edu","ahhh",exampleInputStream,true);
//            CreateAccount ca= new CreateAccount("Hritik", "Sapra","Sapra@usc.edu","lollz",exampleInputStream,false, Long.valueOf("9876543210"),"CSCI");
//            CreateAccount ca= new CreateAccount("Virat", "Kohli","Vk17@usc.edu","winner",exampleInputStream,true);
//            CreateAccount ca= new CreateAccount("Rohit", "Sharma","Hitman@usc.edu","reckless",exampleInputStream,false, Long.valueOf("2642001000"),"BUAD");
//            CreateAccount ca= new CreateAccount("Rishab", "Pant","Madman@usc.edu","ComeOnAsh",exampleInputStream,true);
//            CreateAccount ca= new CreateAccount("Washington", "Sundar","96@usc.edu","Solid",exampleInputStream,false, Long.valueOf("8588804678"),"MechEng",circle_thing);

//            Log.i("upload", "finished creating account");
        } catch (FileNotFoundException e) {
            Log.i("upload", "error in uri parsing");
        }
        ProgressBar circle_thing =(ProgressBar)findViewById(R.id.progressBar4);
//        Snackbar bar= Snackbar.make(findViewById(R.id.progress),"",Snackbar.LENGTH_LONG);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Dialog box works");
        builder.setTitle("Status");
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
        AlertDialog alertDialog = builder.create();

//        CreateAccount ca= new CreateAccount("Cristiano", "Ronaldo", "Ron@usc.edu","goat",true,circle_thing,alertDialog);
//        CreateAccount ca= new CreateAccount("Lio", "Messi", "Messi@usc.edu","BEST EVER",false,Long.valueOf("1234567890"),"balling",circle_thing,alertDialog);

//        CreateAccount ca= new CreateAccount("Marcus", "Rashford", "Rashy@usc.edu","PACE",exampleInputStream,true,circle_thing,alertDialog);
//        CreateAccount ca= new CreateAccount("Bruno", "Penandes", "Magnifico@usc.edu","peno",exampleInputStream,false,Long.valueOf("1234567890"),"goals/assists",circle_thing,alertDialog);

//        CreateAccount ca= new CreateAccount("Erling", "Haaland", "Norway@usc.edu","Goals",exampleInputStream,Extension,true,circle_thing,alertDialog);
        CreateAccount ca= new CreateAccount("Kylian", "Mbappe", "NinjaTurtle@usc.edu","winner",exampleInputStream,Extension,false,Long.valueOf("1234567890"),"Ballon Dor",circle_thing,alertDialog);


    }

    public void changeImage(View v){

        ImageView IVPreviewImage= findViewById(R.id.IVPreviewImage);
        String url=  "https://trojan-check-in-and-out183928-dev173416-dev.s3-us-west-2.amazonaws.com/public/IMG_2405.JPG";
        Glide.with(this).load(url).into(IVPreviewImage);
    }

}