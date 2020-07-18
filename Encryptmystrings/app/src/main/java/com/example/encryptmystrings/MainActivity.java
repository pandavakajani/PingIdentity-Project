package com.example.encryptmystrings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.work.WorkManager;
import com.example.encryptmystrings.firebase.FirebaseMessagingHelper;
import com.example.encryptmystrings.ui.main.MainFragment;
import com.example.encryptmystrings.ui.main.MainModelView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {
    private MainModelView modelView;

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
                        // save new Instance ID token
                        String token = task.getResult().getToken();
                        SharedPreferences sharedPref = getSharedPreferences(modelView.PREFS, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.fcm_token), token);
                        editor.commit();

                        Log.d("TAG", token);
                    }
                });
    }

    /**
     * The method is checking for messages from pending intent.
     * If the app is opened from push than we try to get the parameters and start decryption process.
     * I had to add the {@link Intent#FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY} check to separate the cases in which
     * the app is initialized from recent app and therefor carries a history intent which led to incorrect behaviours.
     */
    @Override
    protected void onResume() {
        super.onResume();
        //check if the app is launched from recent apps
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0)
        {
            Log.d("MainActivity: ", "********from fresh *************");
            //checking if intent was provided to show the decrypted string
            if(getIntent().hasExtra(FirebaseMessagingHelper.key_encrypted)){
                String encrypted = getIntent().getStringExtra(FirebaseMessagingHelper.key_encrypted);
                String biometricStatus = getIntent().getStringExtra(FirebaseMessagingHelper.key_should_use_biometric);
                String signature = getIntent().getStringExtra(FirebaseMessagingHelper.key_signature);
                //validating params
                if(encrypted!=null && signature != null){
                    //go directly to the second fragment and show the decrypted string there
                    //this line was added to support killing of the app by the user which deletes the data
                    modelView.updateToggleBiometric(biometricStatus.equals(FirebaseMessagingHelper.USE_BIOMETRIC_TRUE) ? true : false);
                    NavController navController = Navigation.findNavController(this,R.id.myNavHostFragment);
                    modelView.navigateToDecryptedFragment(navController, encrypted, signature);
                    //clear intent for the next call
                    getIntent().removeExtra(FirebaseMessagingHelper.key_encrypted);
                    getIntent().removeExtra(FirebaseMessagingHelper.key_should_use_biometric);
                }
            }
        }
    }


    /**
     * In this method i check if we have a true flag for pending push task.
     * If we do i am sending it using the {@link WorkManager} and unset the flag to false.
     * The task is already built to wait 15 seconds before it is running.
     */
    @Override
    protected void onStop() {
        super.onStop();

        if(modelView.getRegisterPushMessage().getValue()){
            //create a workManager to operate on the background and set it's delay to 15 seconds
            WorkManager workManager = WorkManager.getInstance(this);
            workManager.enqueue(modelView.getTask());
            modelView.updateRegistrationToPush(false);
        }
    }

    private void initViewModel() {
        modelView = new ViewModelProvider(this).get(MainModelView.class);
        modelView.init();
    }

}
