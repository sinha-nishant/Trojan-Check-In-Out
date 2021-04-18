package com.example.app.account_UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.app.R;
import com.example.app.services.UpdateCapacityService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import static android.text.Html.fromHtml;

public class ManagerCSV extends AppCompatActivity {
    private Integer readRequestCode = 123;
    private Button upload;
    private TextView fileName;
    private String fileNamePrefix = "File Chosen : ";
    private Button confirm;
    public HashMap<String, Integer> map = new HashMap<>();
    private ProgressBar loadingCircle;
    private AlertDialog.Builder builderForDoubleCheck;
    private AlertDialog.Builder builder;
    private AlertDialog updateMessage;
    private List<String> csvBuildingNames = new ArrayList<String>();
    private List<String> cannotUpdate = new ArrayList<String>();
    private TextView notUpdatedNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_c_s_v);
        upload = findViewById(R.id.chooseFileBtn);
        fileName = findViewById(R.id.fileNameTextView);
        confirm = findViewById(R.id.confirmBtn);
        notUpdatedNames = findViewById(R.id.cannotUpdateNames);
        notUpdatedNames.setMovementMethod(new ScrollingMovementMethod());
        loadingCircle = findViewById(R.id.progressCircle);
        loadingCircle.setVisibility(View.GONE);
        builder = new AlertDialog.Builder(this);
        builderForDoubleCheck = new AlertDialog.Builder(this);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingCircle.setVisibility(View.VISIBLE);
                upload.setEnabled(false);
                updateClick();
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
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Open CSV"),readRequestCode);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            File csvFile = new File(data.getData().getPath());
             Log.d("Got CSV",csvFile.toString());
             String fileChosen = fileNamePrefix+csvFile.getName();
             fileName.setText(fileChosen);
            try {
                readCSV(data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public void readCSV(Uri uri) throws IOException {
        InputStream csvFile = getContentResolver().openInputStream(uri);
        Reader isr = new InputStreamReader(csvFile);
        BufferedReader bufferedReader = new BufferedReader(isr);
        map.clear();
        csvBuildingNames.clear();
        cannotUpdate.clear();
        if(bufferedReader.ready()){
            String line = bufferedReader.readLine();
            if(line.toLowerCase().equals("update")){
                updateProcedure(bufferedReader);
            }else if(line.toLowerCase().equals("remove")){

            }else if(line.toLowerCase().equals("add")){

            }
        }
        csvFile.close();
        bufferedReader.close();
        isr.close();
        Log.d("Map",map.toString());
        confirm.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        confirm.setEnabled(false);
        fileName.setText("");
        notUpdatedNames.setText("");
    }
    public void updateClick(){
        doubleCheckMessage("Update Building Capacities","Are you sure you want to update capacities?");

    }
    public void updateProcedure(BufferedReader bufferedReader) throws IOException {
        bufferedReader.readLine();
        String line = bufferedReader.readLine();
        Log.d("Building IS",line);
        while(line!=null){
            List<String> caps = Arrays.asList(line.split(","));
            String buildingName = caps.get(0);
            Integer capacity = Integer.parseInt(caps.get(1));
            csvBuildingNames.add(buildingName);
            map.put(buildingName,capacity);
            line=bufferedReader.readLine();
        }

    }
    public void updateCapacities(){
        MutableLiveData<Boolean> updateMLD = new MutableLiveData<>();
        final Observer<Boolean> updateObserver = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean success){
              if(success){
                  setBuilder("Buildings have been updated","If any buildings were not updated, the names will be displayed");
                  String textViewMessage = "<u>Following Buildings Not Updated</u><br/>";
                  for(int i=0;i<cannotUpdate.size();i++){
                      textViewMessage+="<b>"+cannotUpdate.get(i)+"</b><br/><br/>";

                  }
                  notUpdatedNames.setText(fromHtml(textViewMessage,1));

              }else{
                  setBuilder("Error","Something went wrong on our side. Please try again later");

              }
              confirm.setEnabled(true);
              upload.setEnabled(true);
            }
        };
        updateMLD.observe(this,updateObserver);
        UpdateCapacityService.updateCapacities(this,map,cannotUpdate,csvBuildingNames,updateMLD);
    }
    public void doubleCheckMessage(String title, String message){
        loadingCircle.setVisibility(View.INVISIBLE);
        builderForDoubleCheck.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //check in student since they double checked
                        loadingCircle.setVisibility(View.VISIBLE);
                        upload.setEnabled(false);
                        confirm.setEnabled(false);
                        updateCapacities();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        upload.setEnabled(true);
                        confirm.setEnabled(true);
                        setBuilder("Canceled","Update building capacities is canceled.");

                    }
                })
        ;
        updateMessage=builderForDoubleCheck.create();
        //stop loading bar
        updateMessage.show();
    }
    public void setBuilder(String title, String message){
        loadingCircle.setVisibility(View.GONE);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        updateMessage=builder.create();
        //stop loading bar
        updateMessage.show();
    }
}