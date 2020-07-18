package com.example.encryptmystrings.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.encryptmystrings.MainActivity;
import com.example.encryptmystrings.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Service for handling incoming messages from firebase
 */
public class FirebaseMessagingServiceImpl extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingServiceImpl";
    private static final String CHANNEL_ID = "PING_FIREBASE_CHANEL";
    private static int notification_id = 0;

    public FirebaseMessagingServiceImpl() {
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ\
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Map<String,String> data = remoteMessage.getData();

        //extract message params
        String body = data.get(FirebaseMessagingHelper.key_body);
        String title = data.get(FirebaseMessagingHelper.key_title);
        String encrypted = data.get(FirebaseMessagingHelper.key_encrypted);
        String signature = data.get(FirebaseMessagingHelper.key_signature);
        String useBiometric = data.get(FirebaseMessagingHelper.key_should_use_biometric);

        //verify the response payload and send notification if needed
        if(isNotEmpty(encrypted) && isNotEmpty(title) && isNotEmpty(body) && isNotEmpty(useBiometric)){
            notifyApp(title, body, encrypted, useBiometric, signature);
        }
    }

    private boolean isNotEmpty(String str){
        return str!=null && !str.isEmpty();
    }

    /**
     * Notify the app about the new push message and add a notification with data
     * @param title title of notification
     * @param body body of notification
     * @param encrypted encrypted text
     * @param useBiometric should use biometric when opening the push notification
     * @param signature signature of the original text
     */
    private void notifyApp(String title, String body, String encrypted, String useBiometric, String signature){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.common_full_open_on_phone)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(createPendingIntent(encrypted, useBiometric, signature))
                .setAutoCancel(true);
        //notification channel opening is a must in new API's
        createNotificationChannel();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notification_id++, builder.build());
    }

    /**
     * Create the pending intent for opening the notification.
     * It holds the data the the app needs for the logic.
     * @param encrypted encrypted text
     * @param encrypted encrypted text
     * @param useBiometric should use biometric when opening the push notification
     * @return PendingIntent for the activity
     */
    private PendingIntent createPendingIntent(String encrypted, String useBiometric, String signature){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(FirebaseMessagingHelper.key_encrypted, encrypted);
        intent.putExtra(FirebaseMessagingHelper.key_should_use_biometric, useBiometric);
        intent.putExtra(FirebaseMessagingHelper.key_signature, signature);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    /**
     * Opening a notification channel in order to show the notification
     */
    private void createNotificationChannel() {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
    }
}
