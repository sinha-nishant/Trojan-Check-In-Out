package com.example.app;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;

import java.io.FileNotFoundException;
import java.io.InputStream;

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
    Boolean ImageSet=false;
    String email,password;
    ProgressBar studentProgress;
    AlertDialog alertDialog;
    private final MutableLiveData<Integer> create_success = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_name);

        profileButton= (Button) findViewById(R.id.choosePic);
        img= (ImageView) findViewById(R.id.imageView3);
        firstNameInput = (EditText) findViewById(R.id.managerFirstName);
        lastNameInput = (EditText) findViewById(R.id.managerLastName);
        studentProgress= (ProgressBar)findViewById(R.id.progressBar5);
        studentProgress.setVisibility(View.GONE);

        //Get the bundle
        Bundle bundle = getIntent().getExtras();

        //Extract the data…
        email = bundle.getString("email");
        password = bundle.getString("password");

        submitButton = (Button) findViewById(R.id.nameButtonM);
        AmplifyInit();
        DialogInit();
        MutableInit();
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();

            }
        });

//        submitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fName = firstNameInput.getText().toString();
//                lName = lastNameInput.getText().toString();
//
//                //used for popups to user
//                //showToast(email);
//                //showToast(password);
//
//
//                profileButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        imageChooser();
//                    }
//                });
//                if(fName.length() == 0 || lName.length() == 0)
//                    showToast("First or last name is blank");
//                else
//                {
//                    showToast(email);
//                    //TODO Arjun: add data to S3
//                    //TODO Markus: redirect to Manager Profile, sending email string in bundle
//                }
//            }
//        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fName = firstNameInput.getText().toString();
                lName = lastNameInput.getText().toString();

                //used for popups to user
                //showToast(email);
                //showToast(password);

                if(fName.length() == 0 || lName.length() == 0){
                    showToast("First or last name is blank");
                    return;
                }
                studentProgress.setVisibility(View.VISIBLE);
                submit();

            }
        });
    }

    private void showToast(String text)
    {
        Toast.makeText(ManagerName.this, text, Toast.LENGTH_SHORT).show();
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
                    img.setImageURI(selectedImage);
                    ImageSet = true;


                }

            }
        }
    }

    public void submit(){
        if(ImageSet==false){
//            CreateAccount.Create(fName, lName, email,password,false,create_success);
            CreateAccount.CreateManager(fName,lName,email,password,create_success);
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
        CreateAccount.CreateManager(fName, lName, email,password,exampleInputStream,create_success);

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
                else{
                    //stop progress bar
                    studentProgress.setVisibility(View.GONE);
                    //Generate popupmessage
                    Log.d("Create", "success");


                    LogInOut.SaveData(ManagerName.this,email,0L);

                    alertDialog.setMessage("Succeeded in creating your account");
                    alertDialog.show();

                }
            }

        };
        create_success.observe(this, obs);
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
                        if(create_success.getValue()>=3){
                            openProfile();
                        }

                    }
                });
        alertDialog = builder.create();
    }


    public void openProfile() {
        //What unique identifier will be used to draw up profile page? Email?
        Intent i = new Intent(this, ManagerSearch.class);
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        i.putExtras(bundle);
        startActivity(i);
    }


}