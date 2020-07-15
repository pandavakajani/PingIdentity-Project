package com.example.encryptmystrings;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.encryptmystrings.firebase.FirebaseMessagingHelper;
import com.example.encryptmystrings.firebase.FirebaseWorker;
import com.example.encryptmystrings.ui.main.MainFragment;
import com.example.encryptmystrings.ui.main.MainModelView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final long DELAY = 15;
    private MainModelView modelView;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.main_activity);
        initViewModel();
        getSupportFragmentManager().beginTransaction().add(R.id.myNavHostFragment, new MainFragment()).commit();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("TAG", token);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        FirebaseMessagingHelper.subscribeToEncryptionTopic(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Passing params
        Data.Builder data = new Data.Builder();
        data.putString(FirebaseWorker.DECRYPTED_STRING, modelView.getInputText().getValue());

        //create a workManager to operate on the background and set it's delay to 15 seconds
        WorkManager workManager = WorkManager.getInstance(this);
        workManager.enqueue(new OneTimeWorkRequest.Builder(FirebaseWorker.class)
                .setInputData(data.build())
                .setInitialDelay(DELAY, TimeUnit.SECONDS)
                .build());
    }

    private void initViewModel() {
        modelView = new ViewModelProvider(this).get(MainModelView.class);
        modelView.init();
    }

    public MainModelView getModelView() {
        return modelView;
    }

}
