package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_test);
    }

    // FOR TESTING PURPOSES
    public void test(View v) {
//        StudentAccount a = new StudentAccount();
//        a.setMajor("BBA");
//        a.setUscID((long) 100);
//        a.setProfilePicture("somePic.jpg");
//        a.setPassword("somePass");
//        createAccount(a);

        deleteAccount("someEmail@usc.edu");
    }

    // CheckInOut
    public void checkIn (int uscID, StudentActivity sa) {
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

    public void checkOut(int uscID, String buildingName, LocalDateTime checkOutTime) {

    }

    // DataRetriever
    public List<StudentAccount> getStudents(List<Integer> studentIDs) {
        CollectionReference accounts = db.collection("Accounts");
        Query query = accounts.whereIn("uscID", studentIDs);
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
                            if (sa.getActivity() != null) {
                                Log.d("TEST", sa.getActivity().get(0).getBuildingName());
                                Log.d("TEST", sa.getActivity().get(0).getCheckInTime().toString());
                            }
                        }

                        // call callback function
                    }

                }
            }
        });

        // DO NOT USE THIS VALUE
        // IMPLEMENT A CALLBACK FUNCTION
        return null;
    }

    // WORKS
    public Boolean checkEmailExists(String email) {
        Boolean exists = false;
        CollectionReference accounts = db.collection("Accounts");
        Query query = accounts.whereEqualTo("email", email);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(!task.getResult().isEmpty()) {
                        ((TextView) findViewById(R.id.textBox)).setText("Email exists!");
                        Log.d("EXIST", "Email exists!");

                        // call callback function with param true
                        CreateAccount.setAccepted(false);
                    }

                    else {
                        ((TextView) findViewById(R.id.textBox)).setText("Email does not exist!");
                        Log.d("EXIST", "Email does not exist");

                        // call callback function with param false
                        CreateAccount.setAccepted(true);
                    }
                }
            }
        });
        // DO NOT USE THIS VALUE
        // IMPLEMENT A CALLBACK FUNCTION
        return exists;
    }

    public Boolean checkUSCIdExists(Integer uscID) {
        return false;
    }

    public Boolean authenticate(String email, String password) {
        return false;
    }

    // Update
    public void updateCapacity(String buildingName, int newCapacity) {

    }

    public void updateCapacities(HashMap<String, Integer> newCapacities) {

    }

    public void createAccount(Account a) {
        db.collection("Accounts").add(a).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Log.d("TEST", "Account Added to DB");
                }
            }
        });
    }

    public void deleteAccount(String email) {
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

    public void updatePassword(String email, String newPassword) {

    }

    public void updateMajor(int uscID, String newMajor) {

    }
}