package com.example.app.firebaseDB;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.app.building.Building;
import com.example.app.pre_login_UI.LogInPage;
import com.example.app.users.Account;
import com.example.app.users.StudentAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class FbQuery implements FirestoreConnector {
    /**
     * Checks whether given USC ID already applies to an active account
     *
     * @param uscID  ID to check existence of
     * @param exists true if ID exists, false if does not exist
     */
    public static void checkUSCidExists(Long uscID, MutableLiveData<Boolean> exists) {
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        Query query = accounts.whereEqualTo("uscID", uscID);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot qds : task.getResult()) {
                            if (!qds.getBoolean("isDeleted")) {
                                Log.d("EXIST", "USC ID " + uscID + " exists!");
                                exists.setValue(true);
                            }
                            else{
                                exists.setValue(false);
                            }


                        }
                    } else {
                        Log.d("EXIST", "USC ID " + uscID + " does not exist!");
                        exists.setValue(false);
                    }
                }
            }
        });
    }

    /**
     * Checks whether given email ID exists among the deleted accounts
     *
     * @param email  email of the student
     * @param exists boolean representing whether the account exists or not:
     *               false if doesn't exist
     *               true if exists
     */
    public static void checkRestore(String email, MutableLiveData<Boolean> exists) {
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        Query query = accounts.whereEqualTo("email", email);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot qds : task.getResult()) {
                            if (qds.getBoolean("isDeleted")) {
                                Log.d("RESTORE", "Account exists");
                                exists.setValue(true);
                            }
                        }
                    } else {
                        Log.d("RESTORE", "Account does not exist");
                        exists.setValue(false);
                    }
                }

            }
        });

    }

    /**
     * Retrieves all buildings from database
     *
     * @param buildingsMLD list of retrieved buildings
     */
    public static void getAllBuildings(MutableLiveData<List<Building>> buildingsMLD) {
        FirestoreConnector.getDB().collection("Buildings").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    List<Building> buildings = new ArrayList<>();
                    for (QueryDocumentSnapshot qds : task.getResult()) {

                        buildings.add(qds.toObject(Building.class));
                        List<Long> students = new ArrayList<>();
                        if (qds.get("students") != null) {
                            students.addAll((List<Long>) Objects.requireNonNull(qds.get("students")));
                        }

                        buildings.get(buildings.size() - 1).setStudents_ids(students);
                    }
                    buildingsMLD.setValue(buildings);

                }
            }
        });
    }

    public static void getAllBuildingsMap(MutableLiveData<HashMap<String, Building>> buildingsMLD) {
        FirestoreConnector.getDB().collection("Buildings").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    HashMap<String, Building> map = new HashMap<String, Building>();
                    List<Building> buildings = new ArrayList<>();
                    for (QueryDocumentSnapshot qds : task.getResult()) {

                        buildings.add(qds.toObject(Building.class));
                        List<Long> students = new ArrayList<>();
                        if (qds.get("students") != null) {
                            students.addAll((List<Long>) Objects.requireNonNull(qds.get("students")));
                        }

                        buildings.get(buildings.size() - 1).setStudents_ids(students);
                    }
                    for (int i = 0; i < buildings.size(); i++) {
                        map.put(buildings.get(i).getName(), buildings.get(i));
                    }
                    buildingsMLD.setValue(map);

                }
            }
        });
    }

    /**
     * Retrieves a building with given name
     *
     * @param buildingName the name of the building to retrieve
     * @param buildingMLD  the variable that will store the Building result
     */
    public static void getBuilding(String buildingName, MutableLiveData<Building> buildingMLD) {
        FirestoreConnector.getDB().collection("Buildings").whereEqualTo("name", buildingName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                    Building building = ds.toObject(Building.class);
                    Log.d("BUILDING", String.valueOf(Objects.requireNonNull(building).getStudents_ids()));
                    buildingMLD.setValue(building);
                }
            }
        });
    }

    public static void getBuildings(List<String> buildingNames, MutableLiveData<List<Building>> buildingsMLD) {
        FirestoreConnector.getDB().collection("Buildings").whereIn("name", buildingNames).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    List<Building> buildings = new ArrayList<>();
                    for (QueryDocumentSnapshot qds : task.getResult()) {
                        buildings.add(qds.toObject(Building.class));
                        List<Long> students = new ArrayList<>();
                        if (qds.get("students") != null) {
                            students.addAll((List<Long>) Objects.requireNonNull(qds.get("students")));
                        }
                        buildings.get(buildings.size() - 1).setStudents_ids(students);
                    }
                    buildingsMLD.setValue(buildings);
                }
            }
        });
    }

    /**
     * Provides the current students in a given building
     *
     * @param building    the building object for which the student accounts are desired
     * @param studentsMLD the accounts of the students in the building
     */
    public static void getCurrentStudents(Building building, MutableLiveData<List<StudentAccount>> studentsMLD) {
        Log.d("CURRENT", String.valueOf(building.getStudents_ids()));
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        Query query = accounts.whereIn("uscID", building.getStudents_ids());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    List<DocumentSnapshot> studentDocuments = task.getResult().getDocuments();
                    List<StudentAccount> students = new ArrayList<>();
                    for (DocumentSnapshot ds : studentDocuments) {
                        StudentAccount studentAccount = ds.toObject(StudentAccount.class);
                        Objects.requireNonNull(studentAccount).setUscID((Long) ds.get("uscID"));
                        students.add(studentAccount);
                    }

                    studentsMLD.setValue(students);
                } else {
                    Log.d("CURRENT", String.valueOf(task.getException()));
                    studentsMLD.setValue(null);
                }
            }
        });
    }

    /**
     * Checks whether given email already in use by active account
     *
     * @param email  email to check whether in use
     * @param exists true if exists, false if doesn't
     */
    public static void checkEmailExists(String email, MutableLiveData<Boolean> exists) {
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        Query query = accounts.whereEqualTo("email", email);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        Log.d("abcde","in success not empty");
                        for (QueryDocumentSnapshot qds : task.getResult()) {
                            if (!qds.getBoolean("isDeleted")) {
                                Log.d("EXIST", "Email " + email + " exists!");
                                exists.setValue(true);
                            }
                            else{
                                exists.setValue(false);
                            }
                        }


                    } else {
                        Log.d("EXIST", "Email " + email + " does not exist!");
                        exists.setValue(false);
                    }
                }
                else{
                    Log.d("abcde","not successful email retrieval");
                }
            }
        });
    }

    /**
     * Validates log-in credentials
     *
     * @param email         user's email
     * @param password      user's plain text password
     * @param login_success true if successfully authenticated, false if invalid credentials
     */
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

                                BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashedPW);
                                if (result.verified) {
                                    if (uscID == null) {
                                        uscID = 0L;
                                    }
                                    LogInPage.setId(uscID);
                                    login_success.setValue(true);

                                } else {
                                    login_success.setValue(false);
                                }

                            } else {
                                Log.d("AUTHENTICATE", email + " Auth failed!");
                                login_success.setValue(false);
                            }
                        }
                    }
                });
    }

    /**
     * Retrieve a Student Account by USC ID
     *
     * @param uscID   USC ID associated with account
     * @param student stores Student Account retrieved
     */
    public static void getStudent(Long uscID, MutableLiveData<StudentAccount> student) {
        FirestoreConnector.getDB().collection("Accounts").whereEqualTo("uscID", uscID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                            StudentAccount account = ds.toObject(StudentAccount.class);
                            Objects.requireNonNull(account).setUscID((Long) ds.get("uscID"));
                            Log.d("STUDENT ACCOUNT", account.toString());
                            student.setValue(account);
                        }
                        //Account not found
                        else if (task.getResult().isEmpty()) {
                            Log.d("STUDENT ACCOUNT", "NOT FOUND");
                            student.setValue(null);
                        }
                    }
                });
    }

    /**
     * Retrieve a Manager account by email
     *
     * @param email   email associated with account
     * @param manager Account object retrieved
     */
    public static void getManager(String email, MutableLiveData<Account> manager) {
        FirestoreConnector.getDB().collection("Accounts").whereEqualTo("email", email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                            Account account = (Account) ds.toObject(Account.class);
                            Log.d("MANAGER ACCOUNT", Objects.requireNonNull(account).toString());
                            manager.setValue(account);
                        }
                        //Account not found
                        else if (task.getResult().isEmpty()) {
                            Log.d("MANAGER ACCOUNT", "NOT FOUND");
                        }
                    }
                });
    }

    /**
     * Merges and orders by last name two lists containing Student Accounts.
     * Used to reduce code duplication in querying from Accounts and DeletedAccounts and
     * make it more clear what operation is occurring.
     *
     * @param accounts1 first list of Student Accounts
     * @param accounts2 second list of Student Accounts
     * @return list of merged and sorted Student Accounts
     */
    private static List<StudentAccount> mergeSearchResults(List<StudentAccount> accounts1, List<StudentAccount> accounts2) {
        accounts1.addAll(accounts2);
        accounts1.sort(Comparator.comparing(StudentAccount::getLastName));
        return accounts1;
    }

    /**
     * Search by major
     *
     * @param major       major of student
     * @param studentsMLD list of students
     */
    public static void search(String major, MutableLiveData<List<StudentAccount>> studentsMLD) {
        FirestoreConnector.getDB().collection("Accounts").whereEqualTo("major", major).orderBy("lastName").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                        if (task1.isSuccessful()) {
                            if (!task1.getResult().isEmpty()) {
                                FirestoreConnector.getDB().collection("DeletedAccounts").whereEqualTo("major", major).orderBy("lastName").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                        if (task2.isSuccessful()) {
                                            if (!task2.getResult().isEmpty()) {
                                                List<StudentAccount> studentsFound = mergeSearchResults(
                                                        task1.getResult().toObjects(StudentAccount.class),
                                                        task2.getResult().toObjects(StudentAccount.class)
                                                );

                                                studentsMLD.setValue(studentsFound);
                                            } else {
                                                studentsMLD.setValue(task1.getResult().toObjects(StudentAccount.class));
                                            }
                                        } else {
                                            Log.d("MAJOR", String.valueOf(task2.getException()));
                                            studentsMLD.setValue(null);
                                        }
                                    }
                                });
                            }

                            // if no students with given major are found
                            else {
                                studentsMLD.setValue(new ArrayList<StudentAccount>());
                            }
                        }

                        // if exception in query
                        else {
                            Log.d("MAJOR", String.valueOf(task1.getException()));
                            studentsMLD.setValue(null);
                        }
                    }
                });
    }
}
