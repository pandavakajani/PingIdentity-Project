package com.example.encryptmystrings.firebase;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.encryptmystrings.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseMessagingHelper {
    private static final String TAG = "FirebaseMessagingHelper";
    private static final String serverKey = "AAAAFQejK3Y:APA91bFfWZFSaeTiFUlLrJ4Q-9TBCdQFbf8f4Xi-EuHN2UtuYgwoFlrfSnGPE5Rq0GVD71-aJpNy_TgY7AzGXxLZ5bkjC6xp-Tt9Bdvvjmj0dzyaP-tkKiPNCStKwjttCtbozKajCyPk";
    private static final String path = "https://fcm.googleapis.com/fcm/send";
    private static final String contentType = "application/json";
    private static final String decryptionTopic = "topics/decryption_topic";

    public static JSONObject generateDecryptionNotification(String title, String body, String encryptedData){
        JSONObject message = new JSONObject();
        JSONObject messageBody = new JSONObject();


        try {
            messageBody.put("title", title);
            messageBody.put("message", body);
            messageBody.put("encrypted", encryptedData);
            message.put("topic", decryptionTopic);
            message.put("data", messageBody);

        } catch (JSONException e) {
            message=null;
            Log.e(TAG, "generateNotification: failed to construct json message");
        }
        return message;
    }

    public static void sendNotification(String title, String body, String encryptedData){
        JSONObject message = generateDecryptionNotification(title, body, encryptedData);

        if(message!=null){
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, path, message, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //
                }
            }, );
        }
    }

    public static void subscribeToEncryptionTopic(final Context ctx){
        FirebaseMessaging.getInstance().subscribeToTopic("weather")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = ctx.getString(R.string.topic_registration_success);
                        if (!task.isSuccessful()) {
                            msg = ctx.getString(R.string.topic_registration_failure);
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
