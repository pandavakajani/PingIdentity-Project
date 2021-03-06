package com.example.encryptmystrings.firebase;

import android.content.Context;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is in charge of sending messages to firebase.
 * It knows to create messages with given params and to sent it to firebase.
 */
public class FirebaseMessagingHelper {
    private static final String TAG = "FirebaseMessagingHelper";
    private static final String serverKey = "key=AAAAFQejK3Y:APA91bFfWZFSaeTiFUlLrJ4Q-9TBCdQFbf8f4Xi-EuHN2UtuYgwoFlrfSnGPE5Rq0GVD71-aJpNy_TgY7AzGXxLZ5bkjC6xp-Tt9Bdvvjmj0dzyaP-tkKiPNCStKwjttCtbozKajCyPk";
    private static final String path = "https://fcm.googleapis.com/fcm/send";
    private static final String contentType = "application/json";
    private static final String HeaderContentType = "Content-Type";
    private static final String HeaderAuthorization = "Authorization";

    public static final String key_signature = "signature";
    public static final String key_priority = "priority";
    public static final String key_data = "data";
    public static final String key_to = "to";
    public static final String key_encrypted = "encrypted";
    public static final String key_should_use_biometric = "biometric";
    public static final String key_body = "body";
    public static final String key_title = "title";
    public static final String PRIORITY_NORMAL = "normal";
    public static final String USE_BIOMETRIC_TRUE = "true";
    public static final String USE_BIOMETRIC_FALSE = "false";


    /**
     * Creating a json object to be sent to firebase
     * @param title notification title
     * @param body notification body
     * @param encryptedData encrypted data param
     * @param useBiometric should use biometric when opening notification
     * @param signature signature of the plain text
     * @param registrationKey client registration key to firebase
     * @return notification as json object
     */
    public static JSONObject generateDecryptionNotification(String title, String body, String encryptedData, String useBiometric, String signature, String registrationKey){
        JSONObject message = new JSONObject();
        JSONObject data = new JSONObject();

        try {
            data.put(key_title, title);
            data.put(key_body, body);
            data.put(key_encrypted, encryptedData);
            data.put(key_should_use_biometric, useBiometric);
            data.put(key_signature, signature);
            message.put(key_to, registrationKey);
            message.put(key_data, data);
            message.put(key_priority, PRIORITY_NORMAL);
        } catch (JSONException e) {
            message=null;
            Log.e(TAG, "generateNotification: failed to construct json message");
        }
        return message;
    }

    /**
     * Sending message to firebase
     * @param title notification title
     * @param body notification body
     * @param encryptedData encrypted data param
     * @param useBiometric should use biometric when opening notification
     * @param signature signature of the plain text
     * @param registrationKey client registration key to firebase
     * @param ctx context
     */
    public static void sendNotification(String title, String body, String encryptedData, String useBiometric, String signature, String registrationKey, Context ctx){
        JSONObject message = generateDecryptionNotification(title, body, encryptedData, useBiometric, signature, registrationKey);
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
                    header.put(HeaderContentType, contentType);
                    header.put(HeaderAuthorization, serverKey);
                    return header;
                }
            };
            queue.add(request);
        }
    }
}
