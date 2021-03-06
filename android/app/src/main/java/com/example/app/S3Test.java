package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;

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
        if(myUri==null){
            Log.i("upload", "uri did not get parsed");
        }
       else{
            Log.i("upload", "uri parsed= "+myUri.getPath());
        }
        try {
            InputStream exampleInputStream = getContentResolver().openInputStream(Uri.parse(uri));
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
            CreateAccount ca= new CreateAccount("Washington", "Sundar","96@usc.edu","Solid",exampleInputStream,false, Long.valueOf("8588804678"),"MechEng");

            Log.i("upload", "finished creating account");
        } catch (FileNotFoundException e) {
            Log.i("upload", "error in uri parsing");
        }

    }

    public void changeImage(View v){
//        ImageView IVPreviewImage= findViewById(R.id.IVPreviewImage);
//        Uri uri=  Uri.parse("https://trojan-check-in-and-out183928-dev173416-dev.s3-us-west-2.amazonaws.com/public/Derulo%40usc.edu.png");
//        IVPreviewImage.setImageURI(uri);

        ImageView IVPreviewImage= findViewById(R.id.IVPreviewImage);
        String urlString=  "https://trojan-check-in-and-out183928-dev173416-dev.s3-us-west-2.amazonaws.com/public/IMG_2405.JPG";

        Bitmap bm = null;
        try {
            URL url = new URL(urlString);


            URLConnection conn = url.openConnection();
            InputStream stream = conn.getInputStream();
            bm = BitmapFactory.decodeStream(stream);
        } catch(IOException e) {
            System.out.println(e);
        }
        IVPreviewImage.setImageBitmap(bm);
    }

}