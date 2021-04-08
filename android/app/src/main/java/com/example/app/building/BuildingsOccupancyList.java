 package com.example.app.building;

 import android.app.AlertDialog;
 import android.content.DialogInterface;
 import android.content.Intent;
 import android.os.Bundle;
 import android.text.InputType;
 import android.view.LayoutInflater;
 import android.view.MenuItem;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.ImageView;
 import android.widget.PopupMenu;
 import android.widget.TextView;
 import android.widget.Toast;

 import androidx.annotation.NonNull;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.lifecycle.MutableLiveData;
 import androidx.lifecycle.Observer;
 import androidx.recyclerview.widget.LinearLayoutManager;
 import androidx.recyclerview.widget.RecyclerView;

 import com.example.app.R;
 import com.example.app.account_UI.ManagerHome;
 import com.example.app.firebaseDB.FirestoreConnector;
 import com.example.app.services.QRGeneration;
 import com.example.app.services.UpdateCapacityService;
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
public MutableLiveData<Boolean> updatedMLD = new MutableLiveData<>();

private String m_Text = "";
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hassib_test);
        fireStore = FirestoreConnector.getDB();
        mFirestoreData = findViewById(R.id.studentRView);

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
                holder.menu_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //creating a popup menu
                        PopupMenu popup = new PopupMenu(getApplicationContext(),holder.menu_btn);
                        //inflating menu from xml resource
                        popup.inflate(R.menu.card_menu);
                        popup.setForceShowIcon(true);
                        //adding click listener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.capacity_menu:
                                        //handle menu1 click
                                        alertDialogInput("Update Capacity", "Enter the new capacity for "+holder.buildingName.getText(),model.getOccupancy(),model.getCapacity(),holder.buildingName.getText().toString());
                                        return true;
                                    case R.id.students_menu:
                                        //handle menu2 click
                                        Toast toast2 = Toast.makeText(getApplicationContext(),"Students List Pressed",Toast.LENGTH_SHORT);
                                        toast2.show();
                                        openStudentList(view,model.getName());
                                        return true;

                                    default:
                                        return false;
                                }
                            }
                        });
                        //displaying the popup
                        popup.show();

                    }
                });
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
        private Button menu_btn;
//        private TextView capacity;
         public BuildingViewHolder(@NonNull View itemView) {
             super(itemView);
             qrImage=itemView.findViewById(R.id.qrImageView);
             buildingName = itemView.findViewById(R.id.buildingName);
             occupancy = itemView.findViewById(R.id.buildingOccupancy);
             menu_btn = itemView.findViewById(R.id.menu_button);
         }
     }

     public void openManagerProfile(View v){
         Intent i = new Intent(this, ManagerHome.class);
         startActivity(i);
     }
     public void openStudentList(View v, String buildingName){
         Intent i = new Intent(this, BuildingStudents.class);
         i.putExtra("buildingName", buildingName);
         startActivity(i);
     }
     public void alertDialogInput(String title, String message, Integer currentOccupancy, Integer currentCapacity,String buildingName){
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle(title);
         builder.setMessage(message);

        // Set up the input
         final EditText input = new EditText(this);
         // Specify the type of input expected
         input.setInputType(InputType.TYPE_CLASS_NUMBER);
         builder.setView(input);

        // Set up the buttons
         builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 m_Text = input.getText().toString();
                 Integer new_capacity = Integer.parseInt(m_Text);
                 if(new_capacity.equals(currentCapacity)){
                     String errorMessage = "Error: Capacity is already "+currentCapacity.toString();
                     Toast toast = Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_LONG);
                     toast.show();
                 }
                 else if(new_capacity>=currentOccupancy){
                     //update capacity
                     Toast toastSuccess = Toast.makeText(getApplicationContext(),"Successfully Updated",Toast.LENGTH_SHORT);
                     Toast toastError = Toast.makeText(getApplicationContext(),"Update Failed. Try Again.",Toast.LENGTH_SHORT);
                     setMLD(toastSuccess,toastError);
                     UpdateCapacityService.updateCapacity(buildingName,new_capacity,updatedMLD);
                 }else if(new_capacity<currentOccupancy){
                     //display error message
                     String errorMessage = "Error: Capacity entered is less than or equal to current occupancy";
                     Toast toast = Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_LONG);
                     toast.show();
                 }
             }
         });
         builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 dialog.cancel();
             }
         });

         builder.show();
     }
     public  void setMLD(Toast successful, Toast error){
         final Observer<Boolean> updatedObserver = new Observer<Boolean>() {
             @Override
             public void onChanged(Boolean success) {
                 if(success){
                     successful.show();
                 }else{
                     error.show();
                 }
             }
         };
         updatedMLD.observe(this,updatedObserver);

     }

}