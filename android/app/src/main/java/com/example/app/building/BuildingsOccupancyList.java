 package com.example.app.building;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app.account_UI.ManagerProfile;
import com.example.app.R;
import com.example.app.firebaseDB.FirestoreConnector;
import com.example.app.services.QRGeneration;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

 public class BuildingsOccupancyList extends AppCompatActivity {

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
        Query query = fireStore.collection("Buildings").orderBy("name");
        //RecyclerOptions
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
                String occupancyText = String.format("Occupancy: %d/%d", model.getOccupancy(), model.getCapacity());
                holder.qrImage.setImageBitmap(QRGeneration.GetBitMap(model.getName().toString()));
                holder.buildingName.setText(buildingText);
                holder.occupancy.setText(occupancyText);
            }
        };
         mFirestoreData.setHasFixedSize(true);
         mFirestoreData.setLayoutManager(new LinearLayoutManager(this));
         mFirestoreData.setAdapter(firestoreRecyclerAdapter);

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
         }
     }

     public void openManagerProfile(View v){
         Intent i = new Intent(this, ManagerProfile.class);
         startActivity(i);
     }

}