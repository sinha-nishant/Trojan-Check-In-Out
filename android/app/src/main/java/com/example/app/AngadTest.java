package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AngadTest extends AppCompatActivity implements FirestoreConnector {
    String firstName;
    String lastName;
    String email;
    String password;
    String profilePicture;
    boolean isManager;
    long uscID;


    EditText passwordInput;
    EditText uscIDInput;
    public TextView layItOut;

    Button submitButton;




    //Angad Sood
    public static void updatePassword(String email, String newPassword) {
        FirestoreConnector.getDB().collection("Accounts").whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot qds : task.getResult()) {
                                FirestoreConnector.getDB().collection("Accounts")
                                        .document(qds.getId())
                                        .update("password", newPassword)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("UPDATE", "Updated Password");
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    //Update major
    public static void updateMajor(long uscID, String newMajor) {
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
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });


    }

    public void search(Long uscID) {
        FirestoreConnector.getDB().collection("Accounts").whereEqualTo("uscID", uscID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                            StudentAccount account = (StudentAccount) ds.toObject(StudentAccount.class);
                            account.setUscID((Long) ds.get("uscID"));
                            Log.d("ACCOUNT", account.toString());
                            String buildingNames="";
                            for(StudentActivity sd:account.getActivity()){
                                buildingNames+=sd.getBuildingName()+"\n";
                            }
                            buildingNames+="Profile Pic: (will show if exists)"+"\n";
                            buildingNames+=account.getProfilePicture();
                            layItOut.setText(buildingNames);
                            //if needed check out AngadTest class for implementation details
                        }
                        //Account not found
                        else if (task.getResult().isEmpty()) {
                            Log.d("ACCOUNT", "NOT FOUND");
                        }

                    }
                });
    }


    public static void checkIn(Long uscID,StudentActivity sa) {
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        Query studentQuery = accounts.whereEqualTo("uscID", uscID);
        studentQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
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
                                        updates.put("occupancy", FieldValue.increment(1));
                                        updates.put("students", FieldValue.arrayUnion(uscID));
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            buildings.document(document.getId())
                                                    .update(updates);
                                        }

                                        // call callback function
                                        //Arjun added callback
//


                                    }
                                } else {
                                    //Arjun added callback
//

                                }
                            }
                        });
                    }
                }
            }
        });
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_angad_test);

        passwordInput = (EditText) findViewById(R.id.passwordInput);
        uscIDInput = (EditText) findViewById(R.id.uscIDInput);
        layItOut = (TextView) findViewById(R.id.TextMulti);

        submitButton = (Button) findViewById(R.id.changeMajor);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password = passwordInput.getText().toString();
                uscID = Long.parseLong(uscIDInput.getText().toString());
               // checkIn(uscID,new StudentActivity("Sood's building",new Date(),null));
                search(uscID);


            }
        });
    }
}