package com.example.encryptmystrings.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * This class is the Model of the activity.
 * It holds the data using {@link MutableLiveData}
 * The UI is bound to this data through @{@link MainModelView}
 * To make sure no one is amending the data directly all the getter methods return {@link LiveData}
 */
public class MainModel {

    //data to support push notification
    private MutableLiveData<Boolean> registerPushMessage = new MutableLiveData<>(false);
    //data to support the MainFragment
    private MutableLiveData<Boolean> toggleEncryption = new MutableLiveData<>(false);
    private MutableLiveData<String> operationStatus = new MutableLiveData<>("NOT STARTED");
    private MutableLiveData<String> inputText = new MutableLiveData<>("");
    //data to support the DecryptedFragment
    private MutableLiveData<String> textView = new MutableLiveData<>("");
    //data to support the labels visibility
    private MutableLiveData<Boolean> keyPairCreatedVisibility = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> encryptedVisibility = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> signedVisibility = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> timerCreatedCreatedVisibility = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> verifiedVisibility = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> decryptedVisibility = new MutableLiveData<>(false);

    public LiveData<Boolean> getToggleEncryption(){
        return toggleEncryption;
    }
    public LiveData<String> getOperationStatus(){
        return operationStatus;
    }
    public LiveData<String> getInputText(){ return inputText; }
    public LiveData<String> getTextView(){
        return textView;
    }
    public LiveData<Boolean> getRegisterPush(){
        return registerPushMessage;
    }

    public void setToggleEncryption(Boolean toggleEncryption) {
        this.toggleEncryption.setValue(toggleEncryption);
    }
    public void setOperationStatus(String operationStatus) {
        this.operationStatus.setValue(operationStatus);
    }
    public void setInputText(String inputText) {
        this.inputText.setValue(inputText);
    }
    public void setTextView(String text) {
        this.textView.setValue(text);
    }
    public void setRegisterPushMessage(Boolean registerPushMessage) {
        this.registerPushMessage.setValue(registerPushMessage);
    }

    public MutableLiveData<Boolean> getKeyPairCreatedVisibility() {
        return keyPairCreatedVisibility;
    }

    public void setKeyPairCreatedVisibility(boolean keyPairCreatedVisibility) {
        this.keyPairCreatedVisibility.setValue(keyPairCreatedVisibility);
    }

    public MutableLiveData<Boolean> getEncryptedVisibility() {
        return encryptedVisibility;
    }

    public void setEncryptedVisibility(boolean encryptedVisibility) {
        this.encryptedVisibility.setValue(encryptedVisibility);
    }

    public MutableLiveData<Boolean> getSignedVisibility() {
        return signedVisibility;
    }

    public void setSignedVisibility(boolean signedVisibility) {
        this.signedVisibility.setValue(signedVisibility);
    }

    public MutableLiveData<Boolean> getTimerCreatedCreatedVisibility() {
        return timerCreatedCreatedVisibility;
    }

    public void setTimerCreatedCreatedVisibility(boolean timerCreatedCreatedVisibility) {
        this.timerCreatedCreatedVisibility.setValue(timerCreatedCreatedVisibility);
    }

    public MutableLiveData<Boolean> getVerifiedVisibility() {
        return verifiedVisibility;
    }

    public void setVerifiedVisibility(boolean verifiedVisibility) {
        this.verifiedVisibility.setValue(verifiedVisibility);
    }

    public MutableLiveData<Boolean> getDecryptedVisibility() {
        return decryptedVisibility;
    }

    public void setDecryptedVisibility(boolean decryptedVisibility) {
        this.decryptedVisibility.setValue(decryptedVisibility);
    }
}