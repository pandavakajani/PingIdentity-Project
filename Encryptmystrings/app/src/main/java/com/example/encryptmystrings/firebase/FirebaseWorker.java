package com.example.encryptmystrings.firebase;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Simple worker class that is in charge of passing params to the notification message and
 * sending it using FirebaseMessagingHelper.
 * It will run from the background.
 */
public class FirebaseWorker extends Worker {
    public static final String REGISTRATION_TOKEN = "registration_token";
    public static final String DECRYPTED_STRING = "decrypted_queue";
    public static final String MESSAGE_TITLE = "message_title";
    public static final String MESSAGE_BODY = "message_body";
    public static final String BIOMETRIC = "biometric";
    public static final String SIGNATURE = "signature";

    public FirebaseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     * Extracts params and passing it to FirebaseMessagingHelper
     * @return status of the work
     */
    @NonNull
    @Override
    public Result doWork() {
        Log.d("FirebaseWorker", "doWork for Sync");
        String decryptedString = getInputData().getString(DECRYPTED_STRING);
        String registrationToken = getInputData().getString(REGISTRATION_TOKEN);
        String body = getInputData().getString(MESSAGE_BODY);
        String title = getInputData().getString(MESSAGE_TITLE);
        String useBiometric = getInputData().getString(BIOMETRIC);
        String signature = getInputData().getString(SIGNATURE);
        FirebaseMessagingHelper.sendNotification(title, body, decryptedString, useBiometric, signature, registrationToken, getApplicationContext());
        return Worker.Result.success();
    }
}
