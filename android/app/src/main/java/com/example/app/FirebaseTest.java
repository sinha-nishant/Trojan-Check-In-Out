package com.example.app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class FirebaseTest extends AppCompatActivity implements FirestoreConnector {

    public TextView textBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_test);
        textBox = findViewById(R.id.textBox);
    }

    // FOR TESTING PURPOSES
    public void test(View v) {
        getBuildingOccupanciesRealtime();
    }

    public void getBuildingOccupanciesRealtime() {
        FirestoreConnector.getDB().collection("Buildings").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("OCCUPANCY", "realtime occupancy failed");
                }

                else {
                  if (value != null && !value.isEmpty()) {
                      Log.d("OCCUPANCY", "received occupancy update");
                      DocumentChange dc = value.getDocumentChanges().get(0);
                      String name = (String) dc.getDocument().get("name");
                      String occupancy = String.valueOf(dc.getDocument().get("occupancy"));
                      textBox.setText(String.format("%s: %s", name, occupancy));
                  }

                  else {
                      Log.d("OCCUPANCY", "no realtime changes");
                  }
                }
            }
        });
    }

    // CheckInOut - TO FIX: DOESN'T UPDATE OCCUPANCY YET
    // Arjun: Updated paramaters to allow for callback
    public static void checkIn (Long uscID, StudentActivity sa,MutableLiveData<Boolean> success) {
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        Query studentQuery = accounts.whereEqualTo("uscID", uscID);
        studentQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document: task.getResult()) {
                            accounts.document(document.getId()).update("activity", FieldValue.arrayUnion(sa));
                        }

                        CollectionReference buildings = FirestoreConnector.getDB().collection("Buildings");
                        Query buildingQuery = buildings.whereEqualTo("name", sa.getBuildingName());
                        buildingQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("occupancy",FieldValue.increment(1));
                                        updates.put("students", FieldValue.arrayUnion(uscID));
                                        for (QueryDocumentSnapshot document: task.getResult()) {
                                            buildings.document(document.getId())
                                                    .update(updates);
                                        }

                                        // call callback function
                                        //Arjun added callback
//
                                        success.setValue(true);

                                    }
                                }
                                else{
                                    //Arjun added callback
//
                                    success.setValue(false);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public static void checkOut(Long uscID, StudentActivity sa, Date checkOutTime,MutableLiveData<Boolean> success) {
        FirestoreConnector.getDB().collection("Accounts")
                .whereEqualTo("uscID", uscID)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    DocumentReference account = task.getResult().getDocuments().get(0).getReference();

                    StudentActivity newSA = new StudentActivity(
                            sa.getBuildingName(),
                            sa.getCheckInTime(),
                            checkOutTime
                    );
                    account.update(
                            "activity", FieldValue.arrayRemove(sa),
                            "activity", FieldValue.arrayUnion(newSA))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FirestoreConnector.getDB().collection("Buildings")
                                                .whereEqualTo("name", sa.getBuildingName())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                            DocumentReference building = task.getResult().getDocuments().get(0).getReference();
                                                            Map<String, Object> updates = new HashMap<>();
                                                            updates.put("occupancy", FieldValue.increment(-1));
                                                            updates.put("students", FieldValue.arrayRemove(uscID));
                                                            building.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Log.d("CHECKOUT", "checked out successfully!");
                                                                        //Arjun added callback
//
                                                                        success.setValue(true);
                                                                    }
                                                                    else{
                                                                        //Arjun added callback
//
                                                                        success.setValue(false);
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                }
            }
        });
    }



    //Arjun added another checkout function to deal with student account delete
    public static void checkOut(Long uscID, StudentActivity sa, Date checkOutTime, MutableLiveData<Boolean> success, String email) {
        FirestoreConnector.getDB().collection("Accounts")
                .whereEqualTo("uscID", uscID)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    DocumentReference account = task.getResult().getDocuments().get(0).getReference();

                    StudentActivity newSA = new StudentActivity(
                            sa.getBuildingName(),
                            sa.getCheckInTime(),
                            checkOutTime
                    );
                    account.update(
                            "activity", FieldValue.arrayRemove(sa),
                            "activity", FieldValue.arrayUnion(newSA))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FirestoreConnector.getDB().collection("Buildings")
                                                .whereEqualTo("name", sa.getBuildingName())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                            DocumentReference building = task.getResult().getDocuments().get(0).getReference();
                                                            Map<String, Object> updates = new HashMap<>();
                                                            updates.put("occupancy", FieldValue.increment(-1));
                                                            updates.put("students", FieldValue.arrayRemove(uscID));
                                                            building.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Log.d("CHECKOUT", "checked out successfully!");
                                                                        //Arjun added callback
//
                                                                        FirebaseTest.deleteAccount(email,success);
                                                                    }
                                                                    else{
                                                                        //Arjun added callback
                                                                          success.setValue(false);
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                }
            }
        });
    }

    // DataRetriever
    public static void getBuilding(String buildingName, ProgressBar circle) {
        FirestoreConnector.getDB().collection("Buildings").whereEqualTo("name", buildingName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                    Building building = (Building) ds.toObject(Building.class);
                    List<Long> students = new ArrayList<>();
                    if (((List<Long>) ds.get("students")) == null) {
                        Log.d("BUILDING", "NULL " + ds.get("students"));
                    }
                    else {
                        for (Long uscID: (List<Long>) ds.get("students")) {
                            Log.d("BUILDING", "ITERATED");
                            students.add(uscID);
                        }
                    }
                    building.setStudents_ids(students);
                    Log.d("BUILDING", building.toString());
                }

                // callback
            }
        });
    }




    public static void getBuilding(String buildingName, MutableLiveData<Boolean> success,Long id,StudentActivity sa,Date time) {
        FirestoreConnector.getDB().collection("Buildings").whereEqualTo("name", buildingName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                    Building building = (Building) ds.toObject(Building.class);
                    List<Long> students = new ArrayList<>();
                    if (((List<Long>) ds.get("students")) == null) {
                        Log.d("BUILDING", "NULL " + ds.get("students"));
                    }
                    else {
                        for (Long uscID: (List<Long>) ds.get("students")) {
                            Log.d("BUILDING", "ITERATED");
                            students.add(uscID);
                        }
                    }
                    building.setStudents_ids(students);
                    Log.d("BUILDING", building.toString());

                    //callback
                    FirebaseTest.checkOut(id,sa,time,success);
                }

                // callback
                else{
                    success.setValue(false);
                }
            }
        });
    }

    public static void getAllBuildings(ProgressBar circle) {
        FirestoreConnector.getDB().collection("Buildings").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    List<Building> buildings = new ArrayList<>();
                    for (QueryDocumentSnapshot qds: task.getResult()) {
                        buildings.add((Building) qds.toObject(Building.class));
                        List<Long> students = new ArrayList<>();
                        if (((List<Long>) qds.get("students")) == null) {
                            Log.d("BUILDING", "NULL " + qds.get("students"));
                        }
                        else {
                            for (Long uscID: (List<Long>) qds.get("students")) {
                                Log.d("BUILDING", "ITERATED");
                                students.add(uscID);
                            }
                        }

                        buildings.get(buildings.size() -  1).setStudents_ids(students);
                        Log.d("BUILDING", buildings.get(buildings.size() -  1).toString());
                    }

                    // callback function
                }
            }
        });
    }

    public static void getStudents(Building b, List<Long> studentIDs, EditText buildingparam, ProgressBar circle) {
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        Query query = accounts.whereIn("uscID", studentIDs);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(!task.getResult().isEmpty()) {
                        List<DocumentSnapshot> studentDocuments = task.getResult().getDocuments();
                        List<StudentAccount> students = new ArrayList<>();
                        for (DocumentSnapshot ds: studentDocuments) {
                            StudentAccount studentAccount = ds.toObject(StudentAccount.class);
                            studentAccount.setUscID((Long) ds.get("uscID"));
                            students.add(studentAccount);
                        }

                        // call callback function
                       b.setAccounts(students,buildingparam,circle);
                    }

                }
            }
        });
    }




    // Arjun: overloaded func to allow for sync call to create account with not pic
    public static void checkEmailExists(String email,Account acc,MutableLiveData<Boolean> create_success) {
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        Query query = accounts.whereEqualTo("email", email);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(!task.getResult().isEmpty()) {
                        Log.d("EXIST", "Email " + email + " exists!");

                        // call callback function
                        create_success.setValue(false);
                    }

                    else {
                        Log.d("EXIST", "Email " + email + " does not exist!");

                        // call callback function

                        FirebaseTest.createAccount(acc,create_success);
                    }
                }
            }
        });
    }

    // Arjun: overloaded func to allow for sync call to create account with pic
    public static void checkEmailExists(String email, MutableLiveData<Boolean> success, Account acc, InputStream stream,String Extension) {
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        Query query = accounts.whereEqualTo("email", email);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(!task.getResult().isEmpty()) {
                        Log.d("EXIST", "Email " + email + " exists!");

                        // call callback function

                        success.setValue(false);
                    }

                    else {
                        Log.d("EXIST", "Email " + email + " does not exist!");

                        // call callback function
//                        CreateAccount.setEmailAccepted(true,circle);

                        FirebaseTest.createAccount(acc,success,stream,Extension);
                    }
                }
            }
        });
    }



    public static void authenticate(String email, String password, MutableLiveData<Boolean> login_success) {

        FirestoreConnector.getDB().collection("Accounts")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(!task.getResult().isEmpty()) {
                        Log.d("AUTHENTICATE", email + " Auth successful!");
                        Long uscID = (Long) task.getResult().getDocuments().get(0).get("uscID");
                        Log.d("ID", String.valueOf(uscID));
                        String hashedPW = (String) task.getResult().getDocuments().get(0).get("password");

                        // call callback function
                        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashedPW);
                        if(result.verified){
                            HassibTest.uscid=uscID;
                            login_success.setValue(true);
                        }
                        else{
                            login_success.setValue(false);
                        }

                    }

                    else {
                        Log.d("AUTHENTICATE", email + " Auth failed!");

                        // call callback function
                        login_success.setValue(false);
                    }
                }
            }
        });
    }

    // Update Capacity
    public static void updateCapacity(String buildingName, int newCapacity, ProgressBar circle) {
        FirestoreConnector.getDB().collection("Buildings")
                .whereEqualTo("name", buildingName)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && !task.getResult().isEmpty()) {
                    for (QueryDocumentSnapshot qds: task.getResult()) {
                        FirestoreConnector.getDB().collection("Buildings")
                                .document(qds.getId())
                                .update("capacity", newCapacity)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("UPDATE", buildingName + " updated capacity " + newCapacity);
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    public static void updateCapacities(HashMap<String, Integer> newCapacities) {

    }

//Create account with no pic
public static void createAccount(Account a,MutableLiveData<Boolean> create_success) {
    FirestoreConnector.getDB().collection("Accounts").add(a).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
        @Override
        public void onComplete(@NonNull Task<DocumentReference> task) {
            if (task.isSuccessful()) {
                Log.d("TEST", "Account Added to DB");
                Log.d("Create",a.getFirstName());
                Log.d("Create",a.getLastName());
                Log.d("Create",a.getPassword());
                Log.d("Create",a.getEmail());
                Log.d("Create",a.getIsManager().toString());

                create_success.setValue(true);
            }
            else{
                Log.d("Err", "failed to set up");

                create_success.setValue(false);
            }
        }
    });
}
    //Arjun : create account with pic
    public static void createAccount(Account a,MutableLiveData<Boolean> success, InputStream stream,String Extension) {
        FirestoreConnector.getDB().collection("Accounts").add(a).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Log.d("TEST", "Account Added to DB");
                    uploadPhoto up= new uploadPhoto();
                    up.upload(stream,a.getEmail(),Extension);
                    Log.d("Create",a.getFirstName());
                    Log.d("Create",a.getLastName());
                    Log.d("Create",a.getPassword());
                    Log.d("Create",a.getEmail());
                    Log.d("Create",a.getIsManager().toString());
                    Log.d("Create",a.getProfilePicture());

                    success.setValue(true);
                }
                else{

                    success.setValue(false);
                }
            }
        });
    }



    //updated by Arjun. Added progress bar and snackbar params since call back needs it
    public static void deleteAccount(String email, MutableLiveData<Boolean> delete_success) {
        FirestoreConnector.getDB().collection("Accounts").whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot qds: task.getResult()) {
                        FirestoreConnector.getDB().collection("Accounts").document(qds.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("TEST", "Deleted Account");
                                    //callback added by Arjun

                                    delete_success.setValue(true);

                                }
                                else{
                                    //callback added by Arjun

                                    delete_success.setValue(false);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    //Angad Sood
    public static void updatePassword(String email, String newPassword) {
        FirestoreConnector.getDB().collection("Accounts").whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && !task.getResult().isEmpty()){
                    for(QueryDocumentSnapshot qds: task.getResult()){
                        FirestoreConnector.getDB().collection("Accounts").document(qds.getId()).update("password", newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d("UPDATE","Updated Password");
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    //Update major
    //updated params and added callback
    public static void updateMajor(long uscID, String newMajor,MutableLiveData<Boolean> success ) {
        FirestoreConnector.getDB().collection("Accounts").whereEqualTo("uscID", uscID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot qds : task.getResult()) {
                                FirestoreConnector.getDB().collection("Accounts")
                                        .document(qds.getId())
                                        .update("major", newMajor)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("UPDATE", "Updated Major");
                                                    //Arjun added callback

                                                    success.setValue(true);
                                                }
                                                else{

                                                    success.setValue(false);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });


    }

    //Search
    public static void search(Long uscID){};

}

