package com.example.app.pre_login_UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.example.app.R;
import com.example.app.account_UI.StudentProfile;
import com.example.app.log_create.CreateAccount;
import com.example.app.log_create.LogInOut;

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.annotation.Nullable;

public class StudentUploadPhoto extends AppCompatActivity {

    String email, password, fName, lName, id, major;

    ImageView uploadImage;
    Button bUploadImage;
    ProgressBar studentProgress;
    AlertDialog alertDialog;
    Uri selectedImage;
    Boolean ImageSet=false;
    private final MutableLiveData<Integer> create_success = new MutableLiveData<>();
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
        MutableInit();


        //To Backend: What data y'all get:
        Bundle bundle = getIntent().getExtras();

        email = bundle.getString("email");
        password = bundle.getString("password");
        fName = bundle.getString("fName");
        lName = bundle.getString("lName");
        id = bundle.getString("id");
        major = bundle.getString("major");

        showToast(fName + " " + lName + " (" + id + ")\n"
                + major + " " + email + "\nPass: " + password);
        //End data

        bUploadImage = (Button)findViewById(R.id.uploadImageButton);


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
                selectedImage = data.getData();
                Log.i("Image",selectedImage.toString());
                if (null != selectedImage) {
                    // update the preview image in the layout
                    uploadImage.setImageURI(selectedImage);
                    ImageSet = true;


                }

            }
        }
    }






    private void showToast(String text)
    {
        Toast.makeText(StudentUploadPhoto.this, text, Toast.LENGTH_LONG).show();
    }

    public void openProfile() {
        //What unique identifier will be used to draw up profile page? Email?
        Intent i = new Intent(this, StudentProfile.class);
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("uscID",id);
        i.putExtras(bundle);
        startActivity(i);
    }




    public void submit(View v){
        studentProgress.setVisibility(View.VISIBLE);
        if(ImageSet==false){
//            CreateAccount.Create(fName, lName, email,password,false,create_success);
            CreateAccount.CreateStudent(fName,lName,email,password,Long.valueOf(id),major,create_success);
            return;
        }

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
        CreateAccount.CreateStudent(fName, lName, email,password,exampleInputStream,Long.valueOf(id),major,create_success);

    }


    public void AmplifyInit(){
        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());
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
                        openProfile();
                    }
                });
        alertDialog = builder.create();
    }

    public void MutableInit(){
        final Observer<Integer> obs = new Observer<Integer>(){
            @Override
            public void onChanged(@Nullable final Integer b){
                if(b==null){
                    alertDialog.setMessage("Failed to work");
                    alertDialog.show();
                    return;
                }
                if(b==0){

                    studentProgress.setVisibility(View.GONE);
                    alertDialog.setMessage("Error. This Email already exists.");
                    alertDialog.show();



                }
                else if(b==1){
                    studentProgress.setVisibility(View.GONE);
                    //switch page
                    alertDialog.setMessage("Error. Failed to create your account successfully");
                    alertDialog.show();

                }
                else if(b==2){
                    studentProgress.setVisibility(View.GONE);
                    //switch page
                    alertDialog.setMessage("Error. This ID already exists");
                    alertDialog.show();

                }
                else if(b==3){
                    studentProgress.setVisibility(View.GONE);
                    //switch page
                    alertDialog.setMessage("Could not upload image to AWS S3 storage");
                    alertDialog.show();

                }
                else{
                    //stop progress bar
                    studentProgress.setVisibility(View.GONE);
                    //Generate popupmessage
                    Log.d("Create", "success");


                    LogInOut.SaveData(StudentUploadPhoto.this,email,Long.valueOf(id));

                    alertDialog.setMessage("Succeeded in creating your account");
                    alertDialog.show();

                }
            }

        };
        create_success.observe(this, obs);
    }
}

