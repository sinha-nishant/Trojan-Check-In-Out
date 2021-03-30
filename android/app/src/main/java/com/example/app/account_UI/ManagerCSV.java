package com.example.app.account_UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.app.R;

import java.io.File;

public class ManagerCSV extends AppCompatActivity {
    private Integer readRequestCode = 123;
    private Button upload;
    private TextView fileName;
    private String fileNamePrefix = "File Chosen:";
    private Button confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_c_s_v);
        upload = findViewById(R.id.chooseFileBtn);
        fileName = findViewById(R.id.fileNameTextView);
        confirm = findViewById(R.id.confirmBtn);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Update","Btn pressed");
            }
        });
        confirm.setEnabled(false);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCSVFile();
            }
        });

    }
    public void selectCSVFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        startActivityForResult(Intent.createChooser(intent, "Open CSV"),readRequestCode);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            File csvFile = new File(data.getData().getPath());
             Log.d("Got CSV",csvFile.toString());
             confirm.setEnabled(true);
             String fileChosen = fileNamePrefix+csvFile.getName();
             fileName.setText(fileChosen);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        confirm.setEnabled(false);
        fileName.setText("");
    }
}