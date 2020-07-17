package com.example.encryptmystrings.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;

import com.example.encryptmystrings.R;
import com.example.encryptmystrings.encryption.EncryptionManager;
import com.example.encryptmystrings.firebase.FirebaseMessagingHelper;
import com.example.encryptmystrings.firebase.FirebaseWorker;

import java.util.concurrent.TimeUnit;

public class MainModelView extends ViewModel {
    private static final long DELAY = 15;
    public static final String PREFS = "modelviewPrefs";

    //declaration of members
    private LiveData<Boolean> toggleEncryption;
    private LiveData<String> operationStatus;
    private LiveData<String> inputText;
    private LiveData<String> textView;
    private LiveData<Boolean> registerPushMessage;

    private MainModel model;
    private OneTimeWorkRequest task;
    private EncryptionManager encryptionManager;

    //init our members
    public void init(){
        //init model if needed
        if(model == null){
            model = MainModel.getInstance();
        }
        //init the live data if not done already
        if(toggleEncryption == null){
            toggleEncryption = model.getToggleEncryption();
        }

        if(operationStatus == null){
            operationStatus = model.getOperationStatus();
        }

        if(inputText == null){
            inputText = model.getInputText();
        }

        if(textView == null){
            textView = model.getTextView();
        }

        if(registerPushMessage == null){
            registerPushMessage = model.getRegisterPush();
        }

        if(encryptionManager == null){
            encryptionManager = new EncryptionManager();
        }
    }

    /* the liveDate getter methods return LiveData in order to prevent writing
       to the live data from outside */

    public LiveData<Boolean> getToggleEncryption() {
        return toggleEncryption;
    }

    public LiveData<String> getOperationStatus() {
        return operationStatus;
    }

    public LiveData<String> getInputText() {
        return inputText;
    }

    public LiveData<String> getTextView(){
        return textView;
    }

    public LiveData<Boolean> getRegisterPushMessage() {
        return registerPushMessage;
    }

    public void updateToggleBiometric(boolean useBiometric){
        model.setToggleEncryption(useBiometric);
    }

    public void updateInputText(String inputText){
        model.setInputText(inputText);
    }

    public void updateTextView(String text){
        model.setTextView(text);
    }

    public void updateRegistrationToPush(boolean registerToPush){
        model.setRegisterPushMessage(registerToPush);
    }

    public OneTimeWorkRequest getTask() {
        return task;
    }

    // bind method was done in the layout (main_fragment)
    public void onEncryptionToggled(){
        boolean isOn = toggleEncryption.getValue();
        model.setOperationStatus("Toggle button is now turned " + (isOn==false? "ON" : "OFF"));
        model.setToggleEncryption(!toggleEncryption.getValue());
    }

    public void createTask(String token, String body, String title){
        // Passing params
        Data.Builder data = new Data.Builder();
        data.putString(FirebaseWorker.DECRYPTED_STRING, inputText.getValue());
        data.putString(FirebaseWorker.REGISTRATION_TOKEN, token);
        data.putString(FirebaseWorker.MESSAGE_TITLE, title);
        data.putString(FirebaseWorker.MESSAGE_BODY, body);
        data.putString(FirebaseWorker.BIOMETRIC, toggleEncryption.getValue() ? FirebaseMessagingHelper.USE_BIOMETRIC_TRUE : FirebaseMessagingHelper.USE_BIOMETRIC_FALSE);

        task = new OneTimeWorkRequest.Builder(FirebaseWorker.class)
                .setInputData(data.build())
                .setInitialDelay(DELAY, TimeUnit.SECONDS)
                .build();

    }

    // bind method was done in the layout (main_fragment)
    public void onButtonSendClicked(View v){
        //1. encrypt
//        NavController navController = Navigation.findNavController(v);
//        navigateToDecryptedFragment(navController);
        //register to push when app goes to background
        SharedPreferences prefs = v.getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if(prefs==null){
            return;
        }

        String encrypted = encryptionManager.encryptString(inputText.getValue());
//        String signature = encryptionManager.sign(inputText.getValue());
//
//        String decrypetd = encryptionManager.decryptString(encrypted);
//        boolean isVerified = encryptionManager.verify(signature, decrypetd);



        String token = prefs.getString(v.getContext().getString(R.string.fcm_token),"");
        String title = v.getContext().getString(R.string.message_title);
        String body = v.getContext().getString(R.string.message_body);
        createTask(token, body, title);
        updateRegistrationToPush(true);
    }

    public void navigateToDecryptedFragment(NavController navController){
        if(navController!=null){
            MainFragmentDirections.ActionMainFragmentToDecryptedFragment action = MainFragmentDirections.actionMainFragmentToDecryptedFragment().setDecryptedText(getInputText().getValue());
            navController.navigate(action);
        }
    }
}
