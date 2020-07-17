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
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ\
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Map<String,String> data = remoteMessage.getData();

        String body = data.get(FirebaseMessagingHelper.key_body);
        String title = data.get(FirebaseMessagingHelper.key_title);
        String encrypted = data.get(FirebaseMessagingHelper.key_encrypted);
        String signature = data.get(FirebaseMessagingHelper.key_signature);
        String useBiometric = data.get(FirebaseMessagingHelper.key_should_use_biometric);

        //verify the response payload
        if(isNotEmpty(encrypted) && isNotEmpty(title) && isNotEmpty(body) && isNotEmpty(useBiometric)){
            notifyApp(title, body, encrypted, useBiometric, signature);
        }
    }

    private boolean isNotEmpty(String str){
        return str!=null && !str.isEmpty();
    }

    /**
     * Notify the app about the new push message and add a notification with data
     */
    private void notifyApp(String title, String body, String encrypted, String useBiometric, String signature){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.common_full_open_on_phone)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(createPendingIntent(encrypted, useBiometric, signature))
                .setAutoCancel(true);
        createNotificationChannel();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notification_id++, builder.build());
    }

    private PendingIntent createPendingIntent(String encrypted, String useBiometric, String signature){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(FirebaseMessagingHelper.key_encrypted, encrypted);
        intent.putExtra(FirebaseMessagingHelper.key_should_use_biometric, useBiometric);
        intent.putExtra(FirebaseMessagingHelper.key_signature, signature);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


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

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

}
