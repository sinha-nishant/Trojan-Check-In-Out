package com.example.app;

import android.util.Log;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;
import java.util.HashMap;

public class FbUpdate  implements FirestoreConnector{
    // Update Capacity
    public static void updateCapacity(String buildingName, int newCapacity, ProgressBar circle) {
        FirestoreConnector.getDB().collection("Buildings")
                .whereEqualTo("name", buildingName)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    for (QueryDocumentSnapshot qds : task.getResult()) {
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
    public static void createAccount(Account a, MutableLiveData<Integer> create_success) {
        FirestoreConnector.getDB().collection("Accounts").add(a).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Log.d("CREATE", "Account Added to DB");
//                    Log.d("CREATE", a.toString());
                    if(a.getIsManager()==true){
                        Log.d("CREATE", a.toString());
                    }
                    else{
                        Log.d("CREATE", ((StudentAccount)a).toString());
                    }
                    create_success.setValue(3);

                } else {
                    Log.d("Err", "failed to set up");
                    create_success.setValue(1);
                }
            }
        });
    }

    //Arjun : create account with pic
    public static void createAccount(Account a, MutableLiveData<Integer> success, InputStream stream) {
        FirestoreConnector.getDB().collection("Accounts").add(a).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Log.d("CREATE", "Account Added to DB");

                    uploadPhoto.upload(stream, a.getEmail());
                    if(a.getIsManager()==true){
                        Log.d("CREATE", a.toString());
                        Log.d("CREATE", "Manager acc");
                    }
                    else{
                        Log.d("CREATE", ((StudentAccount)a).toString());
                    }

                    success.setValue(3);

                } else {
                    success.setValue(1);
                }
            }
        });
    }


    //updated by Arjun. Added progress bar and snackbar params since call back needs it
    public static void deleteAccount(String email, MutableLiveData<Integer> delete_success) {
        FirestoreConnector.getDB().collection("Accounts").whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot qds : task.getResult()) {
                        FirestoreConnector.getDB().collection("Accounts").document(qds.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("TEST", "Deleted Account");

                                    //callback added by Arjun
                                    delete_success.setValue(2);

                                } else {

                                    //callback added by Arjun
                                    delete_success.setValue(1);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    //Angad Sood
    public static void updatePassword(String email, String newPassword, MutableLiveData<Boolean> success) {
        FirestoreConnector.getDB().collection("Accounts").whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    for (QueryDocumentSnapshot qds : task.getResult()) {
                        FirestoreConnector.getDB().collection("Accounts").document(qds.getId()).update("password", newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("UPDATE", "Updated Password");
                                    success.setValue(true);
                                } else {
                                    success.setValue(false);
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
    public static void updateMajor(long uscID, String newMajor, MutableLiveData<Boolean> success) {
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
                                                } else {
                                                    success.setValue(false);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    //updated params and added callback
    public static void updatePhoto(String email, MutableLiveData<Boolean> success) {
        FirestoreConnector.getDB().collection("Accounts").whereEqualTo("email", email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot qds : task.getResult()) {
                                FirestoreConnector.getDB().collection("Accounts")
                                        .document(qds.getId())
                                        .update("profilePicture", CreateAccount.AWSLink(email))
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("UPDATE", "Updated Photo");

                                                    //Arjun added callback
                                                    success.setValue(true);
                                                } else {
                                                    success.setValue(false);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

}
