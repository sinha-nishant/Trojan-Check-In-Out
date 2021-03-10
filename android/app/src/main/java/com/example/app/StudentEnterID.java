package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class StudentEnterID extends AppCompatActivity {

    private Button idButton;
    String id, major;
    EditText idInput;
    Spinner majorSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_enter_i_d);

        idInput = (EditText) findViewById(R.id.studentID);
        majorSelect = (Spinner) findViewById(R.id.studentMajor);

        idButton = (Button) findViewById(R.id.signup);
        idButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = idInput.getText().toString();
                major = majorSelect.getSelectedItem().toString();
                if(id.length() != 10)
                {
                    showToast("Invalid USC ID");
                }
                else {
                    Bundle bundle = getIntent().getExtras();

                    //Extract the data…
                    openPhoto(bundle.getString("email"), bundle.getString("password"),
                            bundle.getString("fName"), bundle.getString("lName"), id, major);
                }
            }
        });
    }

    public void openPhoto(String email, String password, String fName, String lName, String id, String major) {
        Intent i = new Intent(this, StudentUploadPhoto.class);
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("password", password);
        bundle.putString("fName", fName);
        bundle.putString("lName", lName);
        bundle.putString("id", id);
        bundle.putString("major", major);
        i.putExtras(bundle);
        startActivity(i);
    }

    private void showToast(String text)
    {
        Toast.makeText(StudentEnterID.this, text, Toast.LENGTH_SHORT).show();
    }
}