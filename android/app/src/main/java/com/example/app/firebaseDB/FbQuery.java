package com.example.app.firebaseDB;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.app.building.Building;
import com.example.app.pre_login_UI.LogInPage;
import com.example.app.users.Account;
import com.example.app.users.StudentAccount;
import com.example.app.users.StudentActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class FbQuery implements FirestoreConnector {
    //Refactored
    public static void checkUSCidExists(Long uscID, MutableLiveData<Boolean> success) {
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        Query query = accounts.whereEqualTo("uscID", uscID);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        Log.d("EXIST", "USC ID " + uscID + " exists!");

                        // call callback function
                        success.setValue(false);

                    } else {
                        Log.d("EXIST", "USC ID " + uscID + " does not exist!");

                        success.setValue(true);
                    }
                }
            }
        });
    }


    public static void getAllBuildings(MutableLiveData<List<Building>> buildingsMLD) {
        FirestoreConnector.getDB().collection("Buildings").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    List<Building> buildings = new ArrayList<>();
                    for (QueryDocumentSnapshot qds : task.getResult()) {

                        buildings.add((Building) qds.toObject(Building.class));
                        List<Long> students = new ArrayList<>();
                        if (((List<Long>) qds.get("students")) == null) {
                            Log.d("BUILDING", "NULL " + qds.get("students"));
                        } else {
                            for (Long uscID : (List<Long>) qds.get("students")) {
                                Log.d("BUILDING", "ITERATED");
                                students.add(uscID);
                            }
                        }

                        buildings.get(buildings.size() - 1).setStudents_ids(students);
                        Log.d("BUILDING", buildings.get(buildings.size() - 1).toString());
                        buildingsMLD.setValue(buildings);
                    }

// callback function
                }
            }
        });
    }
    // DataRetriever
    public static void getBuilding(String buildingName, MutableLiveData<Building> buildingMLD) {
        FirestoreConnector.getDB().collection("Buildings").whereEqualTo("name", buildingName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                    Building building = (Building) ds.toObject(Building.class);
                    List<Long> students = new ArrayList<>();
                    if (((List<Long>) ds.get("students")) == null) {
                        Log.d("BUILDING", "NULL " + ds.get("students"));
                    } else {
                        for (Long uscID : (List<Long>) ds.get("students")) {
                            Log.d("BUILDING", "ITERATED");
                            students.add(uscID);
                        }
                    }
                    building.setStudents_ids(students);
                    Log.d("BUILDING", building.toString());
                    // callback
                    buildingMLD.setValue(building);
                }


            }
        });
    }


    public static void getBuilding(String buildingName, MutableLiveData<Boolean> success, Long id, StudentActivity sa, Date time) {
        FirestoreConnector.getDB().collection("Buildings").whereEqualTo("name", buildingName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                    Building building = (Building) ds.toObject(Building.class);
                    List<Long> students = new ArrayList<>();
                    if (((List<Long>) ds.get("students")) == null) {
                        Log.d("BUILDING", "NULL " + ds.get("students"));
                    } else {
                        for (Long uscID : (List<Long>) ds.get("students")) {
                            Log.d("BUILDING", "ITERATED");
                            students.add(uscID);
                        }
                    }
                    building.setStudents_ids(students);
                    Log.d("BUILDING", building.toString());

                    //callback
                    FbCheckInOut.checkOut(id, sa, time, success);
                }

                // callback
                else {
                    success.setValue(false);
                }
            }
        });
    }

    public static void getStudents(Building b, List<Long> studentIDs,MutableLiveData<List<StudentAccount>> students)  {
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        Query query = accounts.whereIn("uscID", studentIDs);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        List<DocumentSnapshot> studentDocuments = task.getResult().getDocuments();
                        List<StudentAccount> students = new ArrayList<>();
                        for (DocumentSnapshot ds : studentDocuments) {
                            StudentAccount studentAccount = ds.toObject(StudentAccount.class);
                            studentAccount.setUscID((Long) ds.get("uscID"));
                            students.add(studentAccount);
                        }

                        // call callback function
                    }

                }
            }
        });
    }
    // Refactored
    public static void checkEmailExists(String email, MutableLiveData<Boolean> success) {
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        Query query = accounts.whereEqualTo("email", email);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        Log.d("EXIST", "Email " + email + " exists!");

                        // call callback function
                        success.setValue(false);
                    } else {
                        Log.d("EXIST", "Email " + email + " does not exist!");
                        success.setValue(true);

                    }
                }
            }
        });
    }

    public static void authenticate(String email, String password, MutableLiveData<Boolean> login_success) {

        FirestoreConnector.getDB().collection("Accounts")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                Log.d("AUTHENTICATE", email + " Auth successful!");
                                Long uscID = (Long) task.getResult().getDocuments().get(0).get("uscID");
                                Log.d("ID", String.valueOf(uscID));
                                String hashedPW = (String) task.getResult().getDocuments().get(0).get("password");

                                // call callback function
                                BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashedPW);
                                if (result.verified) {
                                    if(uscID==null){
                                        uscID=0L;
                                    }
                                    LogInPage.setId(uscID);
                                    login_success.setValue(true);

                                } else {
                                    login_success.setValue(false);
                                }

                            } else {
                                Log.d("AUTHENTICATE", email + " Auth failed!");

                                // call callback function
                                login_success.setValue(false);
                            }
                        }
                    }
                });
    }


    //Search for account using uscID
    public static void search(Long uscID,MutableLiveData<StudentAccount> student) {
        FirestoreConnector.getDB().collection("Accounts").whereEqualTo("uscID", uscID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                            StudentAccount account = (StudentAccount) ds.toObject(StudentAccount.class);
                            account.setUscID((Long) ds.get("uscID"));
                            Log.d("ACCOUNT", account.toString());
                            student.setValue(account);
                        }
                        //Account not found
                        else if (task.getResult().isEmpty()) {
                            Log.d("ACCOUNT", "NOT FOUND");
                        }
                    }
                });
    }


    //search for account using email
    public static void search(String email,MutableLiveData<Account> manager) {
        FirestoreConnector.getDB().collection("Accounts").whereEqualTo("email", email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                            Account account = (Account) ds.toObject(Account.class);
                            Log.d("ACCOUNT", account.toString());
                            //if needed check out AngadTest class for implementation details
                            manager.setValue(account);
                        }
                        //Account not found
                        else if (task.getResult().isEmpty()) {
                            Log.d("ACCOUNT", "NOT FOUND");

                        }

                    }
                });
    }
}
