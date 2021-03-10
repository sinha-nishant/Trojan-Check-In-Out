package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class StudentUploadPhoto extends AppCompatActivity {

    String email, password, fName, lName, id, major;

    ImageView uploadImage;
    Button bUploadImage;
    private static final int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_upload_photo);

        uploadImage = (ImageView)findViewById(R.id.imageToUpload);
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

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });

        bUploadImage = (Button)findViewById(R.id.uploadImageButton);
        bUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //BACKEND: probably here is where you want to upload to database, wait until upload
                //is complete
                showToast("Success!");
                //go back to John's page, would normally be profile
                openProfile();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        StudentUploadPhoto.super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null)
        {
            Uri selectedImage = data.getData();
            uploadImage.setImageURI(selectedImage);
        }
    }

    private void showToast(String text)
    {
        Toast.makeText(StudentUploadPhoto.this, text, Toast.LENGTH_LONG).show();
    }

    public void openProfile() {
        //What unique identifier will be used to draw up profile page? Email?
        Intent i = new Intent(this, JohnTest.class);
        /*
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        i.putExtras(bundle);
         */
        startActivity(i);
    }
}

