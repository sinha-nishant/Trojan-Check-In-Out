package com.example.app.firebaseDB;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.app.R;
import com.example.app.account_UI.StudentProfile;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

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

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("FCM", "title: " + remoteMessage.getNotification().getTitle());
        Log.d("FCM", "body: " + remoteMessage.getNotification().getBody());
        String channelId = "Default";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody());

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
        manager.createNotificationChannel(channel);
        manager.notify(0, builder.build());

        Intent intent = new Intent(this, StudentProfile.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
