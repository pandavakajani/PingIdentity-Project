package com.example.encryptmystrings.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MainModel {
    //singleton pattern
    private MainModel(){}
    private static MainModel instance;
    public static MainModel getInstance(){
        if(instance == null){
            instance = new MainModel();
        }
        return instance;
    }

    //data to support push notification
    private MutableLiveData<Boolean> registerPushMessage = new MutableLiveData<>(false);
    //data to support the MainFragment
    private MutableLiveData<Boolean> toggleEncryption = new MutableLiveData<>(false);
    private MutableLiveData<String> operationStatus = new MutableLiveData<>("NOT STARTED");
    private MutableLiveData<String> inputText = new MutableLiveData<>("");
    //data to support the DecryptedFragment
    private MutableLiveData<String> textView = new MutableLiveData<>("");

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
}