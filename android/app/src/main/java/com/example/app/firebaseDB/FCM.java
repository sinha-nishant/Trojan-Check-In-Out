package com.example.app.firebaseDB;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FCM extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NotNull String token) {
        Log.d("TOKEN", "Refreshed token\n" + token);
    }

    // Notifies device with given token that the user was kicked out
    public static void notifyKickOut(String token) {

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);

        FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
        mFunctions.getHttpsCallable("addMessage").call(data);
    }
}
