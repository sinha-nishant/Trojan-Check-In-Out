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
import android.widget.Toast;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import static android.text.Html.fromHtml;

public class ManagerCSV extends AppCompatActivity {
    private final Integer readRequestCode = 123;
    private Button upload;
    private TextView fileName;
    private String fileNamePrefix = "File Chosen: ";
    private Button confirm;
    public HashMap<String, Integer> map = new HashMap<>();
    private ProgressBar loadingCircle;
    private AlertDialog.Builder builderForDoubleCheck;
    private AlertDialog.Builder builder;
    private AlertDialog updateMessage;
    private List<String> csvBuildingNames = new ArrayList<String>();
    private List<String> cannotUpdate = new ArrayList<String>();
    private List<String> occupancyErrorBuildings = new ArrayList<String>();
    private List<String> notExistErrorBuildings = new ArrayList<String>();

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
             String fileChosen = fileNamePrefix+csvFile.getPath();
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
            String line = bufferedReader.readLine().trim();
            if(line.toLowerCase().equals("update")){
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingCircle.setVisibility(View.VISIBLE);
                        upload.setEnabled(false);
                        updateClick("Update");
                        Log.d("Update","Btn pressed");
                    }
                });
                updateAddProcedure(bufferedReader);
            }else if(line.toLowerCase().equals("remove")){
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingCircle.setVisibility(View.VISIBLE);
                        upload.setEnabled(false);
                        updateClick("Remove");
                        Log.d("Remove","Btn pressed");
                    }
                });
                removeProcedure(bufferedReader);

            }else if(line.toLowerCase().equals("add")){
                updateAddProcedure(bufferedReader);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingCircle.setVisibility(View.VISIBLE);
                        upload.setEnabled(false);
                        updateClick("Add");
                        Log.d("Add","Btn pressed");
                    }
                });

            }else{
                //display dialog that shows format of csv
                Toast.makeText(getApplicationContext(),"Format Error: First line of CSV must have either Add,Update,or Remove",Toast.LENGTH_LONG).show();
                confirm.setEnabled(false);
            }
        }
        csvFile.close();
        bufferedReader.close();
        isr.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        confirm.setEnabled(false);
        fileName.setText("");
        notUpdatedNames.setText("");
    }
    public void updateClick(String procedure){
        doubleCheckMessage(procedure+" Buildings","Are you sure you want to "+procedure+" buildings?", procedure);

    }
    public void removeProcedure(BufferedReader bufferedReader) throws IOException {
        csvBuildingNames.clear();
        cannotUpdate.clear();
        String indicator = bufferedReader.readLine();
        if(indicator.trim().isEmpty() || !indicator.trim().toLowerCase().equals("building name")){
            Toast.makeText(getApplicationContext(), "Format Error: Must follow 'Remove' line with 'Building Name' as a title", Toast.LENGTH_LONG).show();
            confirm.setEnabled(false);
            return;
        }
        String line = bufferedReader.readLine();
        boolean enableButton = true;
        while(line!=null){
            //error check
            //1. must contain only one column of names
            //2. cannot be blank line
            line = line.trim();
            if(line.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Format Error: Cannot have a blank line. Line must have one building name", Toast.LENGTH_LONG).show();
                enableButton = false;
                break;
            }
            if(line.contains(",")){
                Toast.makeText(getApplicationContext(), "Format Error: Must have one column consisting of building names", Toast.LENGTH_LONG).show();
                enableButton = false;
                break;
            }
            csvBuildingNames.add(line);
            line=bufferedReader.readLine();
        }
        confirm.setEnabled(enableButton);
    }
    public void updateAddProcedure(BufferedReader bufferedReader) throws IOException {
        map.clear();
        csvBuildingNames.clear();
        cannotUpdate.clear();
        String indicator = bufferedReader.readLine();
        if(indicator.trim().isEmpty() || !indicator.trim().toLowerCase().equals("building name,capacity")){
            Toast.makeText(getApplicationContext(), "Format Error: Must follow 'Update/Add' line with 'Building Name,Capacity' as a title", Toast.LENGTH_LONG).show();
            confirm.setEnabled(false);
            return;
        }
        String line = bufferedReader.readLine();
        boolean enableButton = true;
        while(line!=null){
            //error check
            //1.each line must have 2 value seperated by comma
            //2. must be string first field and integer second field
            //3. Capacity must be positive number
            List<String> caps = Arrays.asList(line.split(","));
            if(caps.size()!=2){
                //display error message saying must have building name, capacity
                Toast.makeText(getApplicationContext(),"Format Error: CSV should be formatted as : Building Name,Capacity",Toast.LENGTH_LONG).show();
                enableButton=false;
                break;
            }else{
                try {
                    String buildingName = caps.get(0);
                    Integer capacity = Integer.parseInt(caps.get(1));
                    if(capacity<0){
                        Toast.makeText(getApplicationContext(),"Format Error: Building capacity cannot be negative.",Toast.LENGTH_LONG).show();
                        enableButton=false;
                        break;
                    }
                    else if(buildingName.trim().length()==0){
                        Toast.makeText(getApplicationContext(),"Format Error: Can't have empty building name",Toast.LENGTH_LONG).show();
                        enableButton=false;
                        break;
                    }
                    csvBuildingNames.add(buildingName.trim());
                    map.put(buildingName.trim(),capacity);
                    line=bufferedReader.readLine();
                }catch(NumberFormatException e){
                    Toast.makeText(getApplicationContext(),"Format Error: Must follow building name with integer for capacity.",Toast.LENGTH_LONG).show();
                    enableButton=false;
                    break;
                }

            }

        }

        confirm.setEnabled(enableButton);
    }
    public void removeBuildings(){
        MutableLiveData<Boolean> removeMLD = new MutableLiveData<>();
        final Observer<Boolean> removeObserver = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean success){
                if(success){
                    setBuilder("Buildings have been removed","If any buildings were not removed due to not existing or having people in them will be displayed");
                    String textViewMessage = "<u>Following Buildings Not Removed</u><br/>";
                    for (int i = 0; i < occupancyErrorBuildings.size(); i++) {
                        textViewMessage += "<b>" + occupancyErrorBuildings.get(i) + " - Occupied</b><br/><br/>";

                    }
                    for (int i = 0; i < notExistErrorBuildings.size(); i++) {
                        textViewMessage += "<b>" + notExistErrorBuildings.get(i) + "  - Building nonexistent</b><br/><br/>";

                    }

                    notUpdatedNames.setText(fromHtml(textViewMessage,1));

                }else{
                        if(occupancyErrorBuildings.size()>0 && notExistErrorBuildings.size()>0){
                            setBuilder("Error","Some building names don't exist and some of buildings had an occupancy of 0.");
                        }else if (occupancyErrorBuildings.size()>0 ){
                            setBuilder("Error","Buildings entered did not have occupancy of 0");


                        }else if(notExistErrorBuildings.size()>0){
                            setBuilder("Error","Buildings entered didn't exist");
                        }else{
                            setBuilder("Server Issue","Try again.");

                        }
                        if(cannotUpdate.size()>0) {
                            String textViewMessage = "<u>Following Buildings Not Removed</u><br/>";
                            for (int i = 0; i < occupancyErrorBuildings.size(); i++) {
                                textViewMessage += "<b>" + occupancyErrorBuildings.get(i) + " - Occupied</b><br/><br/>";

                            }
                            for (int i = 0; i < notExistErrorBuildings.size(); i++) {
                                textViewMessage += "<b>" + notExistErrorBuildings.get(i) + "  - Building nonexistent</b><br/><br/>";

                            }
                            notUpdatedNames.setText(fromHtml(textViewMessage, 1));
                        }
                }
                occupancyErrorBuildings.clear();
                notExistErrorBuildings.clear();
                cannotUpdate.clear();
                confirm.setEnabled(true);
                upload.setEnabled(true);
            }
        };
        removeMLD.observe(this, removeObserver);
        UpdateCapacityService.removeBuildings(this,csvBuildingNames,removeMLD,occupancyErrorBuildings,notExistErrorBuildings);
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
                  if(map.size()==0){
                      setBuilder("Error","All buildings entered have an error. Either they don't exist or the capacity is less than occupancy. Please look over and fix.");
                  }else{
                      setBuilder("Error","Something went wrong on our side. Please try again later.");
                  }
              }
              cannotUpdate.clear();
              confirm.setEnabled(true);
              upload.setEnabled(true);
            }
        };
        updateMLD.observe(this,updateObserver);
        UpdateCapacityService.updateCapacities(this,map,cannotUpdate,csvBuildingNames,updateMLD);
    }
    public void addNewBuildings() {
        //call a service but do anything UI related here
        MutableLiveData<Boolean> addMLD = new MutableLiveData<>();
        final Observer<Boolean> addObserver = new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable final Boolean success){
                if(success){
                    setBuilder("Buildings have been added","If any buildings were not added, the names will be displayed");
                    String textViewMessage = "<u>Following Buildings Not Been Added</u><br/>";
                    for(int i=0;i<cannotUpdate.size();i++){
                        textViewMessage+="<b>"+cannotUpdate.get(i)+"</b><br/><br/>";
                    }
                    notUpdatedNames.setText(fromHtml(textViewMessage,1));

                }else{
                    setBuilder("Error","Something went wrong on our side. Please try again later");
                }
                cannotUpdate.clear();
                confirm.setEnabled(true);
                upload.setEnabled(true);
            }
        };
        addMLD.observe(this,addObserver);
        UpdateCapacityService.addBuildings(this,map,cannotUpdate,csvBuildingNames,addMLD);
    }
    public void doubleCheckMessage(String title, String message, String functionality){
        loadingCircle.setVisibility(View.INVISIBLE);
        builderForDoubleCheck.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadingCircle.setVisibility(View.VISIBLE);
                        upload.setEnabled(false);
                        confirm.setEnabled(false);
                        if(functionality.equals("Update")){
                            updateCapacities();
                        }else if(functionality.equals("Add")){
                            addNewBuildings();
                        }else if(functionality.equals("Remove")){
                            removeBuildings();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        upload.setEnabled(true);
                        confirm.setEnabled(true);
                        setBuilder("Canceled",functionality+" buildings canceled.");
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