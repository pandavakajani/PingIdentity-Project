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
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.example.encryptmystrings.firebase.FirebaseMessagingHelper;
import com.example.encryptmystrings.firebase.FirebaseWorker;
import com.example.encryptmystrings.ui.main.MainFragment;
import com.example.encryptmystrings.ui.main.MainModelView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import java.util.concurrent.TimeUnit;

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

    @Override
    protected void onResume() {
        super.onResume();
        //checking if intent was provided to show the decrypted string
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0)
        {
            Log.d("MainActivity: ", "********from fresh *************");
            if(getIntent().hasExtra(FirebaseMessagingHelper.key_encrypted)){
                String decrypted = getIntent().getStringExtra(FirebaseMessagingHelper.key_encrypted);
                String biometricStatus = getIntent().getStringExtra(FirebaseMessagingHelper.key_should_use_biometric);
                if(decrypted!=null){
                    //go directly to the second fragment and show the decrypted string there
                    modelView.updateInputText(decrypted);
                    //this line was added to support killing of the app by the user which deletes the data
                    modelView.updateToggleBiometric(biometricStatus.equals(FirebaseMessagingHelper.USE_BIOMETRIC_TRUE) ? true : false);
                    NavController navController = Navigation.findNavController(this,R.id.myNavHostFragment);
                    modelView.navigateToDecryptedFragment(navController);
                    //clear intent for the next call
                    getIntent().removeExtra(FirebaseMessagingHelper.key_encrypted);
                    getIntent().removeExtra(FirebaseMessagingHelper.key_should_use_biometric);
                }
            }
        }
    }

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

    public MainModelView getModelView() {
        return modelView;
    }

}
