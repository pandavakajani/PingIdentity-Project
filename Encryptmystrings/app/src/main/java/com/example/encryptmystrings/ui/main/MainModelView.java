package com.example.encryptmystrings.ui.main;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;

import com.example.encryptmystrings.R;
import com.example.encryptmystrings.encryption.EncryptionManager;
import com.example.encryptmystrings.firebase.FirebaseMessagingHelper;
import com.example.encryptmystrings.firebase.FirebaseWorker;

import java.util.concurrent.TimeUnit;

import static android.view.View.VISIBLE;

public class MainModelView extends AndroidViewModel implements EncryptionManager.EncryptionManagerListener{
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

    public MainModelView(@NonNull Application application) {
        super(application);
    }

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
            encryptionManager = new EncryptionManager(this);
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

    // bind method was done in the layout (main_fragment)
    public void onEncryptionToggled(){
        boolean isOn = toggleEncryption.getValue();
        model.setOperationStatus("Toggle button is now turned " + (isOn==false? "ON" : "OFF"));
        model.setToggleEncryption(!toggleEncryption.getValue());
    }

    private void resetAllFlags(){
        model.setVerifiedVisibility(false);
        model.setSignedVisibility(false);
        model.setEncryptedVisibility(false);
        model.setDecryptedVisibility(false);
        model.setTimerCreatedCreatedVisibility(false);
        model.setKeyPairCreatedVisibility(false);
        model.setVerifiedVisibility(false);
        model.setVerifiedVisibility(false);
        model.setVerifiedVisibility(false);
    }


    /************* UI ACTIONS *****************/
    // bind method was done in the layout (main_fragment)
    public void onButtonSendClicked(View v){
        resetAllFlags();
        encryptionManager.encryptAndSign(inputText.getValue());
    }

    public int getKeyPairTextViewVisibility(){
        return model.getKeyPairCreatedVisibility().getValue() == false ? View.INVISIBLE : VISIBLE;
    }

    public int getEncryptedTextViewVisibility(){
        return model.getEncryptedVisibility().getValue() == false ? View.INVISIBLE : VISIBLE;
    }

    public int getSignedTextViewVisibility(){
        return model.getSignedVisibility().getValue() == false ? View.INVISIBLE : VISIBLE;
    }

    public int getTimerTextViewVisibility(){
        return model.getTimerCreatedCreatedVisibility().getValue() == false ? View.INVISIBLE : VISIBLE;
    }

    public int getVerifiedTextViewVisibility(){
        return model.getVerifiedVisibility().getValue() == false ? View.INVISIBLE : VISIBLE;
    }

    public int getDecryptedTextViewVisibility(){
        return model.getDecryptedVisibility().getValue() == false ? View.INVISIBLE : VISIBLE;
    }

    /************* NAVIGATION *****************/
    public void navigateToDecryptedFragment(NavController navController, String encryptedText, String signature){
        if(navController!=null){
            MainFragmentDirections.ActionMainFragmentToDecryptedFragment action = MainFragmentDirections.actionMainFragmentToDecryptedFragment()
                    .setDecryptedText(encryptedText)
                    .setSignature(signature);
            navController.navigate(action);
        }
    }


    /************* PUSH MESSAGE *****************/
    public OneTimeWorkRequest getTask() {
        return task;
    }

    public void createTask(String token, String body, String title, String encryptedString, String signature){
        // Passing params
        Data.Builder data = new Data.Builder();
        data.putString(FirebaseWorker.DECRYPTED_STRING, encryptedString);
        data.putString(FirebaseWorker.REGISTRATION_TOKEN, token);
        data.putString(FirebaseWorker.MESSAGE_TITLE, title);
        data.putString(FirebaseWorker.MESSAGE_BODY, body);
        data.putString(FirebaseWorker.SIGNATURE, signature);
        data.putString(FirebaseWorker.BIOMETRIC, toggleEncryption.getValue() ? FirebaseMessagingHelper.USE_BIOMETRIC_TRUE : FirebaseMessagingHelper.USE_BIOMETRIC_FALSE);

        task = new OneTimeWorkRequest.Builder(FirebaseWorker.class)
                .setInputData(data.build())
                .setInitialDelay(DELAY, TimeUnit.SECONDS)
                .build();

    }

    private void generateFuturePushTask(String encryptedString, String signature){

        SharedPreferences prefs =getApplication().getBaseContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if(prefs==null){
            return;
        }
        String token = prefs.getString(getApplication().getBaseContext().getString(R.string.fcm_token),"");
        String title = getApplication().getBaseContext().getString(R.string.message_title);
        String body = getApplication().getBaseContext().getString(R.string.message_body);
        createTask(token, body, title, encryptedString, signature);
        updateRegistrationToPush(true);
        model.setTimerCreatedCreatedVisibility(true);
    }

    public void decryptAndVerify(String encryptedText, String signature){
        encryptionManager.decryptAndVerify(encryptedText, signature);
    }

    /************* Implementation of EncryptionManager.EncryptionManagerListener to support responses from background work *************/
    @Override
    public void onKeyCreated(final boolean isCreated){
        model.setKeyPairCreatedVisibility(Boolean.valueOf(isCreated));
    }

    @Override
    public void onEncryptFinished(boolean isSuccess) {
        if(isSuccess){
            model.setEncryptedVisibility(true);
            encryptionManager.sign(inputText.getValue());
        }else{
            model.setEncryptedVisibility(false);
            Toast.makeText(getApplication(), R.string.decrtption_failed, Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onDecryptedFinished(boolean isSuccess) {
        if(isSuccess){
            model.setDecryptedVisibility(true);
        }else {
            model.setDecryptedVisibility(false);
            Toast.makeText(getApplication(), R.string.encrtption_failed, Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onSignFinished(boolean isSuccess) {
        if(isSuccess){
            model.setSignedVisibility(true);
        }else{
            model.setSignedVisibility(false);
            Toast.makeText(getApplication(), R.string.signing_failed, Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onVerifyFinished(boolean isSuccess) {
        if(isSuccess){
            model.setVerifiedVisibility(true);
        }else{
            model.setVerifiedVisibility(false);
            Toast.makeText(getApplication(), R.string.verify_failed, Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onEncryptionSignFinished(boolean isSuccess, String encrypted, String signature){
        generateFuturePushTask(encrypted, signature);
    }

    @Override
    public void onDecryptedVerifyFinished(boolean isSuccess, String decrypted){
        if(isSuccess){
            updateTextView(decrypted);
        }else{
            Toast.makeText(getApplication(), R.string.decryption_process_failed, Toast.LENGTH_LONG);
        }
    }

}
