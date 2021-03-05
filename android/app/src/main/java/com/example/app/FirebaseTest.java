package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
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
//        EditText textInput = findViewById(R.id.textInput);
//        String text = textInput.getText().toString();

        StudentActivity sa = new StudentActivity("Leventhal School of Accounting", LocalDateTime.now());
        checkIn(42, sa);
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
                    }

                    else {
                        ((TextView) findViewById(R.id.textBox)).setText("Email does not exist!");
                        Log.d("EXIST", "Email does not exist");
                    }
                }
            }
        });
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

    }

    public void deleteAccount(String email) {

    }

    public void updatePassword(String email, String newPassword) {

    }

    public void updateMajor(int uscID, String newMajor) {

    }
}