 package com.example.app.building;

 import android.app.AlertDialog;
 import android.content.DialogInterface;
 import android.content.Intent;
 import android.os.Bundle;
 import android.text.InputType;
 import android.view.LayoutInflater;
 import android.view.Menu;
 import android.view.MenuItem;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.ImageView;
 import android.widget.LinearLayout;
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
 import com.example.app.account_UI.ManagerSearch;
 import com.example.app.firebaseDB.FbCheckInOut;
 import com.example.app.firebaseDB.FbQuery;
 import com.example.app.firebaseDB.FbUpdate;
 import com.example.app.firebaseDB.FirestoreConnector;
 import com.example.app.services.QRGeneration;
 import com.example.app.services.UpdateCapacityService;
 import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
 import com.firebase.ui.firestore.FirestoreRecyclerOptions;
 import com.google.android.material.bottomnavigation.BottomNavigationView;
 import com.google.android.material.floatingactionbutton.FloatingActionButton;
 import com.google.firebase.firestore.FirebaseFirestore;
 import com.google.firebase.firestore.Query;

 import javax.annotation.Nullable;

 import static android.text.Html.fromHtml;

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
        FloatingActionButton addBuilding =findViewById(R.id.addBuildingBtn);
        addBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBuildingAlertDialog("Add New Building","Enter the name of the new building and its capacity");
            }
        });
        fireStore = FirestoreConnector.getDB();
        mFirestoreData = findViewById(R.id.studentRView);

         BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
         Menu menu = bottomNavigationView.getMenu();
         MenuItem menuItem = menu.getItem(0);
         menuItem.setChecked(true);

         findViewById(R.id.manager_search).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent1 = new Intent(BuildingsOccupancyList.this, ManagerSearch.class);
                 startActivity(intent1);
             }
         });

         findViewById(R.id.manager_home).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent2 = new Intent(BuildingsOccupancyList.this, ManagerHome.class);
                 startActivity(intent2);
             }
         });



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
                                        openStudentList(view,model.getName());
                                        return true;
                                    case R.id.remove_menu:
                                        //display message asking if you want to delete
                                        if(model.getOccupancy()==0){
                                            confirmRemoveBuildingMessage("Remove Building", "Are you sure you want to delete "+holder.buildingName.getText(),holder.buildingName.getText().toString());
                                        }else{
                                            Toast.makeText(getApplicationContext(),"Error: Cannot remove a building while students inside it.",Toast.LENGTH_LONG).show();
                                        }
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
                     String errorMessage = "Error: Capacity entered is less than current occupancy";
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
     public void confirmRemoveBuildingMessage(String title, String message,String buildingToRemove){
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle(title);
         builder.setMessage(message);

//         FbQuery.getBuilding(buildingToRemove,buildingsMLD);
         builder.setPositiveButton("Remove Building", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 //remove the building since already exists and  check again still occupancy is 0

                 MutableLiveData<Boolean> removeMLD = new MutableLiveData<>();
                 final Observer<Boolean> removeObserver = new Observer<Boolean>(){
                     @Override
                     public void onChanged(@Nullable final Boolean success){
                         if(success){
                             Toast.makeText(getApplicationContext(),"Successfully removed.",Toast.LENGTH_LONG).show();
                         }else{
                             Toast.makeText(getApplicationContext(),"Failed to remove.Try again later or when occupancy is 0.",Toast.LENGTH_LONG).show();
                         }
                     }
                 };
                 removeMLD.observe(BuildingsOccupancyList.this,removeObserver);
                 MutableLiveData<Building> buildingsMLD = new MutableLiveData<>();
                 final Observer< Building> buildingsObserver = new Observer<Building>(){
                     @Override
                     public void onChanged(@Nullable final Building building){
                         if(building.getOccupancy()==0){
                             FbUpdate.deleteBuilding(buildingToRemove,removeMLD);

                         }else{
                             removeMLD.setValue(false);
                         }
                     }
                 };

                 buildingsMLD.observe(BuildingsOccupancyList.this,buildingsObserver);
                 /*uncomment once firebase remove single building is done*/
                 FbQuery.getBuilding(buildingToRemove,buildingsMLD);
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
     public void addBuildingAlertDialog(String title, String message){
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle(title);
         builder.setMessage(message);

         // Set up the input
         LinearLayout linearLayout= new LinearLayout(this);
         linearLayout.setOrientation(LinearLayout.VERTICAL);
         final EditText newBuildingName = new EditText(this);
         final EditText capacity = new EditText(this);
         newBuildingName.setHint("Building Name");
         capacity.setHint("Building Capacity");
         // Specify the type of input expected
         newBuildingName.setInputType(InputType.TYPE_CLASS_TEXT);
         capacity.setInputType(InputType.TYPE_CLASS_NUMBER);
         linearLayout.addView(newBuildingName);
         linearLayout.addView(capacity);
         builder.setView(linearLayout);

         // Set up the buttons
         builder.setPositiveButton("Add Building", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 //first see if building already exists
                 final Integer cap;
                 if(!capacity.getText().toString().trim().isEmpty()  && !newBuildingName.getText().toString().trim().isEmpty() ){
                     cap = Integer.parseInt(capacity.getText().toString());
                 }else{//if user didn't enter both capacity and name don't go any further
                     cap=null;
                     dialog.cancel();
                     Toast.makeText(getBaseContext(),"Enter a capacity and name please.",Toast.LENGTH_LONG).show();
                     return;
                 }
                 MutableLiveData<Building> buildingMLD = new MutableLiveData<>();
                 MutableLiveData<Boolean> addBuildingMLD = new MutableLiveData<>();
                 final Observer<Building> getBuildingObserver = new Observer<Building>() {
                     @Override
                     public void onChanged(Building building) {
                         if(building==null){//building doesn't exist so add it
                             Building b = new Building(newBuildingName.getText().toString().trim(),cap);
                             FbUpdate.addBuilding(b,addBuildingMLD);

                         }else{//building name exists so don't add it
                             Toast.makeText(getBaseContext(),"Building already exists",Toast.LENGTH_LONG).show();
                         }
                     }
                 };
                 final Observer<Boolean> addBuildingObserver = new Observer<Boolean>() {
                     @Override
                     public void onChanged(Boolean success) {
                         if(success){//building was added
                             Toast.makeText(getBaseContext(),"Building added",Toast.LENGTH_LONG).show();
                         }else{//building was not added
                             Toast.makeText(getBaseContext(),"Something went wrong on our side. Try again later.",Toast.LENGTH_LONG).show();
                         }
                     }
                 };
                 buildingMLD.observe(BuildingsOccupancyList.this,getBuildingObserver);
                 addBuildingMLD.observe(BuildingsOccupancyList.this,addBuildingObserver);
                 FbQuery.getBuilding(newBuildingName.getText().toString().trim(),buildingMLD);

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