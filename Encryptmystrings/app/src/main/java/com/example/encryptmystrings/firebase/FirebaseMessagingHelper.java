package com.example.encryptmystrings.firebase;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.encryptmystrings.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FirebaseMessagingHelper {
    private static final String TAG = "FirebaseMessagingHelper";
    private static final String serverKey = "AAAAFQejK3Y:APA91bFfWZFSaeTiFUlLrJ4Q-9TBCdQFbf8f4Xi-EuHN2UtuYgwoFlrfSnGPE5Rq0GVD71-aJpNy_TgY7AzGXxLZ5bkjC6xp-Tt9Bdvvjmj0dzyaP-tkKiPNCStKwjttCtbozKajCyPk";
    private static final String path = "https://fcm.googleapis.com/fcm/send";
    private static final String contentType = "application/json";
    private static final String decryptionTopic = "topics/decryption_topic";

    public static JSONObject generateDecryptionNotification(String title, String body, String encryptedData){
        JSONObject message = new JSONObject();
        JSONObject notification = new JSONObject();
        JSONObject data = new JSONObject();

        try {
            notification.put("title", title);
            notification.put("body", body);
            data.put("encrypted", encryptedData);
            message.put("to", decryptionTopic);
            message.put("notification", notification);
            message.put("data", data);
        } catch (JSONException e) {
            message=null;
            Log.e(TAG, "generateNotification: failed to construct json message");
        }
        return message;
    }

    public static void sendNotification(String title, String body, String encryptedData, Context ctx){
        JSONObject message = generateDecryptionNotification(title, body, encryptedData);
        RequestQueue queue = Volley.newRequestQueue(ctx);

        if(message!=null){
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    path,
                    message,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "message sent");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "message not sent");
                        }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError{
                    Map<String, String> header = new HashMap<>();
                    header.put("Content-Type", contentType);
                    header.put("authorization", "key=" + serverKey);
                    return header;
                }
            };
            queue.add(request);
        }
    }

    public static void subscribeToEncryptionTopic(final Context ctx){
        FirebaseMessaging.getInstance().subscribeToTopic(decryptionTopic)
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
