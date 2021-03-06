package com.example.app.firebaseDB;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.amazonaws.cognito.clientcontext.datacollection.BuildDataCollector;
import com.example.app.building.Building;
import com.example.app.log_create.CreateAccount;
import com.example.app.log_create.uploadPhoto;
import com.example.app.pre_login_UI.RestorePage;
import com.example.app.users.Account;
import com.example.app.users.StudentAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class FbUpdate implements FirestoreConnector {
    /**
     * Batch add buildings
     *
     * @param buildings hashmap from building name and capacities
     * @param success   indicates whether addition occurred successfully
     */
    public static void addBuildings(HashMap<String, Integer> buildings, MutableLiveData<Boolean> success) {
        //building variables
        Integer occupancy = 0;
        List<Long> students_ids = null;//list of uscId
        String qrCodeURL = null;


        CollectionReference buildingsFB = FirestoreConnector.getDB().collection("Buildings");
        WriteBatch batch = FirestoreConnector.getDB().batch();

        //new code
        for (Map.Entry mapElement : buildings.entrySet()) {
            String key = (String) mapElement.getKey();
            //key and value set here, rest set above
            String name = key;
            Integer capacity = (Integer) mapElement.getValue();
            DocumentReference buildingRef = buildingsFB.document();
            Building building = new Building(name, capacity, occupancy, qrCodeURL, students_ids);
            batch.set(buildingRef, building);

        }

        // Execute batch capacity updates
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    success.setValue(true);
                } else {
                    Log.d("ADD BUILDINGS", String.valueOf(task.getException()));
                    success.setValue(false);
                }
            }
        });
    }

    public static void addBuildingName(String buildingName, MutableLiveData<Boolean> success) {
        String name = buildingName;
        Integer capacity = 0;
        Integer occupancy = 0;
        List<Long> students_ids = null;//list of uscId
        String qrCodeURL = null;
        Building building = new Building(name, capacity, occupancy, qrCodeURL, students_ids);
        FirestoreConnector.getDB().collection("Buildings")
                .add(building).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    success.setValue(true);
                    Log.d("ADD", building.getName() + " added");
                } else {
                    success.setValue(false);
                }
            }
        });
    }

    public static void addBuilding(Building building, MutableLiveData<Boolean> success) {
        FirestoreConnector.getDB().collection("Buildings")
                .add(building).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    success.setValue(true);
                    Log.d("ADD", building.getName() + " added");
                } else {
                    success.setValue(false);
                }
            }
        });
    }

    /**
     * Batch delete buildings
     *
     * @param buildings list of building names
     * @param success   indicates whether addition occurred successfully
     */
    public static void deleteBuildings(List<String> buildings, MutableLiveData<Boolean> success){
        // get all buildings in the list
        FirestoreConnector.getDB().collection("Buildings").whereIn("name", buildings)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    WriteBatch batch = FirestoreConnector.getDB().batch();
                    // Add delete of building reference to batch
                    for (QueryDocumentSnapshot qds : task.getResult()) {
                        batch.delete(qds.getReference());
                    }
                    // Execute batch deletes
                    batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                success.setValue(true);
                            } else {
                                Log.d("DELETE", String.valueOf(task.getException()));
                                success.setValue(false);
                            }
                        }
                    });
                } else {
                    Log.d("DELETE", String.valueOf(task.getException()));
                    success.setValue(false);
                }
            }
        });

    }


    /**
     * Delete single building
     *
     * @param buildingName  building name
     * @param success   indicates whether addition occurred successfully
     */
    public static void deleteBuilding(String buildingName, MutableLiveData<Boolean> success){
        FirestoreConnector.getDB().collection("Buildings").
                whereEqualTo("name", buildingName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && !task.getResult().isEmpty()){
                    for (QueryDocumentSnapshot qds:task.getResult()){
                        FirestoreConnector.getDB().collection("Buildings")
                                .document(qds.getId())
                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    success.setValue(true);
                                    Log.d("DELETE", buildingName + " deleted");
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


    // Update Capacity
    public static void updateCapacity(String buildingName, MutableLiveData<Boolean> success, Integer newCapacity) {
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
                                            success.setValue(true);
                                            Log.d("UPDATE", buildingName + " updated capacity " + newCapacity);
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

    /**
     * Batch update capacities
     *
     * @param buildings hashmap from building name to new capacities
     * @param success   indicates whether update occurred successfully
     */
    public static void updateCapacities(HashMap<String, Integer> buildings, MutableLiveData<Boolean> success) {
        // get all buildings in hashmap
        FirestoreConnector.getDB().collection("Buildings").whereIn("name", new ArrayList<>(buildings.keySet()))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    WriteBatch batch = FirestoreConnector.getDB().batch();
                    // Add capacity update to batch
                    for (QueryDocumentSnapshot qds : task.getResult()) {
                        batch.update(qds.getReference(), "capacity", buildings.get(qds.get("name")));
                    }
                    // Execute batch capacity updates
                    batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                success.setValue(true);
                            } else {
                                Log.d("UPDATE CAPACITIES", String.valueOf(task.getException()));
                                success.setValue(false);
                            }
                        }
                    });
                } else {
                    Log.d("UPDATE CAPACITIES", String.valueOf(task.getException()));
                    success.setValue(false);
                }
            }
        });

    }

    /**
     * Create Account
     *
     * @param a              account to be  created without image
     * @param create_success boolean representing whether the account was created successfully-
     *                       returns following values:
     *                       true: account created without error
     *                       false: error
     */
    public static void createAccount(Account a, MutableLiveData<Boolean> create_success) {
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        String email = a.getEmail();
        //add account to DB
        accounts.add(a).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    //search for the account just created
                    accounts.whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            //create a field "isDeleted" and set it to true
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                                FirestoreConnector.getDB().collection("Accounts").document(ds.getId()).update("isActive", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("CREATE", "Account Added to DB");

                                        if (a.getIsManager()) {
                                            Log.d("CREATE", a.toString());
                                        } else {
                                            Log.d("CREATE", ((StudentAccount) a).toString());
                                        }
                                        create_success.setValue(true);
                                    }
                                });

                            } else {
                                Log.d("Err", "failure while adding isDeleted");
                                create_success.setValue(false);
                            }
                        }
                    });
                } else {
                    Log.d("Err", "failed to set up");
                    create_success.setValue(false);
                }
            }
        });
    }


    /**
     * Create Account
     *
     * @param a              account to be  created with image
     * @param create_success boolean representing whether the account was created successfully-
     *                       returns following values:
     *                       0: account created without error
     *                       1: account already existed in a time remnant (must be restored)
     *                       2: error while execution
     */
    public static void createAccount(Account a, MutableLiveData<Boolean> create_success, InputStream stream) {
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        String email = a.getEmail();
        //add account to DB
        accounts.add(a).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    //search for the account just created
                    accounts.whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            //create a field "isDeleted" and set it to true
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                                FirestoreConnector.getDB().collection("Accounts").document(ds.getId()).update("isActive", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("CREATE", "Account Added to DB");

                                        if (a.getIsManager()) {
                                            Log.d("CREATE", a.toString());
                                        } else {
                                            Log.d("CREATE", ((StudentAccount) a).toString());
                                        }
//                                            create_success.setValue(true);
                                        uploadPhoto.upload(stream, a.getEmail(), create_success);
                                    }
                                });

                            } else {
                                Log.d("Err", "failure while adding isDeleted");
                                create_success.setValue(false);
                            }
                        }
                    });
                } else {
                    Log.d("Err", "failed to set up");
                    create_success.setValue(false);
                }
            }
        });
    }

//    public static void createAccount(Account a, MutableLiveData<Boolean> create_success){
//        //delete later
//    }

    /**
     * Delete account
     *
     * @param email          email
     * @param delete_success indicates whether deletion occurred successfully: 1 if not deleted, 2 if deleted
     */
    public static void deleteAccount(String email, MutableLiveData<Integer> delete_success) {
        CollectionReference accounts = FirestoreConnector.getDB().collection("Accounts");
        accounts.whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot qds : task.getResult()) {
                        //update the value of the field isDeleted to true to indicate account deletion
                        FirestoreConnector.getDB().collection("Accounts").document(qds.getId()).update("isActive", false).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("TEST", "Deleted Account");
                                    delete_success.setValue(2);
                                } else {

                                    Log.d("TEST", "Did not Delete Account");
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
                    DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                    FirestoreConnector.getDB().collection("Accounts").document(ds.getId()).update("password", newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
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

        });
    }

    // Update major
    // Updated params and added callback
    public static void updateMajor(long uscID, String newMajor, MutableLiveData<Boolean> success) {
        FirestoreConnector.getDB().collection("Accounts").whereEqualTo("uscID", uscID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                   task.getResult().getDocuments().get(0).getReference().update("major", newMajor).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Void> task) {
                                                           if (task.isSuccessful()) {
                                                               Log.d("UPDATE", "Updated Major");

                                                               success.setValue(true);
                                                           } else {
                                                               success.setValue(false);
                                                           }
                                                       }
                                                   });
                                               }
                                           }
                                       }
                );
    }

    // Updated params and added callback
    public static void updatePhoto(String email, MutableLiveData<Boolean> success) {
        FirestoreConnector.getDB().collection("Accounts").whereEqualTo("email", email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            task.getResult().getDocuments().get(0).getReference().update("profilePicture", CreateAccount.AWSLink(email)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("UPDATE", "Updated Photo");

                                        success.setValue(true);
                                    } else {
                                        success.setValue(false);
                                    }
                                }
                            });
                        }
                    }

                });
    }

    /**
     * Restore deleted account
     *
     * @param email    email of user
     * @param password plain text user password
     * @param restored boolean to indicate whether account successfully found and restored
     */
    public static void restore(String email, String password, MutableLiveData<Boolean> restored) {
        FirestoreConnector.getDB().collection("Accounts")
                .whereEqualTo("email", email)
                .whereEqualTo("isActive", false)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    String hashedPW = String.valueOf(task.getResult().getDocuments().get(0).get("password"));

                    BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashedPW);
                    //arjun added
                    final Long uscID = (Long) task.getResult().getDocuments().get(0).get("uscID");
                    //ended arjun

                    // If email and password combination is valid
                    if (result.verified) {
                        task.getResult().getDocuments().get(0).getReference().update("isActive", true)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // arjun added
                                        if (uscID == null) {
                                            RestorePage.setId(0L);
                                        } else {
                                            RestorePage.setId(uscID);
                                        }
                                        //ended arjun
                                        restored.setValue(task.isSuccessful());
                                    }
                                });

                    } else {
                        Log.d("RESTORE ACCOUNT", "Invalid credentials");
                        restored.setValue(false);
                    }
                } else {
                    Log.d("RESTORE ACCOUNT", String.valueOf(task.getException()));
                    restored.setValue(false);
                }
            }
        });
    }
}
