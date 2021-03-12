package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


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
                            Account account = (Account) ds.toObject(Account.class);
                            Log.d("ACCOUNT", account.toString());
                            layItOut.setText(account.email);
                        }
                        //Account not found
                        else if (task.getResult().isEmpty()) {
                            Log.d("ACCOUNT", "NOT FOUND");

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
                search(uscID);

            }
        });
    }
}