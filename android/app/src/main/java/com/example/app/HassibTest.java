 package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Observable;

import javax.annotation.Nullable;

 public class HassibTest extends AppCompatActivity {

public static final String shared_pref = "sharedPrefs";
public static final String emailEntry = "email";
public static final String idEntry = "uscid";
private RecyclerView mFirestoreData;
private FirebaseFirestore fireStore;
private FirestoreRecyclerAdapter firestoreRecyclerAdapter;
     @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hassib_test);
        fireStore = FirestoreConnector.getDB();
        mFirestoreData = findViewById(R.id.recyclerList);

        //Query
        Query query = fireStore.collection("Buildings");



        //RecycclerOptions
        FirestoreRecyclerOptions<Building> options = new FirestoreRecyclerOptions.Builder<Building>().setQuery(query,Building.class).build();
         firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Building, BuildingViewHolder>(options) {
            @NonNull
            @Override
            public BuildingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View theView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_in_list,parent,false);
                return new BuildingViewHolder(theView);
            }

            @Override
            protected void onBindViewHolder(@NonNull BuildingViewHolder holder, int position, @NonNull Building model) {
                String buildingText = model.getName();
                String occupancyText = "Occupancy: "+model.getOccupancy()+"/"+model.getCapacity();
//                String capacityText = "Capacity: "+model.getCapacity();
                holder.qrImage.setImageBitmap(QRGeneration.GetBitMap(model.getName().toString()));

                holder.buildingName.setText(buildingText);
                holder.occupancy.setText(occupancyText);
//                holder.capacity.setText(capacityText);

            }
        };
         mFirestoreData.setHasFixedSize(true);
         mFirestoreData.setLayoutManager(new LinearLayoutManager(this));
         mFirestoreData.setAdapter(firestoreRecyclerAdapter);



        //View Holder






    }

     @Override
     protected void onStart() {
         super.onStart();
         firestoreRecyclerAdapter.startListening();
     }

     @Override
     protected void onStop() {
         super.onStop();
         firestoreRecyclerAdapter.stopListening();
     }

     private class BuildingViewHolder extends RecyclerView.ViewHolder {
        private ImageView qrImage;
        private TextView buildingName;
        private TextView occupancy;
//        private TextView capacity;
         public BuildingViewHolder(@NonNull View itemView) {
             super(itemView);
             qrImage=itemView.findViewById(R.id.qrImageView);
             buildingName = itemView.findViewById(R.id.buildingName);
             occupancy = itemView.findViewById(R.id.buildingOccupancy);
//             capacity = itemView.findViewById(R.id.buildingCapacity);

         }
     }
//     public  void SaveData(){
//         SharedPreferences sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
//         SharedPreferences.Editor editor = sharedPreferences.edit();
//         editor.putString(emailEntry,eEmail.getText().toString());
//         editor.putLong(idEntry,uscid);
//         editor.apply();
//         LoadData();
//     }
//    public void LoadData(){
//        SharedPreferences sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
//        String test_retrieve_email = sharedPreferences.getString(emailEntry,"");
//        Long test_retrieve_id = sharedPreferences.getLong(idEntry,0);
//        //load id
//        Log.d("Saved email is : ", test_retrieve_email);
//        Log.d("Saved ID is : ", test_retrieve_id.toString());
//    }
//    public void QRCodeBtn(View view){
//
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//        try{
//            BitMatrix bitMatrix = qrCodeWriter.encode(buildingName.getText().toString(), BarcodeFormat.QR_CODE, 200,200);
//            Bitmap bitmap = Bitmap.createBitmap(200,200, Bitmap.Config.RGB_565);
//            for(int i =0;i<200;i++){//go through all 200x200 pixels
//                for(int y =0;y<200;y++){
//                    bitmap.setPixel(i,y,bitMatrix.get(i,y)? Color.BLACK : Color.WHITE);
//                }
//            }
//            qrimage.setImageBitmap(bitmap);
//
//            buildingName.setText(bitmap.toString());
//        } catch(Exception e){
//
//        }
//
//    }
//


}