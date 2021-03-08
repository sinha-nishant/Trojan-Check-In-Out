package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseTest extends AppCompatActivity implements FirestoreConnector {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_test);
    }

    // FOR TESTING PURPOSES
    public void test(View v) {
        getBuilding("Fluor Tower", null);
        getAllBuildings(null);
    }

    // CheckInOut
    public static void checkIn (Long uscID, StudentActivity sa) {
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
                                        for (QueryDocumentSnapshot document: task.getResult()) {
                                            buildings.document(document.getId()).update("students", FieldValue.arrayUnion(uscID));
                                        }

                                        // call callback function
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public static void checkOut(Long uscID, String buildingName, LocalDateTime checkOutTime) {

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
                    building.setStudents(students);
                    Log.d("BUILDING", building.toString());
                }

                // callback
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

                        buildings.get(buildings.size() -  1).setStudents(students);
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
        Log.d("Inside firebase", "hello");

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(!task.getResult().isEmpty()) {
                        List<DocumentSnapshot> studentDocuments = task.getResult().getDocuments();
                        List<StudentAccount> students = new ArrayList<StudentAccount>();
                        for (DocumentSnapshot ds: studentDocuments) {
                            StudentAccount studentAccount = ds.toObject(StudentAccount.class);
                            studentAccount.setUscID((Long) ds.get("uscID"));
                            students.add(studentAccount);
                        }

                        for (StudentAccount sa: students) {
                            Log.d("TEST", sa.toString());
                            if (sa.getActivity() != null && sa.getActivity().size()!=0) {
                                Log.d("TEST", sa.getActivity().get(0).getBuildingName());
                                Log.d("TEST", sa.getActivity().get(0).getCheckInTime().toString());
                            }
                        }

                        // call callback function
                       b.setAccounts(students,buildingparam,circle);
//                        Integer sizee = students.size();
//                        buildingparam.setText(students.toString());
//                        Log.d("Length of students", sizee.toString());
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

    public static void authenticate(String email, String password, ProgressBar circle) {
        FirestoreConnector.getDB().collection("Accounts")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(!task.getResult().isEmpty()) {
                        Log.d("AUTHENTICATE", email + " Auth successful!");

                        // call callback function
                    }

                    else {
                        Log.d("AUTHENTICATE", email + " Auth failed!");

                        // call callback function
                    }
                }
            }
        });
    }

    // Update
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

    public static void createAccount(Account a) {
        FirestoreConnector.getDB().collection("Accounts").add(a).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Log.d("TEST", "Account Added to DB");
                    CreateAccount.setCreateAccountAccepted(true);
                }
                else{
                    CreateAccount.setCreateAccountAccepted(false);
                }
            }
        });
    }

    public static void deleteAccount(String email) {
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

    }

    //Angad Sood
    public static void updateMajor(int uscID, String newMajor) {

    }
}