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

public class FirebaseTest extends AppCompatActivity {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_test);
    }

    // FOR TESTING PURPOSES
    public void test(View v) {
        checkUSCIdExists((long) 42, null); // true
        checkUSCIdExists(9876543210L, null); // true
        checkUSCIdExists((long) 0, null); // false

        authenticate("Sapra@usc.edu", "lollz", null); // true
        authenticate("Vk17@usc.edu", "badPassword", null); // false
        authenticate("Madman@usc.edu", "$2a$12$t4deXBfmcv7JNCSxZ3RGOe2rgqUCRZUibSxzRjN5N.ZCbwYZozvO2", null); // true

    }

    // CheckInOut
    public static void checkIn (Long uscID, StudentActivity sa) {
        CollectionReference accounts = db.collection("Accounts");
        Query studentQuery = accounts.whereEqualTo("uscID", uscID);
        studentQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document: task.getResult()) {
                            accounts.document(document.getId()).update("activity", FieldValue.arrayUnion(sa));
                        }

                        CollectionReference buildings = db.collection("Buildings");
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
    public static void getStudents(Building b, List<Long> studentIDs, EditText buildingparam, ProgressBar circle) {
        CollectionReference accounts = db.collection("Accounts");
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
        CollectionReference accounts = db.collection("Accounts");
        Query query = accounts.whereEqualTo("email", email);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(!task.getResult().isEmpty()) {
                        Log.d("EXIST", "Email " + email + " exists!");

                        // call callback function
                        CreateAccount.setEmailAccepted(false);
                    }

                    else {
                        Log.d("EXIST", "Email " + email + " does not exist!");

                        // call callback function
                        CreateAccount.setEmailAccepted(true);
                    }
                }
            }
        });
    }

    //TODO Nishant
    public static void checkUSCIdExists(Long uscID, ProgressBar circle) {
        db.collection("Accounts")
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

    //TODO Nishant
    public static void authenticate(String email, String password, ProgressBar circle) {
        db.collection("Accounts")
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
    public static void updateCapacity(String buildingName, int newCapacity) {

    }

    public static void updateCapacities(HashMap<String, Integer> newCapacities) {

    }

    public static void createAccount(Account a) {
        db.collection("Accounts").add(a).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
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
        db.collection("Accounts").whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot qds: task.getResult()) {
                        db.collection("Accounts").document(qds.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
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