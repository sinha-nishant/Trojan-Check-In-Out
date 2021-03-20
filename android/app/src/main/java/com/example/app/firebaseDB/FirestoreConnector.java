package com.example.app.firebaseDB;

import com.google.firebase.firestore.FirebaseFirestore;

public interface FirestoreConnector {
     static FirebaseFirestore getDB() {
        return FirebaseFirestore.getInstance();
    }
}
