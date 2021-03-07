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

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_test);
    }

    // FOR TESTING PURPOSES
    public void test(View v) {
        StudentAccount a = new StudentAccount();
        a.setMajor("BBA");
        a.setUscID((long) 100);
        a.setProfilePicture("somePic.jpg");
        a.setPassword("somePass");
        createAccount(a);

//        FirebaseTest.deleteAccount("someEmail@usc.edu");
    }

    // CheckInOut
    public static void checkIn (int uscID, StudentActivity sa) {
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

    public static void checkOut(int uscID, String buildingName, LocalDateTime checkOutTime) {

    }

    // DataRetriever
    public static List<StudentAccount> getStudents(Building b, List<Long> studentIDs) {
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

                        b.setAccounts(students);
                        Integer sizee = students.size();
                        Log.d("Length of students", sizee.toString());


                    }

                }
            }
        });

        // DO NOT USE THIS VALUE
        // IMPLEMENT A CALLBACK FUNCTION
        return null;
    }

    // WORKS
    public static Boolean checkEmailExists(String email) {
        Boolean exists = false;
        CollectionReference accounts = db.collection("Accounts");
        Query query = accounts.whereEqualTo("email", email);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(!task.getResult().isEmpty()) {
//                        ((TextView) findViewById(R.id.textBox)).setText("Email exists!");
                        Log.d("EXIST", "Email exists!");

                        // call callback function with param true
                        CreateAccount.setEmailAccepted(false);
                    }

                    else {
//                        ((TextView) findViewById(R.id.textBox)).setText("Email does not exist!");
                        Log.d("EXIST", "Email does not exist");

                        // call callback function with param false
                        CreateAccount.setEmailAccepted(true);
                    }
                }
            }
        });
        // DO NOT USE THIS VALUE
        // IMPLEMENT A CALLBACK FUNCTION
        return exists;
    }

    //Nishant
    public static Boolean checkUSCIdExists(Integer uscID) {
        return false;
    }

    //Nishant
    public static Boolean authenticate(String email, String password) {
        return false;
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