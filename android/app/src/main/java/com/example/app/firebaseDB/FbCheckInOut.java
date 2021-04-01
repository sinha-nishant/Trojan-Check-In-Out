package com.example.app.firebaseDB;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.app.users.StudentActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FbCheckInOut implements FirestoreConnector {
    public static void checkIn(Long uscID, StudentActivity sa, MutableLiveData<Boolean> success) {
        Query studentQuery = FirestoreConnector.getDB().collection("Accounts").whereEqualTo("uscID", uscID);
        studentQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    FirebaseFirestore db = FirestoreConnector.getDB();
                    WriteBatch batch = db.batch();
                    batch.update(task.getResult().getDocuments().get(0).getReference(), "activity", FieldValue.arrayUnion(sa));
                    // Convert object to hashmap so can add 'uscID' field as foreign key
                    HashMap<String, Object> studentActivity = new HashMap<>();
                    studentActivity.put("buildingName", sa.getBuildingName());
                    studentActivity.put("checkInTime", sa.getCheckInTime());
                    studentActivity.put("checkOutTime", sa.getCheckOutTime());
                    studentActivity.put("uscID", uscID);
                    batch.set(db.collection("Activities").document(), studentActivity);

                    Query buildingQuery = FirestoreConnector.getDB().collection("Buildings").whereEqualTo("name", sa.getBuildingName());
                    buildingQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("occupancy", FieldValue.increment(1));
                                updates.put("students_ids", FieldValue.arrayUnion(uscID));
                                batch.update(task.getResult().getDocuments().get(0).getReference(), updates);
                                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) Log.d("CHECKIN", String.valueOf(task.getException()));
                                            success.setValue(task.isSuccessful());
                                        }
                                    }
                                );
                            } else {
                                Log.d("CHECKIN", String.valueOf(task.getException()));
                                success.setValue(false);
                            }
                        }
                    });
                } else {
                    Log.d("CHECKIN", String.valueOf(task.getException()));
                    success.setValue(false);
                }
            }
        });
    }

    public static void checkOut(Long uscID, StudentActivity sa, Date checkOutTime, MutableLiveData<Boolean> success) {
        FirestoreConnector.getDB().collection("Accounts").whereEqualTo("uscID", uscID).get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    DocumentReference account = task.getResult().getDocuments().get(0).getReference();
                    StudentActivity newSA = new StudentActivity(sa.getBuildingName(), sa.getCheckInTime(), checkOutTime);
                    account.update("activity", FieldValue.arrayRemove(sa),"activity", FieldValue.arrayUnion(newSA))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FirestoreConnector.getDB().collection("Buildings")
                                                .whereEqualTo("name", sa.getBuildingName()).get()
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
                                                                        FirestoreConnector.getDB().collection("Activities")
                                                                                .whereEqualTo("uscID", uscID)
                                                                                .whereEqualTo("checkOutTime", null)
                                                                                .get()
                                                                                .addOnCompleteListener(
                                                                                        new OnCompleteListener<QuerySnapshot>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                                                                    task.getResult().getDocuments().get(0).getReference()
                                                                                                            .update("checkOutTime", checkOutTime)
                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                    if (!task.isSuccessful()) Log.d("CHECKOUT", String.valueOf(task.getException()));
                                                                                                                    success.setValue(task.isSuccessful());
                                                                                                                }
                                                                                                            });
                                                                                                }

                                                                                                else {
                                                                                                    Log.d("CHECKOUT", String.valueOf(task.getException()));
                                                                                                    success.setValue(false);
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                );
                                                                    } else {
                                                                        //Arjun added callback
                                                                        Log.d("CHECKOUT", String.valueOf(task.getException()));
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


}
