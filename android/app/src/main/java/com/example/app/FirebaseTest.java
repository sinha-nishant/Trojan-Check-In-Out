package com.example.app;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class FirebaseTest extends AppCompatActivity implements FirestoreConnector {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_test);
    }

    // FOR TESTING PURPOSES
    public void test(View v) {
        
    }

    // CheckInOut - TO FIX: DOESN'T UPDATE OCCUPANCY YET
    // Arjun: Updated paramaters to allow for callback
    public static void checkIn (Long uscID, StudentActivity sa,ProgressBar progressbar,AlertDialog alert) {
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
                                        StudentAccount.setCheckInSuccess(true,progressbar,alert);

                                    }
                                }
                                else{
                                    //Arjun added callback
                                    StudentAccount.setCheckInSuccess(false,progressbar,alert);
                                }
                            }
                        });
                    }
                }
            }
        });
    }
    //Arjun updated params and added callback
    public static void checkOut(Long uscID, StudentActivity sa, Date checkOutTime,ProgressBar progressbar,AlertDialog alert) {
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
                                                                StudentAccount.setCheckOutSuccess(true,progressbar,alert);
                                                            }
                                                            else{
                                                                //Arjun added callback
                                                                StudentAccount.setCheckOutSuccess(false,progressbar,alert);
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
    public static void checkOut(Long uscID, StudentActivity sa, Date checkOutTime, ProgressBar progressbar, AlertDialog alert, String email) {
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
//                                                                        StudentAccount.setCheckOutSuccess(true,progressbar,snackbar);
                                                                        FirebaseTest.deleteAccount(email,progressbar,alert);
                                                                    }
                                                                    else{
                                                                        //Arjun added callback
//                                                                        StudentAccount.setCheckOutSuccess(false,progressbar,snackbar);
                                                                        progressbar.setVisibility(View.GONE);
                                                                        progressbar.stopNestedScroll();
                                                                        alert.setMessage("Error. Could not delete account successfully as we could not log you out");
                                                                        alert.show();
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

    //Arjun added new getBuilding with additional params for checkout
    public static void getBuilding(String buildingName, ProgressBar circle,AlertDialog alert,Long id,StudentActivity sa,Date time) {
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
                    FirebaseTest.checkOut(id,sa,time,circle,alert);
                }

                // callback
                else{
                    circle.setVisibility(View.GONE);
                    circle.stopNestedScroll();
                    alert.setMessage("Error. Could not successfully check you out");
                    alert.show();
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

    public static void checkEmailExists(String email, ProgressBar circle) {
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        Query query = accounts.whereEqualTo("email", email);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(!task.getResult().isEmpty()) {
                        Log.d("EXIST", "Email " + email + " exists!");

                        // call callback function
                        CreateAccount.setEmailAccepted(false,circle);
                    }

                    else {
                        Log.d("EXIST", "Email " + email + " does not exist!");

                        // call callback function
                        CreateAccount.setEmailAccepted(true,circle);
                    }
                }
            }
        });
    }
    // Arjun: overloaded func to allow for sync call to create account with not pic
    public static void checkEmailExists(String email, ProgressBar circle,AlertDialog alert,Account acc) {
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        Query query = accounts.whereEqualTo("email", email);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(!task.getResult().isEmpty()) {
                        Log.d("EXIST", "Email " + email + " exists!");

                        // call callback function
//                        CreateAccount.setEmailAccepted(false,circle);
                        circle.setVisibility(View.GONE);
                        circle.stopNestedScroll();
                        alert.setMessage("This Email address is already in use");
                        alert.show();
                    }

                    else {
                        Log.d("EXIST", "Email " + email + " does not exist!");

                        // call callback function
//                        CreateAccount.setEmailAccepted(true,circle);
                        FirebaseTest.createAccount(acc,circle,alert);
                    }
                }
            }
        });
    }

    // Arjun: overloaded func to allow for sync call to create account with pic
    public static void checkEmailExists(String email, ProgressBar circle, AlertDialog alert, Account acc, InputStream stream,String Extension) {
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        Query query = accounts.whereEqualTo("email", email);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(!task.getResult().isEmpty()) {
                        Log.d("EXIST", "Email " + email + " exists!");

                        // call callback function
//                        CreateAccount.setEmailAccepted(false,circle);
                        circle.setVisibility(View.GONE);
                        circle.stopNestedScroll();
                        alert.setMessage("This Email address is already in use");
                        alert.show();
                    }

                    else {
                        Log.d("EXIST", "Email " + email + " does not exist!");

                        // call callback function
//                        CreateAccount.setEmailAccepted(true,circle);
                        FirebaseTest.createAccount(acc,circle,alert,stream,Extension);
                    }
                }
            }
        });
    }

    public static void checkUSCIdExists(Long uscID, ProgressBar circle) {
        FirestoreConnector.getDB().collection("Accounts")
                .whereEqualTo("uscID", uscID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(!task.getResult().isEmpty()) {
                                Log.d("EXIST", "uscID " + uscID + " exists!");

                                // call callback function
                            }

                            else {
                                Log.d("EXIST", "uscID " + uscID + " does not exist!");

                                // call callback function
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
    public static void createAccount(Account a,ProgressBar progressbar, AlertDialog alert) {
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
                    CreateAccount.setCreateAccountAccepted(true, progressbar,alert);
                }
                else{
                    Log.d("Err", "failed to set up");
                    CreateAccount.setCreateAccountAccepted(false,progressbar,alert);
                }
            }
        });
    }
    //Arjun : create account with pic
    public static void createAccount(Account a,ProgressBar progressbar, AlertDialog alert, InputStream stream,String Extension) {
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
                    CreateAccount.setCreateAccountAccepted(true, progressbar,alert);
                }
                else{
                    CreateAccount.setCreateAccountAccepted(false,progressbar,alert);
                }
            }
        });
    }

    //updated by Arjun. Added progress bar and snackbar params since call back needs it
    public static void deleteAccount(String email, ProgressBar progressbar, AlertDialog alert) {
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
                                    Account.setDeleteSuccess(true,progressbar,alert);

                                }
                                else{
                                    //callback added by Arjun
                                    Account.setDeleteSuccess(false,progressbar,alert);
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
    public static void updateMajor(long uscID, String newMajor,ProgressBar progressbar,AlertDialog alert ) {
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
                                                    StudentAccount.setMajorSuccess(true,progressbar,alert);
                                                }
                                                else{
                                                    //Arjun added callback
                                                    StudentAccount.setMajorSuccess(false,progressbar,alert);
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

