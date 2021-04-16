package com.example.app.firebaseDB;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import org.jetbrains.annotations.NotNull;

public class FCM extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NotNull String token) {
        Log.d("TOKEN", "Refreshed token\n" + token);
    }
}
