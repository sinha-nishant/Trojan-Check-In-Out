package com.example.app;

import com.google.firebase.firestore.FirebaseFirestore;

public interface FirestoreConnector {
    static FirebaseFirestore getDB() {
        return FirebaseFirestore.getInstance();
    }
}
