package com.example.encryptmystrings.firebase;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.RequestQueue;

import java.util.HashMap;

public class FirebaseWorker extends Worker {
    public static final String WORKER_QUEUE = "worker_queue";
    public static final String DECRYPTED_STRING = "decrypted_queue";

    public FirebaseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("FirebaseWorker", "doWork for Sync");
        String decryptedString = getInputData().getString(DECRYPTED_STRING);
        FirebaseMessagingHelper.sendNotification("Ping message", "Your text was encrypted", decryptedString, getApplicationContext());
        return Worker.Result.success();
    }
}
