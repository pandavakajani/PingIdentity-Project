package com.example.encryptmystrings.ui.main;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
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


/**
 * The main Model View of the app. Holds reference to {@link MainModel} that holds the data.
 * Responsible for the app's logic and
 * Implements {@link EncryptionManager.EncryptionManagerListener} for receiving callbacks for the progress of encryption, decryption, etc..
 *
 */
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

    /************* UI ACTIONS *****************/
    /**
     * On Send Push button clicked we initiate a process to encrypt and sign the input text and prepare a future push task
     * bind method was done in {@link com.example.encryptmystrings.R.layout#main_fragment}
     */
    public void onButtonSendClicked(View v){
        resetAllFlags();
        startEncryptAndSignProcess();
    }

    /**
     * The above methods are all data-view controls.
     * They are responsible for the labels visibility in
     * {@link com.example.encryptmystrings.R.layout#main_fragment}
     * and {@link com.example.encryptmystrings.R.layout#decryped_layout}
     * the liveDate getter methods return LiveData in order to prevent writing
     * to the live data from outside
     */

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

    public LiveData<Boolean> getKeyPairTextViewVisibility() {
        return model.getKeyPairCreatedVisibility();
    }

    public LiveData<Boolean> getEncryptedTextViewVisibility() {
        return model.getEncryptedVisibility();
    }

    public LiveData<Boolean> getSignedTextViewVisibility() {
        return model.getSignedVisibility();
    }

    public LiveData<Boolean> getTimerTextViewVisibility(){
        return model.getTimerCreatedCreatedVisibility();
    }

    public LiveData<Boolean> getVerifiedTextViewVisibility(){
        return model.getVerifiedVisibility();
    }

    public LiveData<Boolean> getDecryptedTextViewVisibility(){
        return model.getDecryptedVisibility();
    }

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

    public void onEncryptionToggled(boolean value){
        // Avoids infinite loops.
        boolean isOn = toggleEncryption.getValue();
        if (isOn != value) {
            model.setToggleEncryption(value);
            model.setOperationStatus("Toggle button is now turned " + (isOn==false? "ON" : "OFF"));
        }

    }


    /************* NAVIGATION *****************/
    /**
     * The navigation to {@link DecryptedFragment} is done here.
     * We provide the data needed in fragment in order to start the decryption process.
     * @param navController
     * @param encryptedText
     * @param signature
     */
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

    /**
     * Creates the future push task.
     * Saves it as a member.
     *
     * @param token
     * @param body
     * @param title
     * @param encryptedString
     * @param signature
     */
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

    /**
     * Collects the data for creating future push task and creates it.
     * Updating the model that we have a pending push message to shoot.
     *
     * @param encryptedString the encrypted text
     * @param signature plain text signature
     */
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


    public void updateRegistrationToPush(boolean registerToPush){
        model.setRegisterPushMessage(registerToPush);
    }

    /************* DECRYPTION/ENCRYPTION STUFF *****************/

    /**
     * Initiates the decryption and verifying of the received encrypted data.
     *
     * @param encryptedText
     * @param signature
     */
    public void decryptAndVerify(String encryptedText, String signature){
        encryptionManager.decryptAndVerify(encryptedText, signature);
    }

    /**
     * Runs through Handler.postDelayed since i wanted the user to see that the labels
     * are actually reset to invisible before the process turns them on again
     * (the process is finishing before is can be seen on screen)
     */
    private void startEncryptAndSignProcess() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                encryptionManager.encryptAndSign(inputText.getValue());
            }
        }, 500);
    }





    /************ IMPLEMENTATION OF {@link EncryptionManager.EncryptionManagerListener} ********************/

    /**
     * Support responses from background work.
     * It will update the text label with the current progress in the processes.
     */

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

    /**
     * Creates a task that will create a push message 15 seconds after we go down to the background.
     * @param isSuccess has the whole process succeeded
     * @param encrypted encrypted text
     * @param signature the plaintext signature
     */
    @Override
    public void onEncryptionSignFinished(boolean isSuccess, String encrypted, String signature){
        if(isSuccess) {
            generateFuturePushTask(encrypted, signature);
        }else{
            Toast.makeText(getApplication(), R.string.encryption_process_failed, Toast.LENGTH_LONG);
        }

    }

    /**
     * Updating the text view with the plain text in {@link com.example.encryptmystrings.R.layout#decryped_layout#textView}
     * @param isSuccess has the whole decryption process succeeded
     * @param decrypted plain text
     */
    @Override
    public void onDecryptedVerifyFinished(boolean isSuccess, String decrypted){
        if(isSuccess){
            updateTextView(decrypted);
        }else{
            Toast.makeText(getApplication(), R.string.decryption_process_failed, Toast.LENGTH_LONG);
        }
    }

}
