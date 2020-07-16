package com.example.encryptmystrings.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MainModel {
    //singleton pattern
    private static MainModel instance;
    public static MainModel getInstance(){
        if(instance == null){
            instance = new MainModel();
        }
        return instance;
    }

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

    public LiveData<String> getInputText(){
        return inputText;
    }

    public LiveData<String> getTextView(){
        return textView;
    }


    public void setToggleEncryption(Boolean toggleEncryption) {
        this.toggleEncryption.postValue(toggleEncryption);
    }

    public void setOperationStatus(String operationStatus) {
        this.operationStatus.postValue(operationStatus);
    }

    public void setInputText(String inputText) {
        this.inputText.postValue(inputText);
    }

    public void setTextView(String text) {
        this.textView.postValue(text);
    }
}