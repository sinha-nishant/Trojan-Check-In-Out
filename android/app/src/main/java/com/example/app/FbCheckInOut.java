package com.example.app;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FbCheckInOut implements FirestoreConnector{
    // Arjun: Updated paramaters to allow for callback
    public static void checkIn(Long uscID, StudentActivity sa, MutableLiveData<Boolean> success) {
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
                                        updates.put("students_ids", FieldValue.arrayUnion(uscID));
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            buildings.document(document.getId())
                                                    .update(updates);
                                        }

                                        // call callback function
                                        success.setValue(true);

                                    }
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

    public static void checkOut(Long uscID, StudentActivity sa, Date checkOutTime, MutableLiveData<Boolean> success) {
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
                                                            updates.put("students_ids", FieldValue.arrayRemove(uscID));
                                                            building.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Log.d("CHECKOUT", "checked out successfully!");

                                                                        //Arjun added callback
                                                                        success.setValue(true);
                                                                    } else {
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


    //Arjun added another checkout function to deal with student account delete
    public static void checkOut(Long uscID, StudentActivity sa, Date checkOutTime, MutableLiveData<Integer> success, String email,Boolean isDelete) {
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
                                                            updates.put("students_ids", FieldValue.arrayRemove(uscID));
                                                            building.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Log.d("CHECKOUT", "checked out successfully!");

                                                                        //Arjun added callback
                                                                        if(isDelete){
                                                                            FbUpdate.deleteAccount(email, success);
                                                                        }
                                                                        else{
                                                                            success.setValue(2);
                                                                        }

                                                                    } else {
                                                                        //Arjun added callback
                                                                        success.setValue(0);
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

}
