package com.example.app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class FirebaseTest extends AppCompatActivity implements FirestoreConnector {

    public TextView textBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_test);
        textBox = findViewById(R.id.textBox);
    }

    // FOR TESTING PURPOSES
    public void test(View v) {
//       getBuildingsRealtime();
    }

    public void getBuildingsRealtime(MutableLiveData<Building> buildingMLD) {
        FirestoreConnector.getDB().collection("Buildings").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("Building Update", "realtime failed");
                } else {
                    if (value != null && !value.isEmpty()) {
                        Log.d("Building Update", "received update");
                        DocumentChange dc = value.getDocumentChanges().get(0);
                        String name = (String) dc.getDocument().get("name");
                        String occupancy = String.valueOf(dc.getDocument().get("occupancy"));
                        String capacity = String.valueOf(dc.getDocument().get("capacity"));
                        textBox.setText(String.format("%s: %s, %s", name, occupancy,capacity));
//                        buildingMLD.setValue(building);
                    } else {
                        Log.d("Building Update", "no realtime changes");
                    }
                }
            }
        });
    }

    public static void checkUSCidExists(Long uscID, Account acc, MutableLiveData<Integer> success) {
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        Query query = accounts.whereEqualTo("uscID", uscID);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        Log.d("EXIST", "USC ID " + uscID + " exists!");

                        // call callback function
                        success.setValue(2);

                    } else {
                        Log.d("EXIST", "USC ID " + uscID + " does not exist!");

                        // call callback function
                        FirebaseTest.createAccount(acc, success);
                    }
                }
            }
        });
    }


    public static void checkUSCidExists(Long uscID, Account acc, MutableLiveData<Integer> success,InputStream stream) {
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        Query query = accounts.whereEqualTo("uscID", uscID);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        Log.d("EXIST", "USC ID " + uscID + " exists!");

                        // call callback function
                        success.setValue(2);

                    } else {
                        Log.d("EXIST", "USC ID " + uscID + " does not exist!");

                        // call callback function
                        FirebaseTest.createAccount(acc, success,stream);
                    }
                }
            }
        });
    }
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
                                        updates.put("students", FieldValue.arrayUnion(uscID));
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
                                                            updates.put("students", FieldValue.arrayRemove(uscID));
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
                                                            updates.put("students", FieldValue.arrayRemove(uscID));
                                                            building.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Log.d("CHECKOUT", "checked out successfully!");

                                                                        //Arjun added callback
                                                                        if(isDelete){
                                                                            FirebaseTest.deleteAccount(email, success);
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
                    FirebaseTest.checkOut(id, sa, time, success);
                }

                // callback
                else {
                    success.setValue(false);
                }
            }
        });
    }

    public static void getAllBuildings(ProgressBar circle) {
        FirestoreConnector.getDB().collection("Buildings").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    }

                    // callback function
                }
            }
        });
    }

    public static void getStudents(Building b, List<Long> studentIDs, EditText buildingparam, ProgressBar circle) {
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
                        b.setAccounts(students, buildingparam, circle);
                    }

                }
            }
        });
    }


    // Arjun: overloaded func to allow for sync call to create account with not pic
    public static void checkEmailExists(String email, Account acc, MutableLiveData<Integer> create_success,Boolean isManager) {
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        Query query = accounts.whereEqualTo("email", email);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        Log.d("EXIST", "Email " + email + " exists!");

                        // call callback function
                        create_success.setValue(0);
                    } else {
                        Log.d("EXIST", "Email " + email + " does not exist!");

                        // call callback function
//                        FirebaseTest.createAccount(acc, create_success);
                        if(isManager){
                            FirebaseTest.createAccount(acc, create_success);
                        }
                        else{
                            Long id= ((StudentAccount)acc).getUscID();
                            FirebaseTest.checkUSCidExists(id,acc,create_success);
                        }

                    }
                }
            }
        });
    }

    // Arjun: overloaded func to allow for sync call to create account with pic
    public static void checkEmailExists(String email, MutableLiveData<Integer> success, Account acc, InputStream stream,Boolean isManager) {
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        Query query = accounts.whereEqualTo("email", email);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        Log.d("EXIST", "Email " + email + " exists!");

                        // call callback function
                        success.setValue(0);
                    } else {
                        Log.d("EXIST", "Email " + email + " does not exist!");

                        // call callback function
                        if(isManager){
                            FirebaseTest.createAccount(acc, success, stream);
                        }
                        else{
                            Long id= ((StudentAccount)acc).getUscID();
                            FirebaseTest.checkUSCidExists(id,acc,success,stream);
                        }
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
                    if(a.getIsManager()==false){
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
                    if(a.getIsManager()==false){
                        Log.d("CREATE", a.toString());
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
    public void search(String email) {
        FirestoreConnector.getDB().collection("Accounts").whereEqualTo("email", email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                            StudentAccount account = (StudentAccount) ds.toObject(StudentAccount.class);
                            account.setUscID((Long) ds.get("uscID"));
                            Log.d("ACCOUNT", account.toString());
                            //if needed check out AngadTest class for implementation details
                        }
                        //Account not found
                        else if (task.getResult().isEmpty()) {
                            Log.d("ACCOUNT", "NOT FOUND");
                        }

                    }
                });
    }
}
